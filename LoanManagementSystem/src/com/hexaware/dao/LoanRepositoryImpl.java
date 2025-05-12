package com.hexaware.dao;

import com.hexaware.entity.*;
import com.hexaware.util.DBConnUtil;
import com.hexaware.exception.InvalidLoanException;

import java.sql.*;
import java.util.*;

public class LoanRepositoryImpl implements ILoanRepository {

    @Override
    public boolean applyLoan(Loan loan) {
        try (Scanner sc = new Scanner(System.in)) {
			System.out.print("Confirm loan application (Yes/No): ");
			if (!sc.nextLine().equalsIgnoreCase("Yes")) return false;
		}
        try (Connection conn = DBConnUtil.getConnection()) {
            String insertLoan = "INSERT INTO Loan VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(insertLoan);
            ps.setInt(1, loan.getLoanId());
            ps.setInt(2, loan.getCustomer().getCustomerId());
            ps.setDouble(3, loan.getPrincipalAmount());
            ps.setDouble(4, loan.getInterestRate());
            ps.setInt(5, loan.getLoanTerm());
            ps.setString(6, loan.getLoanType());
            ps.setString(7, "Pending");
            ps.executeUpdate();

            if (loan instanceof HomeLoan hl) {
                String sql = "INSERT INTO HomeLoan VALUES (?, ?, ?)";
                PreparedStatement ps2 = conn.prepareStatement(sql);
                ps2.setInt(1, hl.getLoanId());
                ps2.setString(2, hl.getPropertyAddress());
                ps2.setDouble(3, hl.getPropertyValue());
                ps2.executeUpdate();
            } else if (loan instanceof CarLoan cl) {
                String sql = "INSERT INTO CarLoan VALUES (?, ?, ?)";
                PreparedStatement ps2 = conn.prepareStatement(sql);
                ps2.setInt(1, cl.getLoanId());
                ps2.setString(2, cl.getCarModel());
                ps2.setDouble(3, cl.getCarValue());
                ps2.executeUpdate();
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public double calculateInterest(int loanId) throws InvalidLoanException {
        Loan loan = getLoanById(loanId);
        return calculateInterest(loan.getPrincipalAmount(), loan.getInterestRate(), loan.getLoanTerm());
    }

    @Override
    public double calculateInterest(double principal, double rate, int term) {
        return (principal * rate * term) / 12;
    }

    @Override
    public String loanStatus(int loanId) throws InvalidLoanException {
        try (Connection conn = DBConnUtil.getConnection()) {
            String query = "SELECT l.loan_id, c.credit_score FROM Loan l JOIN Customer c ON l.customer_id = c.customer_id WHERE l.loan_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, loanId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int creditScore = rs.getInt("credit_score");
                String status = creditScore > 650 ? "Approved" : "Rejected";

                String update = "UPDATE Loan SET loan_status = ? WHERE loan_id = ?";
                PreparedStatement updatePs = conn.prepareStatement(update);
                updatePs.setString(1, status);
                updatePs.setInt(2, loanId);
                updatePs.executeUpdate();
                return status;
            } else {
                throw new InvalidLoanException("Loan ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidLoanException("DB Error.");
        }
    }

    @Override
    public double calculateEMI(int loanId) throws InvalidLoanException {
        Loan loan = getLoanById(loanId);
        return calculateEMI(loan.getPrincipalAmount(), loan.getInterestRate(), loan.getLoanTerm());
    }

    @Override
    public double calculateEMI(double principal, double rate, int term) {
        double r = rate / (12 * 100); // Monthly interest rate
        return (principal * r * Math.pow(1 + r, term)) / (Math.pow(1 + r, term) - 1);
    }

    @Override
    public String loanRepayment(int loanId, double amount) throws InvalidLoanException {
        double emi = calculateEMI(loanId);
        if (amount < emi) return "Payment too low to cover one EMI.";

        int paidEMIs = (int) (amount / emi);
        return paidEMIs + " EMIs paid with amount " + amount;
    }

    @Override
    public List<Loan> getAllLoan() {
        List<Loan> loanList = new ArrayList<>();
        try (Connection conn = DBConnUtil.getConnection()) {
            String sql = "SELECT * FROM Loan";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int loanId = rs.getInt("loan_id");
                int customerId = rs.getInt("customer_id");
                Customer cust = new Customer(); // Simplified; better to retrieve actual customer
                cust.setCustomerId(customerId);

                Loan loan = new Loan() {}; // Anonymous since Loan is abstract
                loan.setLoanId(loanId);
                loan.setCustomer(cust);
                loan.setPrincipalAmount(rs.getDouble("principal_amount"));
                loan.setInterestRate(rs.getDouble("interest_rate"));
                loan.setLoanTerm(rs.getInt("loan_term"));
                loan.setLoanType(rs.getString("loan_type"));
                loan.setLoanStatus(rs.getString("loan_status"));
                loanList.add(loan);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loanList;
    }

    @Override
    public Loan getLoanById(int loanId) throws InvalidLoanException {
        try (Connection conn = DBConnUtil.getConnection()) {
            String query = "SELECT * FROM Loan WHERE loan_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, loanId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int custId = rs.getInt("customer_id");
                Customer customer = new Customer(); // In real use, fetch customer from DB
                customer.setCustomerId(custId);

                String type = rs.getString("loan_type");
                if ("HomeLoan".equalsIgnoreCase(type)) {
                    String hq = "SELECT * FROM HomeLoan WHERE loan_id = ?";
                    PreparedStatement hps = conn.prepareStatement(hq);
                    hps.setInt(1, loanId);
                    ResultSet hrs = hps.executeQuery();
                    if (hrs.next()) {
                        return new HomeLoan(
                            loanId, customer,
                            rs.getDouble("principal_amount"),
                            rs.getDouble("interest_rate"),
                            rs.getInt("loan_term"),
                            type,
                            rs.getString("loan_status"),
                            hrs.getString("property_address"),
                            hrs.getDouble("property_value")
                        );
                    }
                } else if ("CarLoan".equalsIgnoreCase(type)) {
                    String cq = "SELECT * FROM CarLoan WHERE loan_id = ?";
                    PreparedStatement cps = conn.prepareStatement(cq);
                    cps.setInt(1, loanId);
                    ResultSet crs = cps.executeQuery();
                    if (crs.next()) {
                        return new CarLoan(
                            loanId, customer,
                            rs.getDouble("principal_amount"),
                            rs.getDouble("interest_rate"),
                            rs.getInt("loan_term"),
                            type,
                            rs.getString("loan_status"),
                            crs.getString("car_model"),
                            crs.getDouble("car_value")
                        );
                    }
                }
            }
            throw new InvalidLoanException("Loan not found.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidLoanException("DB error.");
        }
    }
}

package com.hexaware.main;

import com.hexaware.dao.*;
import com.hexaware.entity.*;
import com.hexaware.exception.InvalidLoanException;

import java.util.Scanner;

public class LoanManagementMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ILoanRepository service = new LoanRepositoryImpl();

        while (true) {
            System.out.println("\n=== Loan Management System ===");
            System.out.println("1. Apply Loan");
            System.out.println("2. View All Loans");
            System.out.println("3. Get Loan by ID");
            System.out.println("4. Calculate Interest");
            System.out.println("5. Calculate EMI");
            System.out.println("6. Repay Loan");
            System.out.println("7. Check Loan Status");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter Loan ID: ");
                        int loanId = scanner.nextInt();
                        scanner.nextLine();

                        System.out.print("Enter Customer ID: ");
                        int customerId = scanner.nextInt();
                        scanner.nextLine();

                        System.out.print("Enter Principal Amount: ");
                        double principal = scanner.nextDouble();
                        scanner.nextLine();

                        System.out.print("Enter Interest Rate: ");
                        double rate = scanner.nextDouble();
                        scanner.nextLine();

                        System.out.print("Enter Loan Term (months): ");
                        int term = scanner.nextInt();
                        scanner.nextLine();

                        System.out.print("Loan Type (HomeLoan/CarLoan): ");
                        String type = scanner.nextLine();

                        Customer customer = new Customer();
                        customer.setCustomerId(customerId);

                        if (type.equalsIgnoreCase("HomeLoan")) {
                            System.out.print("Enter Property Address: ");
                            String address = scanner.nextLine();

                            System.out.print("Enter Property Value: ");
                            double propertyValue = scanner.nextDouble();
                            scanner.nextLine();

                            HomeLoan homeLoan = new HomeLoan(loanId, customer, principal, rate, term, "HomeLoan", "Pending", address, propertyValue);
                            service.applyLoan(homeLoan);
                            System.out.println("✅ Home loan applied successfully.");

                        } else if (type.equalsIgnoreCase("CarLoan")) {
                            System.out.print("Enter Car Model: ");
                            String model = scanner.nextLine();

                            System.out.print("Enter Car Value: ");
                            double carValue = scanner.nextDouble();
                            scanner.nextLine();

                            CarLoan carLoan = new CarLoan(loanId, customer, principal, rate, term, "CarLoan", "Pending", model, carValue);
                            service.applyLoan(carLoan);
                            System.out.println("✅ Car loan applied successfully.");

                        } else {
                            System.out.println("❌ Invalid loan type entered.");
                        }
                        break;

                    case 2:
                        service.getAllLoan().forEach(System.out::println);
                        break;

                    case 3:
                        System.out.print("Enter Loan ID: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println(service.getLoanById(id));
                        break;

                    case 4:
                        System.out.print("Enter Loan ID: ");
                        int interestId = scanner.nextInt();
                        scanner.nextLine();
                        double interest = service.calculateInterest(interestId);
                        System.out.println("Interest Amount: " + interest);
                        break;

                    case 5:
                        System.out.print("Enter Loan ID: ");
                        int emiId = scanner.nextInt();
                        scanner.nextLine();
                        double emi = service.calculateEMI(emiId);
                        System.out.println("Monthly EMI: " + emi);
                        break;

                    case 6:
                        System.out.print("Enter Loan ID: ");
                        int repayId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Enter repayment amount: ");
                        double amount = scanner.nextDouble();
                        scanner.nextLine();
                        String result = service.loanRepayment(repayId, amount);
                        System.out.println(result);
                        break;

                    case 7:
                        System.out.print("Enter Loan ID: ");
                        int statusId = scanner.nextInt();
                        scanner.nextLine();
                        String status = service.loanStatus(statusId);
                        System.out.println("Loan Status: " + status);
                        break;

                    case 8:
                        System.out.println("✅ Exiting Loan Management System. Goodbye!");
                        scanner.close();
                        return;

                    default:
                        System.out.println("❌ Invalid choice. Try again.");
                }
            } catch (InvalidLoanException e) {
                System.out.println("❌ Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter numeric values where required.");
            } catch (Exception e) {
                System.out.println("❌ Unexpected error: " + e.getMessage());
            }
        }
    }
}

package com.hexaware.entity;

public class HomeLoan extends Loan {
    private String propertyAddress;
    private double propertyValue;

    public HomeLoan() {}

    public HomeLoan(int loanId, Customer customer, double principalAmount, double interestRate,
                    int loanTerm, String loanType, String loanStatus,
                    String propertyAddress, double propertyValue) {
        super(loanId, customer, principalAmount, interestRate, loanTerm, loanType, loanStatus);
        this.propertyAddress = propertyAddress;
        this.propertyValue = propertyValue;
    }

    // Getters and Setters
    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }

    public double getPropertyValue() { return propertyValue; }
    public void setPropertyValue(double propertyValue) { this.propertyValue = propertyValue; }

    @Override
    public String toString() {
        return super.toString() + ", Property Address: " + propertyAddress + ", Property Value: " + propertyValue;
    }
}

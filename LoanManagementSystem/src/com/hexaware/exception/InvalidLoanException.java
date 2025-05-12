package com.hexaware.exception;

public class InvalidLoanException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidLoanException() {
        super("Invalid Loan: Loan not found or incorrect data provided.");
    }

    public InvalidLoanException(String message) {
        super(message);
    }
}

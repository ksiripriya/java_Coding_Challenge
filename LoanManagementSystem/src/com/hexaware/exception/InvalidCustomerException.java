package com.hexaware.exception;

public class InvalidCustomerException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidCustomerException() {
        super("Invalid Customer: Customer not found or data incorrect.");
    }

    public InvalidCustomerException(String message) {
        super(message);
    }
}


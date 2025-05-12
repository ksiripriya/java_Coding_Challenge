CREATE database Loan_Management;
Use Loan_Management;

CREATE TABLE Customer (
    customer_id INT PRIMARY KEY,
    name VARCHAR(100),
    email_address VARCHAR(100),
    phone_number VARCHAR(15),
    address TEXT,
    credit_score INT
);

CREATE TABLE Loan (
    loan_id INT PRIMARY KEY,
    customer_id INT,
    principal_amount DECIMAL(15, 2),
    interest_rate DECIMAL(5, 2),
    loan_term INT,
    loan_type VARCHAR(20),
    loan_status VARCHAR(20),
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE HomeLoan (
    loan_id INT PRIMARY KEY,
    property_address TEXT,
    property_value DECIMAL(15, 2),
    CONSTRAINT fk_loan FOREIGN KEY (loan_id) REFERENCES Loan(loan_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE CarLoan (
    loan_id INT PRIMARY KEY,
    car_model VARCHAR(100),
    car_value DECIMAL(15, 2),
    CONSTRAINT fk_loan2 FOREIGN KEY (loan_id) REFERENCES Loan(loan_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

INSERT INTO Customer (customer_id, name, email_address, phone_number, address, credit_score)
VALUES
(1, 'Alice Smith', 'alice@example.com', '1234567890', '123 Elm Street', 720),
(2, 'Bob Johnson', 'bob@example.com', '2345678901', '456 Oak Avenue', 630),
(3, 'Charlie Davis', 'charlie@example.com', '3456789012', '789 Pine Lane', 680),
(4, 'Diana Brown', 'diana@example.com', '4567890123', '101 Maple Blvd', 590),
(5, 'Ethan Clark', 'ethan@example.com', '5678901234', '202 Birch Road', 700);




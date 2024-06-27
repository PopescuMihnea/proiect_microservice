package com.example.transaction.error;

public class TransactionNotFoundError extends RuntimeException {
    public TransactionNotFoundError(String message)
    {
        super(message);
    }
}

package com.example.card.error;

public class CardNotFoundError extends RuntimeException{

    public CardNotFoundError(String message)
    {
        super(message);
    }
}

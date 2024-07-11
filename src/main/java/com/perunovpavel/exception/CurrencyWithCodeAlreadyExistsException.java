package com.perunovpavel.exception;

public class CurrencyWithCodeAlreadyExistsException extends RuntimeException{

    public CurrencyWithCodeAlreadyExistsException(String message) {
        super(message);
    }
}

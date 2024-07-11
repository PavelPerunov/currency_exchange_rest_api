package com.perunovpavel.exception;

public class CurrencyPairIsNotInDataBaseException extends RuntimeException{
    public CurrencyPairIsNotInDataBaseException(String message) {
        super(message);
    }
}

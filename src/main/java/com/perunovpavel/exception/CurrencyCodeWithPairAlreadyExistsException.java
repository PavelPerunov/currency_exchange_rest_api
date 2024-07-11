package com.perunovpavel.exception;

public class CurrencyCodeWithPairAlreadyExistsException extends RuntimeException{

    public CurrencyCodeWithPairAlreadyExistsException(String message) {
        super(message);
    }

}

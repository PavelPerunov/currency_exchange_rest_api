package com.perunovpavel.exception;

public class CurrencyCodeOfPairMissingInAddressException extends RuntimeException{
    public CurrencyCodeOfPairMissingInAddressException(String message) {
        super(message);
    }
}

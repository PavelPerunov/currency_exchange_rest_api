package com.perunovpavel.exception;

public class CurrencyCodeMissingFromAddressException extends RuntimeException{
    public CurrencyCodeMissingFromAddressException(String message) {
        super(message);
    }
}

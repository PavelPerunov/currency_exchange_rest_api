package com.perunovpavel.exception;

public class OneOrBothCurrenciesFromPairNotExistException extends  RuntimeException{
    public OneOrBothCurrenciesFromPairNotExistException(String message) {
        super(message);
    }
}

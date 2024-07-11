package com.perunovpavel.exception;

public class RequiredFormFieldMissingException extends RuntimeException{
    public RequiredFormFieldMissingException(String message) {
        super(message);
    }
}

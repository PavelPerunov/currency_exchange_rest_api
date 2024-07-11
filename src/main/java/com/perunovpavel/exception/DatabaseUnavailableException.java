package com.perunovpavel.exception;

public class DatabaseUnavailableException extends RuntimeException {

    public DatabaseUnavailableException(String massage) {
        super(massage);
    }

}

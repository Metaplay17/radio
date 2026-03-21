package org.example.exceptions;

public class IncorrectArgumentGivenException extends RuntimeException {
    public IncorrectArgumentGivenException(String message) {
        super(message);
    }
}

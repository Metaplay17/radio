package org.example.exceptions;

public class LicenceExpiredException extends RuntimeException {
    public LicenceExpiredException(String message) {
        super(message);
    }
}

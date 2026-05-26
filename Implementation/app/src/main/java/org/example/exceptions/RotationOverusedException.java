package org.example.exceptions;

public class RotationOverusedException extends RuntimeException {
    public RotationOverusedException(String message) {
        super(message);
    }
}

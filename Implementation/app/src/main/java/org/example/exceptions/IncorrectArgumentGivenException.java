package org.example.exceptions;

public class IncorrectArgumentGivenException extends RuntimeException {
    public IncorrectArgumentGivenException(String methodName, String argumentName, String problem) {
        super("В методе " + methodName + " аргумент " + argumentName + " " + problem);
    }
}

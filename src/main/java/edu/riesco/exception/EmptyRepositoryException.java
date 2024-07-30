package edu.riesco.exception;

public class EmptyRepositoryException extends RuntimeException {
    public EmptyRepositoryException(String message) {
        super(message);
    }
}

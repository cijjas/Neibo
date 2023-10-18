package ar.edu.itba.paw.interfaces.exceptions;

public class DuplicateKeyException extends RuntimeException {
    public DuplicateKeyException(String message) {
        super(message);
    }
}

package ar.edu.itba.paw.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException() {
        super("The requested resource was not found.");
    }
}

package ar.edu.itba.paw.webapp.exceptions;

public class ResourceNotFoundException extends NeiboException{
    public ResourceNotFoundException() {
        super("Resource Not Found");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

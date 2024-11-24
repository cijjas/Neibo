package ar.edu.itba.paw.webapp.security.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidTokenTypeException extends AuthenticationException {

    public InvalidTokenTypeException(String message) {
        super(message);
    }

    public InvalidTokenTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}

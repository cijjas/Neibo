package ar.edu.itba.paw.webapp.security.exception;

import org.springframework.security.core.AuthenticationException;

public class ExpiredTokenException extends AuthenticationException {
    public ExpiredTokenException(String message) {
        super(message);
    }

    public ExpiredTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
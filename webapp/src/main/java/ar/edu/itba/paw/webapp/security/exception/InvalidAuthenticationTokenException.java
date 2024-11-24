package ar.edu.itba.paw.webapp.security.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidAuthenticationTokenException extends AuthenticationException {

    public InvalidAuthenticationTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}

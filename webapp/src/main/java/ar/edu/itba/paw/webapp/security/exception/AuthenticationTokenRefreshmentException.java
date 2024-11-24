package ar.edu.itba.paw.webapp.security.exception;

public class AuthenticationTokenRefreshmentException extends RuntimeException {

    public AuthenticationTokenRefreshmentException(String message) {
        super(message);
    }

    public AuthenticationTokenRefreshmentException(String message, Throwable cause) {
        super(message, cause);
    }
}

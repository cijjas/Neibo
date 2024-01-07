package ar.edu.itba.paw.webapp.security.api.model;

/**
 * API model for an authentication token.
 *
 * @author cassiomolin
 */
public class AuthenticationToken {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
package ar.edu.itba.paw.webapp.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserAuth extends User {
    private final long userId;
    private final long neighborhoodId;

    public UserAuth(String username, String password, Collection<? extends GrantedAuthority> authorities, long userId, long neighborhoodId) {
        super(username, password, authorities);
        this.userId = userId;
        this.neighborhoodId = neighborhoodId;
    }

    public long getUserId() {
        return userId;
    }

    public long getNeighborhoodId() {
        return neighborhoodId;
    }
}


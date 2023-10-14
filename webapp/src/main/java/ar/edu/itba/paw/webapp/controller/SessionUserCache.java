package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.User;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionUserCache {

    private User cachedUser;

    public User getCachedUser() {
        return cachedUser;
    }

    public void setCachedUser(User user) {
        this.cachedUser = user;
    }

    public void clearCache() {
        this.cachedUser = null;
    }
}

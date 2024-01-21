package ar.edu.itba.paw.webapp.security.service.impl;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.webapp.security.api.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Default implementation for the {@link UserDetailsService}.
 *
 * @author cassiomolin
 */
@Service
public class DefaultUserDetailsService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUserDetailsService.class);

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        LOGGER.info("Loading user with mail {}", mail);

        User user = userService.findUser(mail)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("No user found with mail '%s'.", mail)));

        Set<GrantedAuthority> authorities = new HashSet<>();

        // todo change this so it understands all our roles
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return new AuthenticatedUserDetails.Builder()
                .withUsername(user.getMail())
                .withPassword(user.getPassword())
                .withAuthorities(authorities)
                .build();
    }
}
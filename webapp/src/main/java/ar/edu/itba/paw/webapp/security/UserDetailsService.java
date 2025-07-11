package ar.edu.itba.paw.webapp.security;

import ar.edu.itba.paw.enums.Authority;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsService.class);

    private final UserService us;

    @Autowired
    public UserDetailsService(final @Lazy UserService us) {
        this.us = us;
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws NotFoundException {
        LOGGER.debug("Loading user with mail {}", mail);

        final User n = us.findUser(mail).orElseThrow(NotFoundException::new);
        final Set<GrantedAuthority> authorities = new HashSet<>();

        // Add roles based on user data from the database
        switch (n.getRole()) {
            case ADMINISTRATOR:
                authorities.add(new SimpleGrantedAuthority(Authority.ROLE_ADMINISTRATOR.name()));
                break;
            case UNVERIFIED_NEIGHBOR:
                authorities.add(new SimpleGrantedAuthority(Authority.ROLE_UNVERIFIED_NEIGHBOR.name()));
                break;
            case NEIGHBOR:
                authorities.add(new SimpleGrantedAuthority(Authority.ROLE_NEIGHBOR.name()));
                break;
            case WORKER:
                authorities.add(new SimpleGrantedAuthority(Authority.ROLE_WORKER.name()));
                break;
            case REJECTED:
                authorities.add(new SimpleGrantedAuthority(Authority.ROLE_REJECTED.name()));
                break;
            case SUPER_ADMINISTRATOR:
                authorities.add(new SimpleGrantedAuthority(Authority.ROLE_SUPER_ADMINISTRATOR.name()));
                break;
            default:
                break;
        }

        return new UserAuth(n.getMail(), n.getPassword(), authorities, n.getUserId(), n.getNeighborhood().getNeighborhoodId());
    }

}

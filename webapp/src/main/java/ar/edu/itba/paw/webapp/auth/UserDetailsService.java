package ar.edu.itba.paw.webapp.auth;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserService us;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserDetailsService(final UserService us, final PasswordEncoder passwordEncoder){
        this.us = us;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws NotFoundException {
        final User n = us.findUserByMail(mail).orElseThrow(() -> new NotFoundException("User not found"));
        final Set<GrantedAuthority> authorities = new HashSet<>();

        // Add roles based on user data from the database
        switch (n.getRole()) {
            case ADMINISTRATOR:
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
                break;
            case UNVERIFIED_NEIGHBOR:
                authorities.add(new SimpleGrantedAuthority("ROLE_UNVERIFIED_NEIGHBOR"));
                break;
            case NEIGHBOR:
                authorities.add(new SimpleGrantedAuthority("ROLE_NEIGHBOR"));
                break;
            default:
                // Handle unknown roles or add a default role
                break;
        }

        return new UserAuth(n.getMail(), n.getPassword(), authorities);
    }

}

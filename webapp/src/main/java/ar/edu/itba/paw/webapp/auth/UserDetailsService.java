package ar.edu.itba.paw.webapp.auth;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.NeighborNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import enums.UserRole;
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
    public UserDetails loadUserByUsername(String mail) throws NeighborNotFoundException {
        final User n = us.findUserByMail(mail).orElseThrow(NeighborNotFoundException::new);

        final Set<GrantedAuthority> authorities = new HashSet<>();

        if (n.getRole() == UserRole.ADMINISTRATOR){
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new UserAuth(n.getMail(), n.getPassword(), authorities); // which information is stored in the session
    }
}

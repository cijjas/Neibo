package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.NeighborService;
import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.webapp.exceptions.NeighborNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.NeighborhoodNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class NeighborDetailsService implements UserDetailsService {
    private final NeighborService ns;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public NeighborDetailsService(final NeighborService ns, final PasswordEncoder passwordEncoder){
        this.ns = ns;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws NeighborNotFoundException {
        final Neighbor n = ns.findNeighborByMail(mail).orElseThrow(NeighborNotFoundException::new);

        // Neighbor belongs to an early version where password was not required
        if ( n.getPassword() == null ) {
            ns.setDefaultValues(n.getNeighborId());
            String newPassword = passwordEncoder.encode(n.getName()+n.getSurname());
            ns.setNewPassword(n.getNeighborId(), newPassword);

            final Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            return new NeighborAuth(n.getMail(), newPassword, authorities);
        }

        final Set<GrantedAuthority> authorities = new HashSet<>();


        /*
        * if (inAdminMais)
        *   give ADMIN USER
        *   return
        * */

        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new NeighborAuth(n.getMail(), n.getPassword(), authorities); // which information is stored in the session
    }
}

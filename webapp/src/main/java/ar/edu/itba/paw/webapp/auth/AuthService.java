package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.webapp.controller.NeighborhoodController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private ProductService ps;

    @Autowired
    private UserService us;

    public boolean canAccessProduct(UserDetails principal, Long productId) {
        if (principal == null) {
            return false;
        }

        Optional<Product> optionalProduct = ps.findProduct(productId);
        if (!optionalProduct.isPresent()) {
            return false;
        }

        Optional<User> optionalUser = us.findUser(principal.getUsername());
        if (!optionalUser.isPresent()) {
            return false;
        }

        User user = optionalUser.get();
        Product product = optionalProduct.get();

        return user.getUserId().equals(product.getSeller().getUserId());
    }

    public boolean hasAccess(Long workerId) {
        System.out.println("Neighborhood QP Heavenly Bind");
        LOGGER.info("Verifying Query Params Accessibility");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            if (workerId != null) {
                return authentication.getAuthorities().stream()
                        .noneMatch(authority -> authority.getAuthority().equals("ROLE_UNVERIFIED_NEIGHBOR") || authority.getAuthority().equals("ROLE_REJECTED"));
            } else {
                return true;
            }
        }

        return workerId == null;
    }



}

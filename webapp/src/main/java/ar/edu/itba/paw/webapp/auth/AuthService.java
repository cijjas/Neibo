package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.Entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

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
}

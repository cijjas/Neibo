package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product createProduct(String userURN, String name, String description, Double price, boolean used, String departmentURN, String[] imageURNs, long units);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Product> findProduct(final long productId);

    Optional<Product> findProduct(final long productId, long neighborhoodId);

    List<Product> getProducts(long neighborhoodId, String departmentURN, String userURN, String productStatusURN, int page, int size);

    int countProducts(long neighborhoodId, String departmentURN, String userURN, String productStatusURN);

    int calculateProductPages(long neighborhoodId, int size, String departmentURN, String userURN, String productStatusURN);

    // -----------------------------------------------------------------------------------------------------------------

    Product updateProductPartially(long productId, String name, String description, Double price, boolean used, String departmentURN, String[] imageURNs, Long stock);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteProduct(final long productId);
}

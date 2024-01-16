package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product createProduct(long userId, String name, String description, String price, boolean used, long departmentId, MultipartFile[] pictureFiles, long units);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Product> findProduct(final long productId);

    List<Product> getProducts(long neighborhoodId, String department, Long userId, String productStatus, int page, int size);

    int countProducts(long neighborhoodId, String department, Long userId, String productStatus);

    int calculateProductPages(long neighborhoodId, int size, String department, Long userId, String productStatus);

    // -----------------------------------------------------------------------------------------------------------------

    Product updateProductPartially(long productId, String name, String description, String price, boolean used, long departmentId, MultipartFile[] pictureFiles, Long stock);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteProduct(final long productId);
}

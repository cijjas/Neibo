package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.ProductStatus;
import ar.edu.itba.paw.models.Entities.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product createProduct(long userId, String name, String description, String price, boolean used, long departmentId, MultipartFile[] pictureFiles, long units);

    // -----------------------------------------------------------------------------------------------------------------

    int getProductsTotalPages(long neighborhoodId, int size, String department, long userId, String productStatus);

    Optional<Product> findProductById(final long productId);


    List<Product> getProductsByCriteria(long neighborhoodId, String department, long userId, String productStatus, int page, int size);

    int getProductsCountByCriteria(long neighborhoodId, String department, long userId, String productStatus);

    // -----------------------------------------------------------------------------------------------------------------

    void markAsBought(long buyerId, long productId, long units);

    void updateProduct(long productId, String name, String description, String price, boolean used, long departmentId, MultipartFile[] pictureFiles, Long stock) ;

    Product updateProductPartially(long productId, String name, String description, String price, boolean used, long departmentId, MultipartFile[] pictureFiles, Long stock);

    void restockProduct(long productId, long extraUnits);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteProduct(final long productId);
}

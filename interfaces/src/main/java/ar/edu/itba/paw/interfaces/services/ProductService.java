package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.models.MainEntities.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product createProduct(long userId, String name, String description, String price, boolean used, long departmentId, MultipartFile primaryPictureFile, MultipartFile secondaryPictureFile, MultipartFile tertiaryPictureFile);

    void updateProduct(long productId, String name, String description, String price, boolean used, long departmentId, MultipartFile primaryPictureFile, MultipartFile secondaryPictureFile, MultipartFile tertiaryPictureFile) ;

    boolean deleteProduct(final long productId);

    Optional<Product> findProductById(final long productId);

    List<Product> getProductsByCriteria(long neighborhoodId, Department department, int page, int size);

    int getProductsCountByCriteria(long neighborhoodId, Department department) ;

    List<Product> getProductsSelling(long userId);

    List<Product> getProductsSold(long userId);

    boolean markAsBought(long buyerId, long productId);
}

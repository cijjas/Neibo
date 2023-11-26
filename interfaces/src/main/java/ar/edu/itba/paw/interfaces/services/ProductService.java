package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.models.MainEntities.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product createProduct(long userId, String name, String description, String price, boolean used, long departmentId, MultipartFile[] pictureFiles, long units);

    void updateProduct(long productId, String name, String description, String price, boolean used, long departmentId, MultipartFile[] pictureFiles, Long stock) ;

    boolean deleteProduct(final long productId);

    Optional<Product> findProductById(final long productId);

    List<Product> getProductsByCriteria(long neighborhoodId, Department department, int page, int size);

    int getProductsCountByCriteria(long neighborhoodId, Department department);

    int getProductsSellingCount(long userId);

    int getProductsSoldCount(long userId);

    int getProductsBoughtCount(long userId);

    List<Product> getProductsSelling(long userId, int page, int size);

    List<Product> getProductsSold(long userId, int page, int size);

    List<Product> getProductsBought(long userId, int page, int size);

    void markAsBought(long buyerId, long productId, long units);

    int getProductsTotalPages(long neighborhoodId, int size, Department department);

    int getProductsSellingTotalPages(long userId, int size);

    int getProductsSoldTotalPages(long userId, int size);

    int getProductsBoughtTotalPages(long userId, int size);

    void restockProduct(long productId, long extraUnits);
}

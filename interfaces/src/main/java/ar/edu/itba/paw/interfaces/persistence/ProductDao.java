package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.ProductStatus;
import ar.edu.itba.paw.models.MainEntities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {

    // --------------------------------------------- PRODUCTS INSERT ---------------------------------------------------

    Product createProduct(long userId, String name, String description, double price, boolean used, long departmentId, Long primaryPictureId, Long secondaryPictureId, Long tertiaryPictureId, Long units);

    // --------------------------------------------- PRODUCTS SELECT ---------------------------------------------------

    Optional<Product> findProductById(final long productId);

    List<Product> getProductsByCriteria(long neighborhoodId, Department department, int page, int size);

    int getProductsCountByCriteria(long neighborhoodId, Department department);

    int getProductsSellingCount(long userId);

    int getProductsSoldCount(long userId);

    int getProductsBoughtCount(long userId);

    List<Product> getProductsSelling(long userId, int page, int size);

    List<Product> getProductsSold(long userId, int page, int size);

    List<Product> getProductsBought(long userId, int page, int size);

    // --------------------------------------------- PRODUCTS UPDATE ---------------------------------------------------

    Product updateProduct(final long productId, final String name, final String description, final double price, final boolean used, final long departmentId,
                          final Long primaryPictureId, final Long secondaryPictureId, final Long tertiaryPictureId, Long stock);

    // --------------------------------------------- PRODUCTS DELETE ---------------------------------------------------

    boolean deleteProduct(final long productId);
}

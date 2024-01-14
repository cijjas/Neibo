package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.ProductStatus;
import ar.edu.itba.paw.models.Entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {

    // --------------------------------------------- PRODUCTS INSERT ---------------------------------------------------

    Product createProduct(long userId, String name, String description, double price, boolean used, long departmentId, Long primaryPictureId, Long secondaryPictureId, Long tertiaryPictureId, Long units);

    // --------------------------------------------- PRODUCTS SELECT ---------------------------------------------------

    Optional<Product> findProductById(final long productId);

    List<Product> getProductsByCriteria(long neighborhoodId, String department, long userId, String productStatus, int page, int size);

    int getProductsCountByCriteria(long neighborhoodId, String department, long userId, String productStatus);

    // --------------------------------------------- PRODUCTS DELETE ---------------------------------------------------

    boolean deleteProduct(final long productId);
}

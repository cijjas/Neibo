package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {

    // --------------------------------------------- PRODUCTS INSERT ---------------------------------------------------

    Product createProduct(long userId, String name, String description, double price, Long units, boolean used, long departmentId, Long primaryPictureId, Long secondaryPictureId, Long tertiaryPictureId);

    // --------------------------------------------- PRODUCTS SELECT ---------------------------------------------------

    Optional<Product> findProduct(long neighborhoodId, long productId);

    Optional<Product> findProduct(long productId);

    List<Product> getProducts(long neighborhoodId, Long userId, Long departmentId, Long productStatusId, int page, int size);

    int countProducts(long neighborhoodId, Long userId, Long departmentId, Long productStatusId);

    // --------------------------------------------- PRODUCTS DELETE ---------------------------------------------------

    boolean deleteProduct(long neighborhoodId, long productId);
}

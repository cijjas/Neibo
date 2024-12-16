package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {

    // --------------------------------------------- PRODUCTS INSERT ---------------------------------------------------

    Product createProduct(long userId, String name, String description, double price, boolean used, long departmentId, Long primaryPictureId, Long secondaryPictureId, Long tertiaryPictureId, Long units);

    // --------------------------------------------- PRODUCTS SELECT ---------------------------------------------------

    Optional<Product> findProduct(final long productId);

    Optional<Product> findProduct(final long productId, long neighborhoodId);

    List<Product> getProducts(long neighborhoodId, Long departmentId, Long userId, Long productStatusId, int page, int size);

    int countProducts(long neighborhoodId, Long departmentId, Long userId, Long productStatusId);

    // --------------------------------------------- PRODUCTS DELETE ---------------------------------------------------

    boolean deleteProduct(final long neighborhoodId, final long productId);
}

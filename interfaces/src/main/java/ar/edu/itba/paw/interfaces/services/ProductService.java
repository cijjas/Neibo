package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Product createProduct(long userId, String name, String description, Double price, boolean used, long departmentId, List<Long> imageIds, long units);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Product> findProduct(final long productId);

    Optional<Product> findProduct(final long productId, long neighborhoodId);

    List<Product> getProducts(long neighborhoodId, Long departmentId, Long userId, Long productStatusId, int page, int size);

    // ---------------------------------------------------

    int calculateProductPages(long neighborhoodId, int size, Long departmentId, Long userId, Long productStatusId);

    // -----------------------------------------------------------------------------------------------------------------

    Product updateProductPartially(long productId, String name, String description, Double price, Boolean used, Long departmentId, List<Long> imageIds, Long stock);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteProduct(final long productId);
}

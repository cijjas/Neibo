package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Product createProduct(long userId, String name, String description, Double price, long units, boolean used, long departmentId, List<Long> imageIds);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Product> findProduct(long neighborhoodId, long productId);

    List<Product> getProducts(long neighborhoodId, Long userId, Long departmentId, Long productStatusId, int page, int size);

    int calculateProductPages(long neighborhoodId, Long userId, Long departmentId, Long productStatusId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Product updateProduct(long neighborhoodId, long productId, String name, String description, Double price, Long units, Boolean used, Long departmentId, List<Long> imageIds);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteProduct(long neighborhoodId, long productId);
}

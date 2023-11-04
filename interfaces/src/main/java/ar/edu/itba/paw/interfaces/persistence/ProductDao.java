package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.SearchVariant;
import ar.edu.itba.paw.models.MainEntities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {
    Product createProduct(final long userId, final String name, final String description, final double price, final boolean used, final long departmentId,
                          final Long primaryPictureId, final Long secondaryPictureId, final Long tertiaryPictureId);

    Product updateProduct(final long productId, final String name, final String description, final double price, final boolean used, final long departmentId,
                          final Long primaryPictureId, final Long secondaryPictureId, final Long tertiaryPictureId);

    boolean deleteProduct(final long productId);

    Optional<Product> findProductById(final long productId);

    List<Product> getProductsByCriteria(long neighborhoodId, Department department, int page, int size);

    int getProductsCountByCriteria(long neighborhoodId, Department department);

    List<Product> getProductsSelling(long userId);

    List<Product> getProductsSold(long userId);

    boolean markAsBought(long buyerId, long productId);

    List<Product> searchProductsByName(long userId, long neighborhoodId, String searchQuery, SearchVariant searchVariant, int page, int size);

    List<Product> searchInAllProductsBeingSold(long neighborhoodId, String searchQuery, int page, int size);
}

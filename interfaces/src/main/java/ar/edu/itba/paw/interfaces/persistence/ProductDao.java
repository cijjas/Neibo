package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.MainEntities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {
    Product createProduct(final long userId, final String name, final String description, final double price, final boolean used,
                          final Long primaryPictureId, final Long secondaryPictureId, final Long tertiaryPictureId);

    void updateProduct(final long productId, final String name, final String description, final double price, final boolean used,
                          final Long primaryPictureId, final Long secondaryPictureId, final Long tertiaryPictureId);

    boolean deleteProduct(final long productId);

    Optional<Product> findProductById(final long productId);

    List<Product> getProductsByCriteria(long neighborhoodId, List<String> departments, int page, int size);

    int getProductsCountByCriteria(long neighborhoodId, List<String> departments);

    List<Product> getProductsSelling(long userId);
    List<Product> getProductsSold(long userId);
    boolean markAsBought(long buyerId, long productId);
}

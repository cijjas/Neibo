package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.MainEntities.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);
    private final ProductDao productDao;

    @Autowired
    public ProductServiceImpl(final ProductDao productDao) {
        this.productDao = productDao;
    }
    @Override
    public Product createProduct(long userId, String name, String description, double price, boolean used, Long primaryPictureId, Long secondaryPictureId, Long tertiaryPictureId) {
        LOGGER.info("User {} Creating Product {}", userId, name);
        return productDao.createProduct(userId, name, description, price, used, primaryPictureId, secondaryPictureId, tertiaryPictureId);
    }

    @Override
    public void updateProduct(long productId, String name, String description, double price, boolean used, Long primaryPictureId, Long secondaryPictureId, Long tertiaryPictureId) {
        LOGGER.info("Updating Product {}", productId);
        productDao.updateProduct(productId, name, description, price, used, primaryPictureId, secondaryPictureId, tertiaryPictureId);
    }

    @Override
    public boolean deleteProduct(long productId) {
        LOGGER.info("Deleting Product {}", productId);
        return productDao.deleteProduct(productId);
    }

    @Override
    public Optional<Product> findProductById(long productId) {
        LOGGER.info("Selecting Product with id {}", productId);
        return productDao.findProductById(productId);
    }

    @Override
    public List<Product> getProductsByCriteria(long neighborhoodId, List<String> departments, int page, int size) {
        LOGGER.info("Selecting Products by neighborhood {} and departments {}", neighborhoodId, departments);
        return productDao.getProductsByCriteria(neighborhoodId, departments, page, size);
    }

    @Override
    public int getProductsCountByCriteria(long neighborhoodId, List<String> departments) {
        LOGGER.info("Counting Products by neighborhood {} and departments {}", neighborhoodId, departments);
        return productDao.getProductsCountByCriteria(neighborhoodId, departments);
    }

    @Override
    public List<Product> getProductsSelling(long userId) {
        LOGGER.info("Selecting Products Selling by user {}", userId);
        return productDao.getProductsSelling(userId);
    }

    @Override
    public List<Product> getProductsSold(long userId) {
        LOGGER.info("Selecting Products Sold by user {}", userId);
        return productDao.getProductsSold(userId);
    }

    @Override
    public boolean markAsBought(long buyerId, long productId) {
        LOGGER.info("Marking Product {} as bought by user {}", productId, buyerId);
        return productDao.markAsBought(buyerId, productId);
    }
}

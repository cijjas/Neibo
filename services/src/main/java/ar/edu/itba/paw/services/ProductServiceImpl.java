package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Departments;
import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.MainEntities.Image;
import ar.edu.itba.paw.models.MainEntities.Post;
import ar.edu.itba.paw.models.MainEntities.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);
    private final ProductDao productDao;
    private final ImageService imageService;


    @Autowired
    public ProductServiceImpl(final ProductDao productDao, final ImageService imageService) {
        this.productDao = productDao;
        this.imageService = imageService;
    }
    @Override
    public Product createProduct(long userId, String name, String description, double price, boolean used, long departmentId, MultipartFile primaryPictureFile, MultipartFile secondaryPictureFile, MultipartFile tertiaryPictureFile) {
        LOGGER.info("User {} Creating Product {}", userId, name);

        return productDao.createProduct(userId, name, description, price, used, departmentId, getImageId(primaryPictureFile), getImageId(secondaryPictureFile), getImageId(tertiaryPictureFile));
    }

    private Long getImageId(MultipartFile imageFile) {
        Image i = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            i = imageService.storeImage(imageFile);
        }
        return i == null ? 0 : i.getImageId();
    }

    @Override
    public void updateProduct(long productId, String name, String description, double price, boolean used, long departmentId, MultipartFile primaryPictureFile, MultipartFile secondaryPictureFile, MultipartFile tertiaryPictureFile) {
        LOGGER.info("Updating Product {}", productId);
        productDao.updateProduct(productId, name, description, price, used, departmentId, getImageId(primaryPictureFile), getImageId(secondaryPictureFile), getImageId(tertiaryPictureFile));
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
    public List<Product> getProductsByCriteria(long neighborhoodId, Departments department, int page, int size) {
        LOGGER.info("Selecting Products by neighborhood {} and departments {}", neighborhoodId, department);
        return productDao.getProductsByCriteria(neighborhoodId, department, page, size);
    }

    @Override
    public int getProductsCountByCriteria(long neighborhoodId, Departments department) {
        LOGGER.info("Counting Products by neighborhood {} and departments {}", neighborhoodId, department);
        return productDao.getProductsCountByCriteria(neighborhoodId, department);
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

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.SearchVariant;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.ConsoleHandler;

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
    public Product createProduct(long userId, String name, String description, String price, boolean used, long departmentId, MultipartFile[] pictureFiles) {
        LOGGER.info("User {} Creating Product {}", userId, name);
        double priceDouble = Double.parseDouble(price.replace("$", "").replace(",", ""));
        Long[] idArray = {0L, 0L, 0L};
        for(int i = 0; i < pictureFiles.length; i++){
            idArray[i] = getImageId(pictureFiles[i]);
        }
        return productDao.createProduct(userId, name, description, priceDouble, used, departmentId, idArray[0], idArray[1], idArray[2]);
    }

    private Long getImageId(MultipartFile imageFile) {
        Image i = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            i = imageService.storeImage(imageFile);
        }
        return i == null ? 0 : i.getImageId();
    }

    @Override
    public void updateProduct(long productId, String name, String description, String price, boolean used, long departmentId, MultipartFile[] pictureFiles) {
        LOGGER.info("Updating Product {}", productId);
        double priceDouble = Double.parseDouble(price.replace("$", "").replace(",", ""));
        productDao.updateProduct(productId, name, description, priceDouble, used, departmentId, getImageId(pictureFiles[0]), getImageId(pictureFiles[1]), getImageId(pictureFiles[2]));
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
    public List<Product> getProductsByCriteria(long neighborhoodId, Department department, int page, int size) {
        LOGGER.info("Selecting Products by neighborhood {} and departments {}", neighborhoodId, department);
        return productDao.getProductsByCriteria(neighborhoodId, department, page, size);
    }

    @Override
    public int getProductsCountByCriteria(long neighborhoodId, Department department) {
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

    @Override
    public List<Product> searchInProductsBought(long userId, long neighborhoodId,String searchQuery, int page, int size){
        return productDao.searchProductsByName(userId, neighborhoodId, searchQuery, SearchVariant.BOUGHT, page, size);
    }

    @Override
    public List<Product> searchInProductsSold(long userId, long neighborhoodId, String searchQuery, int page, int size){
        return productDao.searchProductsByName(userId, neighborhoodId, searchQuery, SearchVariant.SOLD, page, size);
    }

    @Override
    public List<Product> searchInProductsSelling(long userId, long neighborhoodId, String searchQuery, int page, int size){
        return productDao.searchProductsByName(userId, neighborhoodId, searchQuery, SearchVariant.SELLING, page, size);
    }

    @Override
    public List<Product> searchInProductsBeingSold(long neighborhoodId, String searchQuery, int page, int size){
        return productDao.searchInAllProductsBeingSold(neighborhoodId, searchQuery, page, size);
    }
}

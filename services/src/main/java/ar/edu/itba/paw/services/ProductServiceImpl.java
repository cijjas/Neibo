package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.DepartmentDao;
import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.interfaces.persistence.PurchaseDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.Product;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductDao productDao;
    private final PurchaseDao purchaseDao;
    private final ImageService imageService;
    private final DepartmentDao departmentDao;

    @Autowired
    public ProductServiceImpl(final ProductDao productDao, final PurchaseDao purchaseDao, final ImageService imageService, DepartmentDao departmentDao) {
        this.productDao = productDao;
        this.purchaseDao = purchaseDao;
        this.imageService = imageService;
        this.departmentDao = departmentDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Product createProduct(long userId, String name, String description, String price, boolean used, long departmentId, MultipartFile[] pictureFiles, long units) {
        LOGGER.info("User {} Creating Product {}", userId, name);
        double priceDouble = Double.parseDouble(price.replace("$", "").replace(",", ""));
        Long[] idArray = {0L, 0L, 0L};
        int pictureFilesLength = pictureFiles == null? 0 : pictureFiles.length;
        for(int i = 0; i < pictureFilesLength; i++){
            idArray[i] = getImageId(pictureFiles[i]);
        }
        return productDao.createProduct(userId, name, description, priceDouble, used, departmentId, idArray[0], idArray[1], idArray[2], units);
    }

    @Override
    public Optional<Product> findProductById(long productId) {
        LOGGER.info("Selecting Product with id {}", productId);
        if (productId <= 0)
            throw new IllegalArgumentException("Product ID must be a positive integer");
        return productDao.findProductById(productId);
    }

    @Override
    public List<Product> getProductsByCriteria(long neighborhoodId, String department, long userId, String productStatus, int page, int size) {
        LOGGER.info("Selecting Products by neighborhood {} and departments {}", neighborhoodId, department);
        return productDao.getProductsByCriteria(neighborhoodId, department, userId, productStatus, page, size);
    }

    @Override
    public int getProductsCountByCriteria(long neighborhoodId, String department, long userId, String productStatus) {
        LOGGER.info("Counting Products by neighborhood {} and departments {}", neighborhoodId, department);
        return productDao.getProductsCountByCriteria(neighborhoodId, department, userId, productStatus);
    }

    @Override
    public void markAsBought(long buyerId, long productId, long units) {
        LOGGER.info("Marking Product {} as bought by user {}", productId, buyerId);
        purchaseDao.createPurchase(productId, buyerId, units);
        Product product = productDao.findProductById(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));
        product.setRemainingUnits(product.getRemainingUnits()-units);
    }

    @Override
    public int getProductsTotalPages(long neighborhoodId, int size, String department, long userId, String productStatus){
        return (int) Math.ceil((double) getProductsCountByCriteria(neighborhoodId, department, userId, productStatus) / size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void restockProduct(long productId, long extraUnits) {
        LOGGER.info("Restocking Product {}", productId);
        Product product = productDao.findProductById(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));
        product.setRemainingUnits(product.getRemainingUnits()+extraUnits);
    }

    @Override
    public void updateProduct(long productId, String name, String description, String price, boolean used, long departmentId, MultipartFile[] pictureFiles, Long stock) {
        LOGGER.info("Updating Product {}", productId);
        double priceDouble = Double.parseDouble(price.replace("$", "").replace(",", ""));
        Long[] idArray = {0L, 0L, 0L};
        for(int i = 0; i < pictureFiles.length; i++){
            idArray[i] = getImageId(pictureFiles[i]);
        }
        Long updatedStock = stock;
        if(stock == null){
            updatedStock = productDao.findProductById(productId).orElseThrow(()-> new NotFoundException("Product Not Found")).getRemainingUnits();
        }

        Product product = findProductById(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));
        product.setName(name);
        product.setDescription(description);
        product.setPrice(priceDouble);
        product.setUsed(used);
        product.setDepartment(departmentDao.findDepartmentById(departmentId).orElseThrow(()-> new NotFoundException("Department Not Found")));
        product.setRemainingUnits(updatedStock);
    }

    @Override
    public Product updateProductPartially(long productId, String name, String description, String price, boolean used, long departmentId, MultipartFile[] pictureFiles, Long stock){
        LOGGER.info("Updating Product {}", productId);
        Product product = findProductById(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));
        if(name != null && !name.isEmpty())
            product.setName(name);
        if(description != null && !description.isEmpty())
            product.setDescription(description);
        if(price != null && !price.isEmpty()) {
            double priceDouble = Double.parseDouble(price.replace("$", "").replace(",", ""));
            product.setPrice(priceDouble);
        }
        if(used != product.isUsed())
            product.setUsed(used);
        if(departmentId != 0)
            product.setDepartment(departmentDao.findDepartmentById(departmentId).orElseThrow(()-> new NotFoundException("Department Not Found")));
        if(stock != null)
            product.setRemainingUnits(stock);

        if(pictureFiles != null && pictureFiles.length > 0 && !pictureFiles[0].isEmpty()) {
            int pictureFilesLength = pictureFiles.length;

            if(pictureFilesLength >= 1) {
                Image i = imageService.storeImage(pictureFiles[0]);
                product.setPrimaryPicture(i);
            }

            if(pictureFilesLength >= 2) {
                Image i = imageService.storeImage(pictureFiles[1]);
                product.setSecondaryPicture(i);
            }

            if(pictureFilesLength == 3) {
                Image i = imageService.storeImage(pictureFiles[2]);
                product.setTertiaryPicture(i);
            }

        }

        return product;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteProduct(long productId) {
        LOGGER.info("Deleting Product {}", productId);
        return productDao.deleteProduct(productId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    private Long getImageId(MultipartFile imageFile) {
        Image i = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            i = imageService.storeImage(imageFile);
        }
        return i == null ? 0 : i.getImageId();
    }

}

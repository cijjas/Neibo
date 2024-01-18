package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
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

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Optional<Product> findProduct(long productId) {
        LOGGER.info("Selecting Product with id {}", productId);

        ValidationUtils.checkProductId(productId);

        return productDao.findProduct(productId);
    }

    @Override
    public Optional<Product> findProduct(long productId, long neighborhoodId) {
        LOGGER.info("Selecting Product with id {}", productId);

        ValidationUtils.checkProductId(productId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return productDao.findProduct(productId, neighborhoodId);
    }

    @Override
    public List<Product> getProducts(long neighborhoodId, String department, Long userId, String productStatus, int page, int size) {
        LOGGER.info("Selecting Products by neighborhood {} and departments {}", neighborhoodId, department);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkOptionalProductStatusString(productStatus);
        ValidationUtils.checkOptionalDepartmentString(department);
        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkPageAndSize(page, size);

        return productDao.getProducts(neighborhoodId, department, userId, productStatus, page, size);
    }

    // ---------------------------------------------------

    @Override
    public int countProducts(long neighborhoodId, String department, Long userId, String productStatus) {
        LOGGER.info("Counting Products by neighborhood {} and departments {}", neighborhoodId, department);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkOptionalProductStatusString(productStatus);
        ValidationUtils.checkOptionalDepartmentString(department);
        ValidationUtils.checkUserId(userId);

        return productDao.countProducts(neighborhoodId, department, userId, productStatus);
    }

    @Override
    public int calculateProductPages(long neighborhoodId, int size, String department, Long userId, String productStatus){

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkOptionalProductStatusString(productStatus);
        ValidationUtils.checkOptionalDepartmentString(department);
        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(productDao.countProducts(neighborhoodId, department, userId, productStatus), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Product updateProductPartially(long productId, String name, String description, String price, boolean used, long departmentId, MultipartFile[] pictureFiles, Long stock){
        LOGGER.info("Updating Product {}", productId);

        Product product = findProduct(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));
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
            product.setDepartment(departmentDao.findDepartment(departmentId).orElseThrow(()-> new NotFoundException("Department Not Found")));
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
        ValidationUtils.checkProductId(productId);
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

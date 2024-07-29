package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.DepartmentDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.interfaces.persistence.PurchaseDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.TwoIds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Validation;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductDao productDao;
    private final NeighborhoodDao neighborhoodDao;
    private final ImageService imageService;
    private final DepartmentDao departmentDao;

    @Autowired
    public ProductServiceImpl(final ProductDao productDao, final ImageService imageService,
                              DepartmentDao departmentDao, NeighborhoodDao neighborhoodDao) {
        this.productDao = productDao;
        this.imageService = imageService;
        this.departmentDao = departmentDao;
        this.neighborhoodDao = neighborhoodDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Product createProduct(String userURN, String name, String description, Double price, boolean used, String departmentURN, String[] imageURNs, long units) {
        LOGGER.info("Creating Product {} from User {}", name, userURN);

        long departmentId = ValidationUtils.extractURNId(departmentURN);
        ValidationUtils.checkDepartmentId(departmentId);

        Long[] idArray = {0L, 0L, 0L};
        int imageURNsLength = imageURNs == null? 0 : imageURNs.length;
        for(int i = 0; i < imageURNsLength; i++)
            idArray[i] = getImageId(imageURNs[i]);

        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN);

        return productDao.createProduct(userId, name, description, price, used, departmentId, idArray[0], idArray[1], idArray[2], units);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Optional<Product> findProduct(long productId) {
        LOGGER.info("Finding Product {}", productId);

        ValidationUtils.checkProductId(productId);

        return productDao.findProduct(productId);
    }

    @Override
    public Optional<Product> findProduct(long productId, long neighborhoodId) {
        LOGGER.info("Finding Product {} from Neighborhood {}", productId, neighborhoodId);

        ValidationUtils.checkProductId(productId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return productDao.findProduct(productId, neighborhoodId);
    }

    @Override
    public List<Product> getProducts(long neighborhoodId, String departmentURN, String userURN, String productStatusURN, int page, int size) {
        LOGGER.info("Getting Products with status {} from Department {} by User {} from Neighborhood {}", productStatusURN, departmentURN, userURN, neighborhoodId);

        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN);
        Long departmentId = ValidationUtils.checkURNAndExtractUserDepartmentId(departmentURN);
        Long productStatusId = ValidationUtils.checkURNAndExtractUserProductStatusId(productStatusURN);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPageAndSize(page, size);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return productDao.getProducts(neighborhoodId, departmentId, userId, productStatusId, page, size);
    }

    // ---------------------------------------------------

    @Override
    public int countProducts(long neighborhoodId, String departmentURN, String userURN, String productStatusURN) {
        LOGGER.info("Counting Products with status {} from Department {} by User {} from Neighborhood {}", productStatusURN, departmentURN, userURN, neighborhoodId);

        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN);
        Long departmentId = ValidationUtils.checkURNAndExtractUserDepartmentId(departmentURN);
        Long productStatusId = ValidationUtils.checkURNAndExtractUserProductStatusId(productStatusURN);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return productDao.countProducts(neighborhoodId, departmentId, userId, productStatusId);
    }

    @Override
    public int calculateProductPages(long neighborhoodId, int size, String departmentURN, String userURN, String productStatusURN){
        LOGGER.info("Calculating Product Pages with status {} from Department {} by User {} from Neighborhood {}", productStatusURN, departmentURN, userURN, neighborhoodId);

        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN);
        Long departmentId = ValidationUtils.checkURNAndExtractUserDepartmentId(departmentURN);
        Long productStatusId = ValidationUtils.checkURNAndExtractUserProductStatusId(productStatusURN);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(productDao.countProducts(neighborhoodId, departmentId, userId, productStatusId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Product updateProductPartially(long productId, String name, String description, Double price, boolean used, String departmentURN, String[] imageURNs, Long stock){
        LOGGER.info("Updating Product {}", productId);

        long departmentId = ValidationUtils.extractURNId(departmentURN);
        ValidationUtils.checkDepartmentId(departmentId);

        Product product = findProduct(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));
        if(name != null && !name.isEmpty())
            product.setName(name);
        if(description != null && !description.isEmpty())
            product.setDescription(description);
//        if(price != null && !price.isEmpty()) {
//            double priceDouble = Double.parseDouble(price.replace("$", "").replace(",", ""));
//            product.setPrice(priceDouble);
//        }
        if(price != null)
            product.setPrice(price);
        if(used != product.isUsed())
            product.setUsed(used);
        if(departmentId != 0)
            product.setDepartment(departmentDao.findDepartment(departmentId).orElseThrow(()-> new NotFoundException("Department Not Found")));
        if(stock != null)
            product.setRemainingUnits(stock);

        if(imageURNs != null && imageURNs.length > 0 && imageURNs[0] != null) {
            int pictureFilesLength = imageURNs.length;

            long imageId = ValidationUtils.extractURNId(imageURNs[0]);
            ValidationUtils.checkImageId(imageId);
            Image i = imageService.findImage(imageId).orElseThrow(() -> new NotFoundException("Image not found"));
            product.setPrimaryPicture(i);

            if(pictureFilesLength >= 2) {
                imageId = ValidationUtils.extractURNId(imageURNs[1]);
                ValidationUtils.checkImageId(imageId);
                i = imageService.findImage(imageId).orElseThrow(() -> new NotFoundException("Image not found"));
                product.setSecondaryPicture(i);
            }

            if(pictureFilesLength == 3) {
                imageId = ValidationUtils.extractURNId(imageURNs[2]);
                ValidationUtils.checkImageId(imageId);
                i = imageService.findImage(imageId).orElseThrow(() -> new NotFoundException("Image not found"));
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

    private Long getImageId(String imageURN) {
        if (imageURN != null) {
            long imageId = ValidationUtils.extractURNId(imageURN);
            ValidationUtils.checkImageId(imageId);
            return imageId;
        }
        return 0L;
    }
}

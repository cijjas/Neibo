package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.DepartmentDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.Product;
import org.hibernate.annotations.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public Product createProduct(long userId, String name, String description, Double price, boolean used, long departmentId, List<Long> imageIds, long units) {
        LOGGER.info("Creating Product {} from User {}", name, userId);

        Long[] idArray = {0L, 0L, 0L};
        int imageURNsLength = imageIds == null ? 0 : imageIds.size();
        for (int i = 0; i < imageURNsLength && i < idArray.length; i++)
            idArray[i] = imageIds.get(i);

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
    public int calculateProductPages(long neighborhoodId, int size, String departmentURN, String userURN, String productStatusURN) {
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
    public Product updateProductPartially(long productId, String name, String description, Double price, Boolean used, Long departmentId, List<Long> imageIds, Long stock) {
        LOGGER.info("Updating Product {}", productId);

        Product product = findProduct(productId).orElseThrow(() -> new NotFoundException("Product Not Found"));

        if (name != null)
            product.setName(name);
        if (description != null )
            product.setDescription(description);
        if (price != null)
            product.setPrice(price);
        if (used != null)
            product.setUsed(used);
        if (departmentId!= null)
            product.setDepartment(departmentDao.findDepartment(departmentId).orElseThrow(() -> new NotFoundException("Department Not Found")));
        if (stock != null)
            product.setRemainingUnits(stock);

        if (imageIds != null && !imageIds.isEmpty()) {
            product.setPrimaryPicture(imageService.findImage(imageIds.get(0)).orElseThrow(() -> new NotFoundException("Image Not Found")));

            if (imageIds.size() > 1)
                product.setSecondaryPicture(imageService.findImage(imageIds.get(1)).orElseThrow(() -> new NotFoundException("Image Not Found")));

            if (imageIds.size() > 2)
                product.setTertiaryPicture(imageService.findImage(imageIds.get(2)).orElseThrow(() -> new NotFoundException("Image Not Found")));
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
}

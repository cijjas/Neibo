package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.DepartmentDao;
import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.Entities.Product;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductDao productDao;
    private final ImageService imageService;
    private final DepartmentDao departmentDao;

    @Autowired
    public ProductServiceImpl(ProductDao productDao, ImageService imageService, DepartmentDao departmentDao) {
        this.productDao = productDao;
        this.imageService = imageService;
        this.departmentDao = departmentDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Product createProduct(long userId, String name, String description, Double price, long units, boolean used, long departmentId, List<Long> imageIds) {
        LOGGER.info("Creating Product {} described as {} with {} units categorized in Department {} and {} used condition from User {}", name, description, units, departmentId, used, userId);

        Long[] idArray = {0L, 0L, 0L};
        int imagesLength = imageIds == null ? 0 : imageIds.size();
        for (int i = 0; i < imagesLength && i < idArray.length; i++)
            idArray[i] = imageIds.get(i);

        return productDao.createProduct(userId, name, description, price, units, used, departmentId, idArray[0], idArray[1], idArray[2]);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findProduct(long productId) {
        LOGGER.info("Finding Product {}", productId);

        return productDao.findProduct(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findProduct(long neighborhoodId, long productId) {
        LOGGER.info("Finding Product {} from Neighborhood {}", productId, neighborhoodId);

        return productDao.findProduct(neighborhoodId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProducts(long neighborhoodId, Long userId, Long departmentId, Long productStatusId, int page, int size) {
        LOGGER.info("Getting Products with status {} from Department {} by User {} from Neighborhood {}", productStatusId, departmentId, userId, neighborhoodId);

        return productDao.getProducts(neighborhoodId, userId, departmentId, productStatusId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateProductPages(long neighborhoodId, Long userId, Long departmentId, Long productStatusId, int size) {
        LOGGER.info("Calculating Product Pages with status {} from Department {} by User {} from Neighborhood {}", productStatusId, departmentId, userId, neighborhoodId);

        return PaginationUtils.calculatePages(productDao.countProducts(neighborhoodId, userId, departmentId, productStatusId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Product updateProduct(long neighborhoodId, long productId, String name, String description, Double price, Long units, Boolean used, Long departmentId, List<Long> imageIds) {
        LOGGER.info("Updating Product {} from Neighborhood {}", productId, neighborhoodId);

        Product product = findProduct(neighborhoodId, productId).orElseThrow(NotFoundException::new);

        if (name != null)
            product.setName(name);
        if (description != null)
            product.setDescription(description);
        if (price != null)
            product.setPrice(price);
        if (used != null)
            product.setUsed(used);
        if (departmentId != null)
            product.setDepartment(departmentDao.findDepartment(departmentId).orElseThrow(NotFoundException::new));
        if (units != null)
            product.setRemainingUnits(units);

        if (imageIds != null) {
            if (imageIds.isEmpty()) {
                product.setPrimaryPicture(null);
                product.setSecondaryPicture(null);
                product.setTertiaryPicture(null);
            } else {
                product.setPrimaryPicture(imageService.findImage(imageIds.get(0)).orElseThrow(NotFoundException::new));
                product.setSecondaryPicture(imageIds.size() > 1 ? imageService.findImage(imageIds.get(1)).orElseThrow(NotFoundException::new) : null);
                product.setTertiaryPicture(imageIds.size() > 2 ? imageService.findImage(imageIds.get(2)).orElseThrow(NotFoundException::new) : null);
            }
        }

        return product;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteProduct(long neighborhoodId, long productId) {
        LOGGER.info("Deleting Product {} from Neighborhood {}", productId, neighborhoodId);

        return productDao.deleteProduct(neighborhoodId, productId);
    }
}

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
    public ProductServiceImpl(final ProductDao productDao, final ImageService imageService, DepartmentDao departmentDao) {
        this.productDao = productDao;
        this.imageService = imageService;
        this.departmentDao = departmentDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Product createProduct(long userId, String name, String description, Double price, boolean used, long departmentId, List<Long> imageIds, long units) {
        LOGGER.info("Creating Product {} from User {}", name, userId);

        Long[] idArray = {0L, 0L, 0L};
        int imagesLength = imageIds == null ? 0 : imageIds.size();
        for (int i = 0; i < imagesLength && i < idArray.length; i++)
            idArray[i] = imageIds.get(i);

        return productDao.createProduct(userId, name, description, price, used, departmentId, idArray[0], idArray[1], idArray[2], units);
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
    public Optional<Product> findProduct(long productId, long neighborhoodId) {
        LOGGER.info("Finding Product {} from Neighborhood {}", productId, neighborhoodId);

        return productDao.findProduct(productId, neighborhoodId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProducts(long neighborhoodId, Long departmentId, Long userId, Long productStatusId, int page, int size) {
        LOGGER.info("Getting Products with status {} from Department {} by User {} from Neighborhood {}", productStatusId, departmentId, userId, neighborhoodId);

        return productDao.getProducts(neighborhoodId, departmentId, userId, productStatusId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateProductPages(long neighborhoodId, int size, Long departmentId, Long userId, Long productStatusId) {
        LOGGER.info("Calculating Product Pages with status {} from Department {} by User {} from Neighborhood {}", productStatusId, departmentId, userId, neighborhoodId);

        return PaginationUtils.calculatePages(productDao.countProducts(neighborhoodId, departmentId, userId, productStatusId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Product updateProductPartially(long productId, String name, String description, Double price, Boolean used, Long departmentId, List<Long> imageIds, Long stock) {
        LOGGER.info("Updating Product {}", productId);

        Product product = findProduct(productId).orElseThrow(NotFoundException::new);

        if (name != null)
            product.setName(name);
        if (description != null )
            product.setDescription(description);
        if (price != null)
            product.setPrice(price);
        if (used != null)
            product.setUsed(used);
        if (departmentId!= null)
            product.setDepartment(departmentDao.findDepartment(departmentId).orElseThrow(NotFoundException::new));
        if (stock != null)
            product.setRemainingUnits(stock);

        if (imageIds != null && !imageIds.isEmpty()) {
            product.setPrimaryPicture(imageService.findImage(imageIds.get(0)).orElseThrow(NotFoundException::new));
            if (imageIds.size() > 1)
                product.setSecondaryPicture(imageService.findImage(imageIds.get(1)).orElseThrow(NotFoundException::new));
            if (imageIds.size() > 2)
                product.setTertiaryPicture(imageService.findImage(imageIds.get(2)).orElseThrow(NotFoundException::new));
        }

        return product;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteProduct(long productId) {
        LOGGER.info("Deleting Product {}", productId);

        return productDao.deleteProduct(productId);
    }
}

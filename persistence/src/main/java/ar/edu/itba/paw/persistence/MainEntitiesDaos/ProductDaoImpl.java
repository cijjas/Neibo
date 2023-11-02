package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.SearchVariant;
import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.models.MainEntities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductDaoImpl implements ProductDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // ------------------------------------------------ PRODUCT INSERT -------------------------------------------------

    @Override
    public Product createProduct(long userId, String name, String description, double price, boolean used, long departmentId, Long primaryPictureId, Long secondaryPictureId, Long tertiaryPictureId) {
        LOGGER.debug("Inserting Product {}", name);
        Product product = new Product.Builder()
                .name(name)
                .description(description)
                .price(price)
                .used(used)
                .department(em.find(ar.edu.itba.paw.models.MainEntities.Department.class, departmentId))
                .seller(em.find(User.class, userId))
                .primaryPicture(em.find(Image.class, primaryPictureId))
                .secondaryPicture(em.find(Image.class, secondaryPictureId))
                .tertiaryPicture(em.find(Image.class, tertiaryPictureId))
                .build();
        em.persist(product);
        return product;
    }

    @Override
    public Product updateProduct(long productId, String name, String description, double price, boolean used, long departmentId, Long primaryPictureId, Long secondaryPictureId, Long tertiaryPictureId) {
        LOGGER.debug("Updating Product {}", productId);

        Product product = em.find(Product.class, productId);
        if (product != null) {
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setUsed(used);
            product.setDepartment(em.find(ar.edu.itba.paw.models.MainEntities.Department.class, departmentId));
            product.setPrimaryPicture(em.find(Image.class, primaryPictureId));
            product.setSecondaryPicture(em.find(Image.class, secondaryPictureId));
            product.setTertiaryPicture(em.find(Image.class, tertiaryPictureId));
        }

        return product;
    }

    @Override
    public boolean deleteProduct(long productId) {
        LOGGER.debug("Deleting Product with id {}", productId);
        int deletedCount = em.createQuery("DELETE FROM Product WHERE productId = :productId ")
                .setParameter("productId", productId)
                .executeUpdate();
        return deletedCount > 0;
    }

    @Override
    public Optional<Product> findProductById(long productId) {
        LOGGER.debug("Selecting Product with id {}", productId);
        return Optional.ofNullable(em.find(Product.class, productId));
    }

    @Override
    public List<Product> getProductsByCriteria(long neighborhoodId, Department department, int page, int size) {
        LOGGER.debug("Selecting Products from neighborhood {}, with department {}", neighborhoodId, department);
        TypedQuery<Product> query;
        if(department != Department.NONE) {
            query = em.createQuery("SELECT DISTINCT p FROM Product p WHERE p.seller.neighborhood.neighborhoodId = :neighborhoodId AND p.department.department = (:department)", Product.class)
                    .setParameter("neighborhoodId", neighborhoodId)
                    .setParameter("department", department);
        } else {
            query = em.createQuery("SELECT DISTINCT p FROM Product p WHERE p.seller.neighborhood.neighborhoodId = :neighborhoodId", Product.class)
                    .setParameter("neighborhoodId", neighborhoodId);
        }

        if(page != 0) {
            query.setFirstResult((page - 1) * size).setMaxResults(size);
        }

        return query.getResultList();
    }

    @Override
    public int getProductsCountByCriteria(long neighborhoodId, Department department) {
        LOGGER.debug("Selecting Products Count from neighborhood {}, with tags {}", neighborhoodId, department);
        TypedQuery<Integer> count = em.createQuery("SELECT COUNT( DISTINCT p) FROM Product p WHERE p.seller.neighborhood = :neighborhoodId AND p.department.department = (:department)", Integer.class)
                .setParameter("neighborhoodId", neighborhoodId)
                .setParameter("department", department);
        return count.getSingleResult();
    }

    @Override
    public List<Product> getProductsSelling(long userId) {
        LOGGER.debug("Selecting Selling Products from User {}", userId);
        TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p WHERE p.buyer IS NULL AND p.seller.userId = :userId", Product.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<Product> getProductsSold(long userId) {
        LOGGER.debug("Selecting Sold Products from User {}", userId);
        TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p WHERE p.buyer IS NOT NULL AND p.seller.userId = :userId", Product.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public boolean markAsBought(long buyerId, long productId) {
        LOGGER.debug("Marking Product {} as bought by User {}", productId, buyerId);
        Product product = em.find(Product.class, productId);
        if (product != null && product.getBuyer() == null) {
            product.setBuyer(em.find(User.class, buyerId));
            return true;
        }
        return false;
    }

    @Override
    public List<Product> searchInAllProductsBeingSold(String searchQuery){
        LOGGER.debug("Searching for products with name containing: {} ", searchQuery);
        String searchParam = "%" + searchQuery.toLowerCase() + "%";

        // Initialize the SQL query
        String sql = "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:search) AND p.buyer IS NULL";

        TypedQuery<Product> query = em.createQuery(sql, Product.class);
        query.setParameter("search", searchParam);

        return query.getResultList();
    }

    public List<Product> searchProductsByName(long userId, String searchQuery, SearchVariant searchVariant) {
        LOGGER.debug("Searching for products with name containing: {} for user {} with variant {}", searchQuery, userId, searchVariant);
        String searchParam = "%" + searchQuery.toLowerCase() + "%";

        // Initialize the SQL query
        String sql = "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:search)";

        // Set parameters based on the searchVariant
        switch (searchVariant) {
            case BOUGHT:
                sql += " AND p.buyer.userId = :userId";
                break;
            case SOLD:
                sql += " AND p.seller.userId = :userId AND p.buyer IS NOT NULL";
                break;
            case SELLING:
                sql += " AND p.seller.userId = :userId AND p.buyer IS NULL";
                break;
        }

        TypedQuery<Product> query = em.createQuery(sql, Product.class);
        query.setParameter("search", searchParam);

        // Set userId parameter if needed
            query.setParameter("userId", userId);

        return query.getResultList();
    }
}

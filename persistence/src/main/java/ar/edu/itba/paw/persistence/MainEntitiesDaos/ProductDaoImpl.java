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
import javax.persistence.criteria.*;
import java.util.Collections;
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
    public List<Product> searchInAllProductsBeingSold(long neighborhoodId, String searchQuery, int page, int size) {
        LOGGER.debug("Searching for products with name containing: {} in neighborhood: {}", searchQuery, neighborhoodId);
        String searchParam = "%" + searchQuery.toLowerCase() + "%";

        // Initialize the first query to retrieve product IDs
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<Product> idRoot = idQuery.from(Product.class);
        idQuery.select(idRoot.get("productId"));

        // Join through the neighborhoodId
        Join<Product, User> sellerJoin = idRoot.join("seller");
        Join<User, Neighborhood> neighborhoodJoin = sellerJoin.join("neighborhood");

        idQuery.where(
                cb.and(
                        cb.like(cb.lower(idRoot.get("name")), searchParam),
                        cb.isNull(idRoot.get("buyer")),
                        cb.equal(neighborhoodJoin.get("neighborhoodId"), neighborhoodId)
                )
        );

        TypedQuery<Long> idTypedQuery = em.createQuery(idQuery);
        idTypedQuery.setFirstResult((page - 1) * size);
        idTypedQuery.setMaxResults(size);

        List<Long> productIds = idTypedQuery.getResultList();

        // Check if productIds is empty for better performance
        if (productIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Initialize the second query to fetch product details
        CriteriaQuery<Product> dataQuery = cb.createQuery(Product.class);
        Root<Product> dataRoot = dataQuery.from(Product.class);

        // Add a predicate to filter by the IDs retrieved in the first query
        dataQuery.where(dataRoot.get("productId").in(productIds));

        TypedQuery<Product> dataTypedQuery = em.createQuery(dataQuery);

        return dataTypedQuery.getResultList();
    }


    @Override
    public List<Product> searchProductsByName(long userId, long neighborhoodId, String searchQuery, SearchVariant searchVariant, int page, int size) {
        LOGGER.debug("Searching for products with name containing: {} for user {} with variant {}", searchQuery, userId, searchVariant);
        String searchParam = "%" + searchQuery.toLowerCase() + "%";

        // Initialize the first query to retrieve product IDs
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<Product> idRoot = idQuery.from(Product.class);
        idQuery.select(idRoot.get("productId"));

        Join<Product, User> sellerJoin = idRoot.join("seller");
        Join<User, Neighborhood> neighborhoodJoin = sellerJoin.join("neighborhood");

        Predicate conditions = cb.and(
                cb.like(cb.lower(idRoot.get("name")), searchParam),
                cb.equal(neighborhoodJoin.get("neighborhoodId"), neighborhoodId)
        );

        switch (searchVariant) {
            case BOUGHT:
                Join<Product, User> buyerJoinBought = idRoot.join("buyer");
                conditions = cb.and(
                        conditions,
                        cb.equal(buyerJoinBought.get("userId"), userId),
                        cb.isNotNull(buyerJoinBought.get("userId"))
                );
                break;
            case SOLD:
                Join<Product, User> buyerJoinSold = idRoot.join("buyer");
                conditions = cb.and(
                        conditions,
                        cb.equal(sellerJoin.get("userId"), userId),
                        cb.isNotNull(buyerJoinSold.get("userId"))
                );
                break;
            case SELLING:
                conditions = cb.and(
                        conditions,
                        cb.isNull(idRoot.get("buyer")),
                        cb.equal(sellerJoin.get("userId"), userId)
                );
                break;
        }

        idQuery.where(conditions);

        TypedQuery<Long> idTypedQuery = em.createQuery(idQuery);
        idTypedQuery.setFirstResult((page - 1) * size);
        idTypedQuery.setMaxResults(size);

        List<Long> productIds = idTypedQuery.getResultList();

        // Check if productIds is empty for better performance
        if (productIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Initialize the second query to fetch product details
        CriteriaQuery<Product> dataQuery = cb.createQuery(Product.class);
        Root<Product> dataRoot = dataQuery.from(Product.class);

        // Add a predicate to filter by the IDs retrieved in the first query
        dataQuery.where(dataRoot.get("productId").in(productIds));

        TypedQuery<Product> dataTypedQuery = em.createQuery(dataQuery);

        return dataTypedQuery.getResultList();
    }





}

package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.models.JunctionEntities.Purchase;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // ------------------------------------------------ PRODUCT INSERT -------------------------------------------------

    @Override
    public Product createProduct(long userId, String name, String description, double price, boolean used, long departmentId, Long primaryPictureId, Long secondaryPictureId, Long tertiaryPictureId, Long units) {
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
                .creationDate(new java.sql.Date(System.currentTimeMillis()))
                .remainingUnits(units)
                .build();
        em.persist(product);
        return product;
    }

//    @Override
//    public Product updateProduct(long productId, String name, String description, double price, boolean used, long departmentId, Long primaryPictureId, Long secondaryPictureId, Long tertiaryPictureId, Long stock) {
//        LOGGER.debug("Updating Product {}", productId);
//
//        Product product = em.find(Product.class, productId);
//        if (product != null) {
//            product.setName(name);
//            product.setDescription(description);
//            product.setPrice(price);
//            product.setUsed(used);
//            product.setDepartment(em.find(ar.edu.itba.paw.models.MainEntities.Department.class, departmentId));
//            product.setRemainingUnits(stock);
//        }
//
//        return product;
//    }

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
        LOGGER.debug("Selecting Products from neighborhood {}, in departments {}", neighborhoodId, department);
        // Initialize CriteriaBuilder
        CriteriaBuilder cb = em.getCriteriaBuilder();
        // First Query to retrieve product ids
        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<Product> idRoot = idQuery.from(Product.class);
        idQuery.select(idRoot.get("productId"));
        // Add conditions for filtering
        Predicate predicate = cb.equal(idRoot.get("seller").get("neighborhood").get("neighborhoodId"), neighborhoodId);
        if (department != Department.NONE) {
            predicate = cb.and(predicate, cb.equal(idRoot.get("department").get("departmentId"), department.getId()));
        }
        idQuery.where(predicate);
        // Create the query
        TypedQuery<Long> idTypedQuery = em.createQuery(idQuery);
        // Implement pagination in the first query
        idTypedQuery.setFirstResult((page - 1) * size);
        idTypedQuery.setMaxResults(size);
        // Results
        List<Long> productIds = idTypedQuery.getResultList();
        // Check if productIds is empty for better performance
        if (productIds.isEmpty()) {
            return Collections.emptyList();
        }
        // Second Query to retrieve actual products
        CriteriaQuery<Product> dataQuery = cb.createQuery(Product.class);
        Root<Product> dataRoot = dataQuery.from(Product.class);
        // Add predicate that enforces existence within the IDs recovered in the first query
        dataQuery.where(dataRoot.get("productId").in(productIds));
        dataQuery.orderBy(cb.desc(dataRoot.get("creationDate")));
        // Create the query
        TypedQuery<Product> dataTypedQuery = em.createQuery(dataQuery);
        // Return Results
        return dataTypedQuery.getResultList();
    }

    @Override
    public int getProductsCountByCriteria(long neighborhoodId, Department department) {
        LOGGER.debug("Selecting Products Count from neighborhood {}, in departments {}", neighborhoodId, department);
        // Initialize CriteriaBuilder
        CriteriaBuilder cb = em.getCriteriaBuilder();
        // First Query to retrieve product count
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        countQuery.select(cb.countDistinct(countRoot));
        // Add conditions for filtering
        Predicate predicate = cb.equal(countRoot.get("seller").get("neighborhood").get("neighborhoodId"), neighborhoodId);
        if (department != Department.NONE) {
            predicate = cb.and(predicate, cb.equal(countRoot.get("department").get("departmentId"), department.getId()));
        }
        countQuery.where(predicate);
        // Create the query
        TypedQuery<Long> countTypedQuery = em.createQuery(countQuery);
        // Result
        Long countResult = countTypedQuery.getSingleResult();
        // Return the count as an integer
        return countResult.intValue();
    }

    @Override
    public int getProductsSellingCount(long userId) {
        LOGGER.debug("Selecting Selling Products Count from User {}", userId);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Purchase> purchaseRoot = countQuery.from(Purchase.class);
        Join<Purchase, Product> productJoin = purchaseRoot.join("product");

        countQuery.select(cb.countDistinct(productJoin));

        Predicate predicate = cb.equal(productJoin.get("seller").get("userId"), userId);
        predicate = cb.and(predicate, cb.isNull(purchaseRoot.get("user")));
        countQuery.where(predicate);

        TypedQuery<Long> countTypedQuery = em.createQuery(countQuery);
        Long countResult = countTypedQuery.getSingleResult();

        return countResult.intValue();
    }

    @Override
    public int getProductsSoldCount(long userId) {
        LOGGER.debug("Selecting Sold Products Count from User {}", userId);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Purchase> purchaseRoot = countQuery.from(Purchase.class);
        Join<Purchase, Product> productJoin = purchaseRoot.join("product");

        countQuery.select(cb.countDistinct(productJoin));

        Predicate predicate = cb.equal(productJoin.get("seller").get("userId"), userId);
        predicate = cb.and(predicate, cb.isNotNull(purchaseRoot.get("user")));
        countQuery.where(predicate);

        TypedQuery<Long> countTypedQuery = em.createQuery(countQuery);
        Long countResult = countTypedQuery.getSingleResult();

        return countResult.intValue();
    }

    @Override
    public int getProductsBoughtCount(long userId) {
        LOGGER.debug("Selecting Bought Products Count from User {}", userId);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Purchase> purchaseRoot = countQuery.from(Purchase.class);
        Join<Purchase, Product> productJoin = purchaseRoot.join("product");

        countQuery.select(cb.countDistinct(productJoin));

        Predicate predicate = cb.equal(purchaseRoot.get("user").get("userId"), userId);
        countQuery.where(predicate);

        TypedQuery<Long> countTypedQuery = em.createQuery(countQuery);
        Long countResult = countTypedQuery.getSingleResult();

        return countResult.intValue();
    }

    @Override
    public List<Product> getProductsSelling(long userId, int page, int size) {
        LOGGER.debug("Selecting Selling Products from User {}", userId);

        // First query to retrieve product IDs with remainingUnits > 0
        TypedQuery<Long> idQuery = em.createQuery(
                "SELECT p.productId FROM Product p WHERE p.seller.userId = :userId", Long.class);
        idQuery.setParameter("userId", userId);
        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);

        List<Long> productIds = idQuery.getResultList();

        // Second query to retrieve Product objects based on the IDs
        if (!productIds.isEmpty()) {
            TypedQuery<Product> productQuery = em.createQuery(
                    "SELECT p FROM Product p WHERE p.productId IN :productIds ORDER BY p.creationDate DESC", Product.class);
            productQuery.setParameter("productIds", productIds);
            return productQuery.getResultList();
        }

        return Collections.emptyList();
    }


    @Override
    public List<Product> getProductsSold(long userId, int page, int size) {
        LOGGER.debug("Selecting Sold Products from User {}", userId);

        // First query to retrieve product IDs
        TypedQuery<Long> idQuery = em.createQuery(
                "SELECT DISTINCT p.product.id FROM Purchase p WHERE p.user IS NOT NULL AND p.product.seller.userId = :userId", Long.class);
        idQuery.setParameter("userId", userId);
        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);

        List<Long> productIds = idQuery.getResultList();

        // Second query to retrieve Product objects based on the IDs
        if (!productIds.isEmpty()) {
            TypedQuery<Product> productQuery = em.createQuery(
                    "SELECT p FROM Product p WHERE p.productId IN :productIds ORDER BY p.creationDate DESC", Product.class);
            productQuery.setParameter("productIds", productIds);
            return productQuery.getResultList();
        }

        return Collections.emptyList();
    }


    @Override
    public List<Product> getProductsBought(long userId, int page, int size) {
        LOGGER.debug("Selecting Bought Products from User {}", userId);

        // First query to retrieve product IDs
        TypedQuery<Long> idQuery = em.createQuery(
                "SELECT DISTINCT p.product.id FROM Purchase p WHERE p.user.userId = :userId", Long.class);
        idQuery.setParameter("userId", userId);
        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);

        List<Long> productIds = idQuery.getResultList();

        // Second query to retrieve Product objects based on the IDs
        if (!productIds.isEmpty()) {
            TypedQuery<Product> productQuery = em.createQuery(
                    "SELECT p FROM Product p WHERE p.productId IN :productIds ORDER BY p.creationDate DESC", Product.class);
            productQuery.setParameter("productIds", productIds);
            return productQuery.getResultList();
        }

        return Collections.emptyList();
    }

}

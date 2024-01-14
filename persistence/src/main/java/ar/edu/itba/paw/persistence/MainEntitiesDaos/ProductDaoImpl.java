package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.ProductStatus;
import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.Entities.Purchase;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
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
                .department(em.find(ar.edu.itba.paw.models.Entities.Department.class, departmentId))
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

    @Override
    public boolean deleteProduct(long productId) {
        LOGGER.debug("Deleting Product with id {}", productId);
        int deletedCount = em.createQuery("DELETE FROM Product WHERE productId = :productId ")
                .setParameter("productId", productId)
                .executeUpdate();
        return deletedCount > 0;
    }

    @Override
    public Optional<Product> findProduct(long productId) {
        LOGGER.debug("Selecting Product with id {}", productId);
        return Optional.ofNullable(em.find(Product.class, productId));
    }

    @Override
    public List<Product> getProducts(long neighborhoodId, String department, long userId, String productStatus, int page, int size) {
        LOGGER.debug("Selecting Products from neighborhood {}, in departments {}", neighborhoodId, department);

        List<Predicate> predicates = new ArrayList<>();
        if(userId != 0 || productStatus != null){
            ProductStatus productStatusEnum = ProductStatus.valueOf(productStatus);
            TypedQuery<Long> idQuery = null;
            if(productStatusEnum == ProductStatus.BOUGHT){
                idQuery = em.createQuery(
                        "SELECT DISTINCT p.product.id FROM Purchase p WHERE p.user.userId = :userId", Long.class);
                idQuery.setParameter("userId", userId);
            }
            else if(productStatusEnum == ProductStatus.SOLD){
                idQuery = em.createQuery(
                        "SELECT DISTINCT p.product.id FROM Purchase p WHERE p.user IS NOT NULL AND p.product.seller.userId = :userId", Long.class);
                idQuery.setParameter("userId", userId);
            }
            else if(productStatusEnum == ProductStatus.SELLING){
                idQuery = em.createQuery(
                        "SELECT p.productId FROM Product p WHERE p.seller.userId = :userId", Long.class);
                idQuery.setParameter("userId", userId);
            }
            idQuery.setFirstResult((page - 1) * size);
            idQuery.setMaxResults(size);
            List<Long> productIds = idQuery.getResultList();
            if (!productIds.isEmpty()) {
                TypedQuery<Product> productQuery = em.createQuery(
                        "SELECT p FROM Product p WHERE p.productId IN :productIds ORDER BY p.creationDate DESC", Product.class);
                productQuery.setParameter("productIds", productIds);
                return productQuery.getResultList();
            }
        }
        else if(department != null) {
            // Initialize CriteriaBuilder
            CriteriaBuilder cb = em.getCriteriaBuilder();
            // First Query to retrieve product ids
            CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
            Root<Product> idRoot = idQuery.from(Product.class);
            idQuery.select(idRoot.get("productId"));
            Department departmentEnum = Department.valueOf(department);
            predicates.add(cb.equal(idRoot.get("seller").get("neighborhood").get("neighborhoodId"), neighborhoodId));
            if (departmentEnum != Department.NONE) {
                predicates.add(cb.equal(idRoot.get("department").get("departmentId"), departmentEnum.getId()));
            }
            predicates.add(cb.greaterThan(idRoot.get("remainingUnits"), 0L));
            idQuery.where(predicates.toArray(new Predicate[0]));
            // Create the query
            TypedQuery<Long> idTypedQuery = em.createQuery(idQuery);
            // Implement pagination in the first query
            if(page > 0){
                idTypedQuery.setFirstResult((page - 1) * size);
                idTypedQuery.setMaxResults(size);
            }
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
            // Create the query
            TypedQuery<Product> dataTypedQuery = em.createQuery(dataQuery);
            // Return Results
            return dataTypedQuery.getResultList();
        }
        return Collections.emptyList();
    }

    @Override
    public int countProducts(long neighborhoodId, String department, long userId, String productStatus) {
        LOGGER.debug("Selecting Products Count from neighborhood {}, in departments {}", neighborhoodId, department);

        if(userId != 0 || productStatus != null){
            ProductStatus productStatusEnum = ProductStatus.valueOf(productStatus);
            TypedQuery<Long> query = null;
            if(productStatusEnum == ProductStatus.BOUGHT){
                query = em.createQuery("SELECT COUNT(*) FROM Product p JOIN Purchase pu ON p.productId = pu.product.productId WHERE pu.user.userId = :userId", Long.class);
            }
            else if(productStatusEnum == ProductStatus.SOLD){
                query = em.createQuery("SELECT COUNT(*) FROM Product p JOIN Purchase pu ON p.productId = pu.product.productId WHERE p.seller.userId = :userId", Long.class);
            }
            else if(productStatusEnum == ProductStatus.SELLING){
                query = em.createQuery("SELECT COUNT(*) FROM Product p WHERE p.seller.userId = :userId", Long.class);
            }
            query.setParameter("userId", userId);
            return query.getSingleResult().intValue();
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        // First Query to retrieve product count
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        countQuery.select(cb.countDistinct(countRoot));
        // Add conditions for filtering
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(countRoot.get("seller").get("neighborhood").get("neighborhoodId"), neighborhoodId));
        if (department != null && Department.valueOf(department) != Department.NONE) {
            predicates.add(cb.equal(countRoot.get("department").get("departmentId"), Department.valueOf(department).getId()));
        }
        predicates.add(cb.greaterThan(countRoot.get("remainingUnits"), 0L));
        countQuery.where(predicates.toArray(new Predicate[0]));
        // Create the query
        TypedQuery<Long> countTypedQuery = em.createQuery(countQuery);
        // Result
        Long countResult = countTypedQuery.getSingleResult();
        // Return the count as an integer
        return countResult.intValue();
    }

}

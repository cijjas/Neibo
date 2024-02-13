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
import javax.persistence.Query;
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
    public Optional<Product> findProduct(long productId, long neighborhoodId) {
        LOGGER.debug("Selecting Product with productId {}, neighborhoodId {}", productId, neighborhoodId);

        TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE p.productId = :productId AND p.seller.neighborhood.neighborhoodId = :neighborhoodId",
                Product.class
        );

        query.setParameter("productId", productId);
        query.setParameter("neighborhoodId", neighborhoodId);

        List<Product> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Product> getProducts(long neighborhoodId, String department, Long userId, String productStatus, int page, int size) {
        LOGGER.debug("Selecting Products from neighborhood {}, in department {}", neighborhoodId, department);

        StringBuilder nativeQuery = new StringBuilder("SELECT p.* FROM products p " +
                "JOIN users u ON p.sellerid = u.userid " +
                "WHERE u.neighborhoodid = :neighborhoodId ");

        if (department != null) {
            nativeQuery.append("AND p.departmentid = :departmentId ");
        }

        if (userId != null && productStatus == null) {
            // Was bought by a user with this ID or is being sold by a user with this ID
            nativeQuery.append("AND (p.productid IN (" +
                    "SELECT DISTINCT p.productid FROM products_users_purchases p " +
                    "WHERE p.userid = :userId ) " +
                    "OR " +
                    "p.sellerid = :userId ) ");

        }

        if (productStatus != null) {
            switch (ProductStatus.valueOf(productStatus.toUpperCase())) {
                case BOUGHT:
                    nativeQuery.append("AND (p.productId IN (" +
                            "SELECT DISTINCT p.productid FROM products_users_purchases pc " +
                            "WHERE pc.userid = :userId AND p.remainingunits = 0)) ");
                    break;
                case SOLD:
                    nativeQuery.append("AND (p.productId IN (" +
                            "SELECT DISTINCT p.productid FROM products_users_purchases pc " +
                            "JOIN products pd ON pd.productid = pc.productid " +
                            "WHERE pd.sellerid = :userId AND p.remainingunits = 0)) ");
                    break;
                case SELLING:
                    nativeQuery.append("AND (p.productId IN (" +
                            "SELECT DISTINCT p.productid FROM products p " +
                            "WHERE p.sellerid = :userId AND p.remainingunits > 0)) ");
                    break;
            }
        }


        nativeQuery.append(" ORDER BY p.creationdate DESC");

        Query query = em.createNativeQuery(nativeQuery.toString(), Product.class);

        query.setParameter("neighborhoodId", neighborhoodId);

        if (department != null) {
            query.setParameter("departmentId", Department.valueOf(department.toUpperCase()).getId());
        }

        if (userId != null) {
            query.setParameter("userId", userId);
        }

        // No parameters have to be set for the ProductStatus Condition

        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public int countProducts(long neighborhoodId, String department, Long userId, String productStatus) {
        LOGGER.debug("Selecting Products Count from neighborhood {}, in department {}", neighborhoodId, department);

        StringBuilder nativeQuery = new StringBuilder("SELECT COUNT(DISTINCT p.productid) FROM products p " +
                "JOIN users u ON p.sellerid = u.userid " +
                "WHERE u.neighborhoodid = :neighborhoodId ");

        if (department != null) {
            nativeQuery.append("AND p.departmentid = :departmentId ");
        }

        if (userId != null && productStatus == null) {
            // Was bought by a user with this ID or is being sold by a user with this ID
            nativeQuery.append("AND (p.productid IN (" +
                    "SELECT DISTINCT p.productid FROM products_users_purchases p " +
                    "WHERE p.userid = :userId ) " +
                    "OR " +
                    "p.sellerid = :userId ) ");

        }

        if (productStatus != null) {
            switch (ProductStatus.valueOf(productStatus.toUpperCase())) {
                case BOUGHT:
                    nativeQuery.append("AND (p.productId IN (" +
                            "SELECT DISTINCT p.productid FROM products_users_purchases pc " +
                            "WHERE pc.userid = :userId AND p.remainingunits = 0)) ");
                    break;
                case SOLD:
                    nativeQuery.append("AND (p.productId IN (" +
                            "SELECT DISTINCT p.productid FROM products_users_purchases pc " +
                            "JOIN products pd ON pd.productid = pc.productid " +
                            "WHERE pd.sellerid = :userId AND p.remainingunits = 0)) ");
                    break;
                case SELLING:
                    nativeQuery.append("AND (p.productId IN (" +
                            "SELECT DISTINCT p.productid FROM products p " +
                            "WHERE p.sellerid = :userId AND p.remainingunits > 0)) ");
                    break;
            }
        }

        Query query = em.createNativeQuery(nativeQuery.toString());

        query.setParameter("neighborhoodId", neighborhoodId);

        if (department != null) {
            query.setParameter("departmentId", Department.valueOf(department.toUpperCase()).getId());
        }

        if (userId != null) {
            query.setParameter("userId", userId);
        }

        // No parameters have to be set for the ProductStatus Condition

        Object countResult = query.getSingleResult();

        return Integer.parseInt(countResult.toString());
    }
}

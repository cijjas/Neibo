package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.enums.ProductStatus;
import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductDaoImpl implements ProductDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // ------------------------------------------------ PRODUCT INSERT -------------------------------------------------

    @Override
    public Product createProduct(long userId, String name, String description, double price, Long units, boolean used, long departmentId, Long primaryPictureId, Long secondaryPictureId, Long tertiaryPictureId) {
        LOGGER.debug("Inserting Product {} with User Id {}", name, used);

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
                .creationDate(new Date(System.currentTimeMillis()))
                .remainingUnits(units)
                .build();

        em.persist(product);
        return product;
    }

    // ------------------------------------------------ PRODUCT SELECT -------------------------------------------------

    @Override
    public Optional<Product> findProduct(long neighborhoodId, long productId) {
        LOGGER.debug("Selecting Product with Neighborhood Id {} and Product Id {}", neighborhoodId, productId);

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
    public List<Product> getProducts(long neighborhoodId, Long userId, Long departmentId, Long productStatusId, int page, int size) {
        LOGGER.debug("Selecting Products with Neighborhood Id {}, User Id {}, Department Id {} and Product Status Id {}", neighborhoodId, userId, departmentId, productStatusId);

        StringBuilder nativeQuery = new StringBuilder("SELECT p.* FROM products p " +
                "JOIN users u ON p.sellerid = u.userid " +
                "WHERE u.neighborhoodid = :neighborhoodId ");

        if (departmentId != null) {
            nativeQuery.append("AND p.departmentid = :departmentId ");
        }

        if (userId != null) {
            if (productStatusId == null) {
                nativeQuery.append("AND (p.productid IN (" +
                        "SELECT DISTINCT r.productid FROM products_users_requests r " +
                        "WHERE r.userid = :userId ) " +
                        "OR " +
                        "p.sellerid = :userId ) ");
            } else {
                switch (ProductStatus.fromId(productStatusId).get()) { // Controller layer guarantees non-empty Optional
                    case BOUGHT:
                        nativeQuery.append("AND (p.productId IN (" +
                                "SELECT DISTINCT r.productid FROM products_users_requests r " +
                                "WHERE r.userid = :userId AND r.status like 'ACCEPTED')) ");
                        break;
                    case SOLD:
                        nativeQuery.append("AND p.sellerid = :userId AND (p.productId IN (" +
                                "SELECT DISTINCT r.productid FROM products_users_requests r " +
                                "WHERE r.status like 'ACCEPTED')) ");
                        break;
                    case SELLING:
                        nativeQuery.append("AND p.sellerid = :userId AND p.remainingunits > 0 ");
                        break;
                }
            }
        } else if (productStatusId != null) {
            switch (ProductStatus.fromId(productStatusId).get()) { // Controller layer guarantees non-empty Optional
                case BOUGHT:
                case SOLD:
                    nativeQuery.append("AND (p.productId IN (" +
                            "SELECT DISTINCT r.productid FROM products_users_requests r " +
                            "WHERE r.status like 'ACCEPTED')) ");
                    break;
                case SELLING:
                    nativeQuery.append("AND p.remainingunits > 0 ");
                    break;
            }
        }

        nativeQuery.append("ORDER BY p.creationdate DESC");

        Query query = em.createNativeQuery(nativeQuery.toString(), Product.class);

        query.setParameter("neighborhoodId", neighborhoodId);

        if (departmentId != null) {
            query.setParameter("departmentId", departmentId);
        }

        if (userId != null) {
            query.setParameter("userId", userId);
        }

        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public int countProducts(long neighborhoodId, Long userId, Long departmentId, Long productStatusId) {
        LOGGER.debug("Counting Products with Neighborhood Id {}, User Id {}, Department Id {} and Product Status Id {}", neighborhoodId, userId, departmentId, productStatusId);

        StringBuilder nativeQuery = new StringBuilder("SELECT COUNT(DISTINCT p.productid) FROM products p " +
                "JOIN users u ON p.sellerid = u.userid " +
                "WHERE u.neighborhoodid = :neighborhoodId ");

        if (departmentId != null) {
            nativeQuery.append("AND p.departmentid = :departmentId ");
        }

        if (userId != null) {
            if (productStatusId == null) {
                nativeQuery.append("AND (p.productid IN (" +
                        "SELECT DISTINCT r.productid FROM products_users_requests r " +
                        "WHERE r.userid = :userId ) " +
                        "OR " +
                        "p.sellerid = :userId ) ");
            } else {
                switch (ProductStatus.fromId(productStatusId).get()) {
                    case BOUGHT:
                        nativeQuery.append("AND (p.productId IN (" +
                                "SELECT DISTINCT r.productid FROM products_users_requests r " +
                                "WHERE r.userid = :userId AND r.status like 'ACCEPTED')) ");
                        break;
                    case SOLD:
                        nativeQuery.append("AND p.sellerid = :userId AND (p.productId IN (" +
                                "SELECT DISTINCT r.productid FROM products_users_requests r " +
                                "WHERE r.status like 'ACCEPTED')) ");
                        break;
                    case SELLING:
                        nativeQuery.append("AND p.sellerid = :userId AND p.remainingunits > 0 ");
                        break;
                }
            }
        } else if (productStatusId != null) {
            switch (ProductStatus.fromId(productStatusId).get()) {
                case BOUGHT:
                case SOLD:
                    nativeQuery.append("AND (p.productId IN (" +
                            "SELECT DISTINCT r.productid FROM products_users_requests r " +
                            "WHERE r.status like 'ACCEPTED')) ");
                    break;
                case SELLING:
                    nativeQuery.append("AND p.remainingunits > 0 ");
                    break;
            }
        }

        Query query = em.createNativeQuery(nativeQuery.toString());

        query.setParameter("neighborhoodId", neighborhoodId);

        if (departmentId != null) {
            query.setParameter("departmentId", departmentId);
        }
        if (userId != null) {
            query.setParameter("userId", userId);
        }

        Object countResult = query.getSingleResult();
        return Integer.parseInt(countResult.toString());
    }

    // --------------------------------------------- PRODUCTS DELETE ---------------------------------------------------

    @Override
    public boolean deleteProduct(long neighborhoodId, long productId) {
        LOGGER.debug("Deleting Product with Neighborhood Id {} and Product Id {}", neighborhoodId, productId);

        int deletedCount = em.createNativeQuery(
                        "DELETE FROM products p WHERE p.productId = :productId AND p.sellerId IN (SELECT u.userId FROM users u WHERE u.neighborhoodId = :neighborhoodId)"
                )
                .setParameter("productId", productId)
                .setParameter("neighborhoodId", neighborhoodId)
                .executeUpdate();

        return deletedCount > 0;
    }
}

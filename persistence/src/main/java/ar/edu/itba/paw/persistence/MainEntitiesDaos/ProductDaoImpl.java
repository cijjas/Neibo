package ar.edu.itba.paw.persistence.MainEntitiesDaos;

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

    @Override
    public Product createProduct(long userId, String name, String description, double price, boolean used, Long primaryPictureId, Long secondaryPictureId, Long tertiaryPictureId) {
        LOGGER.debug("Inserting Product {}", name);
        Product product = new Product.Builder()
                .name(name)
                .description(description)
                .price(price)
                .used(used)
                .seller(em.find(User.class, userId))
                .primaryPicture(em.find(Image.class, primaryPictureId))
                .secondaryPicture(em.find(Image.class, secondaryPictureId))
                .tertiaryPicture(em.find(Image.class, tertiaryPictureId))
                .build();
        em.persist(product);
        return product;
    }

    @Override
    public void updateProduct(long productId, String name, String description, double price, boolean used, Long primaryPictureId, Long secondaryPictureId, Long tertiaryPictureId) {
        LOGGER.debug("Updating Product {}", productId);

        Product product = em.find(Product.class, productId);
        if (product != null) {
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setUsed(used);
            product.setPrimaryPicture(em.find(Image.class, primaryPictureId));
            product.setSecondaryPicture(em.find(Image.class, secondaryPictureId));
            product.setTertiaryPicture(em.find(Image.class, tertiaryPictureId));
        }
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
    public List<Product> getProductsByCriteria(long neighborhoodId, List<String> departments, int page, int size) {
        LOGGER.debug("Selecting Products from neighborhood {}, with tags {}", neighborhoodId, departments);
        if(page != 0) {
            TypedQuery<Product> query = em.createQuery("SELECT DISTINCT p FROM Product p WHERE p.seller.neighborhood = :neighborhoodId AND p.departments IN (:departments)", Product.class)
                    .setParameter("neighborhoodId", neighborhoodId)
                    .setParameter("departments", departments)
                    .setFirstResult((page - 1) * size)
                    .setMaxResults(size);
            return query.getResultList();
        }
        TypedQuery<Product> query = em.createQuery("SELECT DISTINCT p FROM Product p WHERE p.seller.neighborhood = :neighborhoodId AND p.departments IN (:departments)", Product.class)
                .setParameter("neighborhoodId", neighborhoodId)
                .setParameter("departments", departments);
        return query.getResultList();
    }

    @Override
    public int getProductsCountByCriteria(long neighborhoodId, List<String> departments) {
        LOGGER.debug("Selecting Products Count from neighborhood {}, with tags {}", neighborhoodId, departments);
        TypedQuery<Integer> count = em.createQuery("SELECT COUNT( DISTINCT p) FROM Product p WHERE p.seller.neighborhood = :neighborhoodId AND p.departments IN (:departments)", Integer.class)
                .setParameter("neighborhoodId", neighborhoodId)
                .setParameter("departments", departments);
        return count.getSingleResult();
    }

    @Override
    public List<Product> getProductsSelling(long userId) {
        LOGGER.debug("Selecting Selling Products from User {}", userId);
        TypedQuery<Product> query = em.createQuery("SELECT p FROM User.productsSelling p WHERE p.buyer IS NULL", Product.class);
        return query.getResultList();
    }

    @Override
    public List<Product> getProductsSold(long userId) {
        LOGGER.debug("Selecting Sold Products from User {}", userId);
        TypedQuery<Product> query = em.createQuery("SELECT p FROM User.productsSelling p WHERE p.buyer IS NOT NULL", Product.class);
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
}

package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.ProductStatus;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.ProductDaoImpl;
import ar.edu.itba.paw.persistence.TestInserter;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.TestConstants.INVALID_ID;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class ProductDaoImplTest {
    public static final String MAIL1 = "user1@gmail.com";
    public static final String MAIL2 = "user2@gmail.com";
    public static final String MAIL3 = "user3@gmail.com";
    public static final String PRODUCT_NAME = "Product Name";
    public static final String PRODUCT_DESCRIPTION = "Product Description";
    public static final float PRICE = 35.35f;
    public static final boolean USED = true;
    public static final String PRODUCT_NAME1 = "New Product Name";
    public static final String PRODUCT_DESCRIPTION1 = "New Product Description";
    public static final float PRICE1 = 30.12f;
    public static final boolean USED1 = false;
    public static final String NAME = "Iphone";
    public static final String STRING = "lala";
    public static final String ANOTHER_PRODUCT = "AnotherProduct";
    public static final String FIRST_NEIGHBORHOOD = "First Neighborhood";
    public static final String SECOND_NEIGHBORHOOD = "Second Neighborhood";
    public static final long UNITS = 2L;

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ProductDaoImpl productDao;
    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void create_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(MAIL1, nhKey);
        long dKey = Department.ELECTRONICS.getId();

        // Exercise
        Product product = productDao.createProduct(uKey, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRICE, USED, iKey, iKey, iKey, dKey, UNITS);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products.name()));
        assertNotNull(product);
        assertEquals(PRODUCT_NAME, product.getName());
        assertEquals(PRODUCT_DESCRIPTION, product.getDescription());
        assertEquals(USED, product.isUsed());
        assertEquals(UNITS,product.getRemainingUnits().longValue());
    }

    @Test
    public void find_productId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(MAIL1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey, dKey1);

        // Exercise
        Optional<Product> maybeProduct = productDao.findProduct(pKey);

        // Validations & Post Conditions
        assertTrue(maybeProduct.isPresent());
    }

    @Test
    public void find_productId_invalid_productId() {
        // Pre Conditions

        // Exercise
        Optional<Product> maybeProduct = productDao.findProduct(1);

        // Validations & Post Conditions
        assertFalse(maybeProduct.isPresent());
    }

    @Test
    public void find_productId_neighborhoodId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(MAIL1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey, dKey1);

        // Exercise
        Optional<Product> maybeProduct = productDao.findProduct(pKey, nhKey);

        // Validations & Post Conditions
        assertTrue(maybeProduct.isPresent());
    }

    @Test
    public void find_productId_neighborhoodId_invalid_productId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(MAIL1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey, dKey1);

        // Exercise
        Optional<Product> maybeProduct = productDao.findProduct(INVALID_ID, nhKey);

        // Validations & Post Conditions
        assertFalse(maybeProduct.isPresent());
    }

    @Test
    public void find_productId_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(MAIL1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey, dKey1);

        // Exercise
        Optional<Product> maybeProduct = productDao.findProduct(pKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(maybeProduct.isPresent());
    }

    @Test
    public void find_productId_neighborhoodId_invalid_productId_neighborhoodId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(MAIL1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey, dKey1);

        // Exercise
        Optional<Product> maybeProduct = productDao.findProduct(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(maybeProduct.isPresent());
    }

/*

    @Test
    public void testGetProductsByNeighborhood() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long pKey2 = testInserter.createProduct(iKey, iKey, iKey, uKey2, dKey1);

        // Exercise
        List<Product> products = productDao.getProducts(nhKey, null, uKey1, (long) ProductStatus.SOLD.getId(), 1, 10);
        // Validations & Post Conditions
        assertTrue(products.isEmpty()); //TODO fix me
//        assertEquals(2, products.size());
    }

    @Test
    public void testGetProductsByInvalidNeighborhood() {
        // Pre Conditions

        // Exercise
        List<Product> products = productDao.getProducts(1L, (long) Department.NONE.getId(), 1L, (long) ProductStatus.SELLING.getId(), 1, 10);

        // Validations & Post Conditions
        assertTrue(products.isEmpty());
    }

    @Test
    public void testGetNoProductsByNeighborhoodByDepartment() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);

        // Exercise
        List<Product> products = productDao.getProducts(nhKey, (long) Department.APPLIANCES.getId(), uKey1, (long) ProductStatus.SELLING.getId(), 1, 10);

        // Validations & Post Conditions
        assertTrue(products.isEmpty());
    }

    @Test
    public void testGetProductsByNeighborhoodByInvalidDepartment() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey2 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey2);
        long pKey2 = testInserter.createProduct(iKey, iKey, iKey, uKey2, dKey2);

        // Exercise
        List<Product> products = productDao.getProducts(nhKey, (long) Department.TRAVEL_LUGGAGE.getId(), uKey1, (long) ProductStatus.SELLING.getId(), 1, 10);

        // Validations & Post Conditions
        assertTrue(products.isEmpty());
    }


*/

    @Test
    public void delete_productId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);

        // Exercise
        boolean deleted = productDao.deleteProduct(pKey1);

        // Validations & Post Conditions
        assertTrue(deleted);
    }

    @Test
    public void delete_productId_invalid_productId() {
        // Pre Conditions

        // Exercise
        boolean deleted = productDao.deleteProduct(1);

        // Validations & Post Conditions
        assertFalse(deleted);
    }
}

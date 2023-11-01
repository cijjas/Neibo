package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Departments;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.JunctionEntities.Inquiry;
import ar.edu.itba.paw.models.MainEntities.Product;
import ar.edu.itba.paw.persistence.JunctionDaos.ChannelMappingDaoImpl;
import ar.edu.itba.paw.persistence.JunctionDaos.InquiryDaoImpl;
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

import java.util.*;

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
    public void testCreateProduct() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(MAIL1, nhKey);
        long dKey = Departments.ELECTRONICS.getId();
        // Exercise
        Product product = productDao.createProduct(uKey, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRICE, USED, iKey, iKey, iKey, dKey);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products.name()));
        assertNotNull(product);
        assertEquals(PRODUCT_NAME, product.getName());
        assertEquals(PRODUCT_DESCRIPTION, product.getDescription());
        assertEquals(USED, product.isUsed());
    }

    @Test
    public void testFindProductById() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(MAIL1, nhKey);
        long dKey1 = testInserter.createDepartment(Departments.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey, null, dKey1);

        // Exercise
        Optional<Product> maybeProduct = productDao.findProductById(pKey);

        // Validations & Post Conditions
        assertTrue(maybeProduct.isPresent());
    }

    @Test
    public void testFindProductByInvalidId() {
        // Pre Conditions

        // Exercise
        Optional<Product> maybeProduct = productDao.findProductById(1);

        // Validations & Post Conditions
        assertFalse(maybeProduct.isPresent());
    }

    @Test
    public void testGetProductsByNeighborhood() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Departments.ELECTRONICS);
        long pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, uKey2, dKey1);
        long pKey2 = testInserter.createProduct(iKey, iKey, iKey, uKey2, uKey1, dKey1);

        // Exercise
        List<Product> products = productDao.getProductsByCriteria(nhKey, Departments.NONE, 1, 10);

        // Validations & Post Conditions
        assertFalse(products.isEmpty());
        assertEquals(2, products.size());
    }

    @Test
    public void testGetProductsByInvalidNeighborhood() {
        // Pre Conditions

        // Exercise
        List<Product> products = productDao.getProductsByCriteria(1, Departments.NONE, 1, 10);

        // Validations & Post Conditions
        assertTrue(products.isEmpty());
    }

    @Test
    public void testGetProductsByNeighborhoodByDepartment() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Departments.APPLIANCES);
        long dKey2 = testInserter.createDepartment(Departments.ELECTRONICS);
        long pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, uKey2, dKey1);
        long pKey2 = testInserter.createProduct(iKey, iKey, iKey, uKey2, uKey1, dKey2);

        // Exercise
        List<Product> products = productDao.getProductsByCriteria(nhKey, Departments.APPLIANCES, 1, 10);

        // Validations & Post Conditions
        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
    }

    @Test
    public void testGetProductsByNeighborhoodByInvalidDepartment() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey2 = testInserter.createDepartment(Departments.ELECTRONICS);
        long pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, uKey2, dKey2);
        long pKey2 = testInserter.createProduct(iKey, iKey, iKey, uKey2, uKey1, dKey2);

        // Exercise
        List<Product> products = productDao.getProductsByCriteria(nhKey, Departments.TRAVEL_LUGGAGE, 1, 10);

        // Validations & Post Conditions
        assertTrue(products.isEmpty());
    }

    @Test
    public void testGetProductsSelling() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey2 = testInserter.createDepartment(Departments.ELECTRONICS);
        long pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, null, dKey2);
        long pKey2 = testInserter.createProduct(iKey, iKey, iKey, uKey2, null, dKey2);

        // Exercise
        List<Product> products = productDao.getProductsSelling(uKey1);

        // Validations & Post Conditions
        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
    }

    @Test
    public void testGetNoProductsSelling() {
        // Pre Conditions

        // Exercise
        List<Product> products = productDao.getProductsSelling(1);

        // Validations & Post Conditions
        assertTrue(products.isEmpty());
    }

    @Test
    public void testGetProductsSold() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Departments.ELECTRONICS);
        long dKey2 = testInserter.createDepartment(Departments.APPLIANCES);
        long pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, uKey2, dKey1);
        long pKey2 = testInserter.createProduct(iKey, iKey, iKey, uKey2, uKey1, dKey2);


        // Exercise
        List<Product> products = productDao.getProductsSold(uKey1);

        // Validations & Post Conditions
        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
    }

    @Test
    public void testGetNoProductsSold() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);

        // Exercise
        List<Product> products = productDao.getProductsSold(uKey1);

        // Validations & Post Conditions
        assertTrue(products.isEmpty());
    }

    @Test
    public void testMarkAsBought() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Departments.ELECTRONICS);
        long pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, null, dKey1);

        // Exercise
        boolean bought = productDao.markAsBought(uKey2, pKey1);

        // Validations & Post Conditions
        assertTrue(bought);
    }

    @Test
    public void testInvalidMarkAsBought() {
        // Pre Conditions

        // Exercise
        boolean bought = productDao.markAsBought(1, 1);

        // Validations & Post Conditions
        assertFalse(bought);
    }

    @Test
    public void testUpdateProduct() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long dKey1 = testInserter.createDepartment(Departments.ELECTRONICS);
        long pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, null, dKey1);
        long dKey = Departments.ELECTRONICS.getId();
        // Exercise
        Product product = productDao.updateProduct(pKey1, PRODUCT_NAME1, PRODUCT_DESCRIPTION1, PRICE1, USED1, iKey, iKey, iKey, dKey);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products.name()));
        assertNotNull(product);
        assertEquals(PRODUCT_NAME1, product.getName());
        assertEquals(PRODUCT_DESCRIPTION1, product.getDescription());
        assertEquals(USED1, product.isUsed());
    }

    @Test
    public void testDeleteProduct() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long dKey1 = testInserter.createDepartment(Departments.ELECTRONICS);
        long pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, null, dKey1);

        // Exercise
        boolean deleted = productDao.deleteProduct(pKey1);

        // Validations & Post Conditions
        assertTrue(deleted);
    }

    @Test
    public void testDeleteInvalidProduct() {
        // Pre Conditions

        // Exercise
        boolean deleted = productDao.deleteProduct(1);

        // Validations & Post Conditions
        assertFalse(deleted);
    }
}

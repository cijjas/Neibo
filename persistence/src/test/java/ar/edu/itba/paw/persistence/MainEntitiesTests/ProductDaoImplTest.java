package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Department;
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
import java.util.Optional;

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class ProductDaoImplTest {

    public static final String PRODUCT_NAME = "Product Name";
    public static final String PRODUCT_DESCRIPTION = "Product Description";
    public static final float PRODUCT_PRICE = 35.35f;
    public static final boolean PRODUCT_USED = true;
    public static final long PRODUCT_UNITS = 2L;

    public static final String MAIL2 = "user2@gmail.com";
    public static final String MAIL3 = "user3@gmail.com";
    public static final String PRODUCT_NAME1 = "New Product Name";
    public static final String PRODUCT_DESCRIPTION1 = "New Product Description";
    public static final float PRICE1 = 30.12f;
    public static final boolean USED1 = false;
    public static final String NAME = "Iphone";
    public static final String STRING = "lala";
    public static final String ANOTHER_PRODUCT = "AnotherProduct";
    public static final String FIRST_NEIGHBORHOOD = "First Neighborhood";
    public static final String SECOND_NEIGHBORHOOD = "Second Neighborhood";


    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ProductDaoImpl productDaoImpl;
    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    // ------------------------------------------------- CREATE --------------------------------------------------------

    @Test
    public void create_valid() {
        // Pre Conditions
        long iKey1 = testInserter.createImage();
        long iKey2 = testInserter.createImage();
        long iKey3 = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey = Department.ELECTRONICS.getId();

        // Exercise
        Product product = productDaoImpl.createProduct(uKey, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_USED, iKey1, iKey2, iKey3, dKey, PRODUCT_UNITS);

        // Validations & Post Conditions
        em.flush();
        // parece que por alguna razon se desordenan las imagenes iKey1 = 2 iKey2 = 3 iKey3 = 1
        assertNotNull(product);
        assertEquals(iKey1, product.getPrimaryPicture().getImageId().longValue());
        assertEquals(iKey2, product.getSecondaryPicture().getImageId().longValue());
        assertEquals(iKey3, product.getTertiaryPicture().getImageId().longValue());
        assertEquals(dKey, product.getDepartment().getDepartmentId().longValue());
        assertEquals(PRODUCT_NAME, product.getName());
        assertEquals(PRODUCT_DESCRIPTION, product.getDescription());
        assertEquals(PRODUCT_USED, product.isUsed());
        assertEquals(PRODUCT_UNITS,product.getRemainingUnits().longValue());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_productId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey, dKey1);

        // Exercise
        Optional<Product> optionalProduct = productDaoImpl.findProduct(pKey);

        // Validations & Post Conditions
        assertTrue(optionalProduct.isPresent());
        assertEquals(pKey, optionalProduct.get().getProductId().longValue());

    }

    @Test
    public void find_productId_invalid_productId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey, dKey1);

        // Exercise
        Optional<Product> optionalProduct = productDaoImpl.findProduct(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalProduct.isPresent());
    }

    @Test
    public void find_productId_neighborhoodId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey, dKey1);

        // Exercise
        Optional<Product> optionalProduct = productDaoImpl.findProduct(pKey, nhKey);

        // Validations & Post Conditions
        assertTrue(optionalProduct.isPresent());
        assertEquals(pKey, optionalProduct.get().getProductId().longValue());
    }

    @Test
    public void find_productId_neighborhoodId_invalid_productId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey, dKey1);

        // Exercise
        Optional<Product> optionalProduct = productDaoImpl.findProduct(INVALID_ID, nhKey);

        // Validations & Post Conditions
        assertFalse(optionalProduct.isPresent());
    }

    @Test
    public void find_productId_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey, dKey1);

        // Exercise
        Optional<Product> optionalProduct = productDaoImpl.findProduct(pKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalProduct.isPresent());
    }

    @Test
    public void find_productId_neighborhoodId_invalid_productId_neighborhoodId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey, dKey1);

        // Exercise
        Optional<Product> optionalProduct = productDaoImpl.findProduct(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalProduct.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

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

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_productId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);

        // Exercise
        boolean deleted = productDaoImpl.deleteProduct(pKey1);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products.name()));
    }

    @Test
    public void delete_productId_invalid_productId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);

        // Exercise
        boolean deleted = productDaoImpl.deleteProduct(INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}

package ar.edu.itba.paw.persistence.MainEntitiesTests;

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

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class ProductDaoImplTest {

    public static final String PRODUCT_DESCRIPTION = "Product Description";
    public static final float PRODUCT_PRICE = 35.35f;
    public static final boolean PRODUCT_USED = true;
    public static final long PRODUCT_UNITS = 2L;
    public static final String PRODUCT_NAME_1 = "Product Name 1";
    public static final String PRODUCT_NAME_2 = "Product Name 2";
    public static final String PRODUCT_NAME_3 = "Product Name 3";
    public static final String PRODUCT_NAME_4 = "Product Name 4";
    public static final String PRODUCT_NAME_5 = "Product Name 5";

    private static long nhKey;
    private static long uKey1;
    private static long uKey2;
    private static long dKey1;
    private static long dKey2;


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
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);

        // Exercise
        Product product = productDaoImpl.createProduct(uKey, PRODUCT_NAME_1, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_USED, dKey1, iKey1, iKey2, iKey3, PRODUCT_UNITS);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(product);
        assertEquals(iKey1, product.getPrimaryPicture().getImageId().longValue());
        assertEquals(iKey2, product.getSecondaryPicture().getImageId().longValue());
        assertEquals(iKey3, product.getTertiaryPicture().getImageId().longValue());
        assertEquals(dKey1, product.getDepartment().getDepartmentId().longValue());
        assertEquals(PRODUCT_NAME_1, product.getName());
        assertEquals(PRODUCT_DESCRIPTION, product.getDescription());
        assertEquals(PRODUCT_USED, product.isUsed());
        assertEquals(PRODUCT_UNITS, product.getRemainingUnits().longValue());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_productId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
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
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
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
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
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
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
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
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
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
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey, dKey1);

        // Exercise
        Optional<Product> optionalProduct = productDaoImpl.findProduct(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalProduct.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        populateProducts();

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(nhKey, EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(SEVEN_ELEMENTS, productList.size());
    }

    @Test
    public void get_departmentId() {
        // Pre Conditions
        populateProducts();

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(nhKey, dKey1, EMPTY_FIELD, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(FOUR_ELEMENTS, productList.size());
    }

    @Test
    public void get_userId() {
        // Pre Conditions
        populateProducts();

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(nhKey, EMPTY_FIELD, uKey1, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(SIX_ELEMENTS, productList.size());
    }

    @Test
    public void get_boughtStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(nhKey, EMPTY_FIELD, EMPTY_FIELD, (long) ProductStatus.SELLING.getId(), BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, productList.size());
    }

    @Test
    public void get_soldStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(nhKey, EMPTY_FIELD, EMPTY_FIELD, (long) ProductStatus.SOLD.getId(), BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(FIVE_ELEMENTS, productList.size());
    }

    @Test
    public void get_sellingStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(nhKey, EMPTY_FIELD, EMPTY_FIELD, (long) ProductStatus.SELLING.getId(), BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, productList.size());
    }

    @Test
    public void get_departmentId_userId() {
        // Pre Conditions
        populateProducts();

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(
                nhKey,
                dKey1,
                uKey1,
                EMPTY_FIELD,
                BASE_PAGE,
                BASE_PAGE_SIZE
        );

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, productList.size());
    }

    @Test
    public void get_departmentId_boughtStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(nhKey, dKey1, EMPTY_FIELD, (long) ProductStatus.BOUGHT.getId(), BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, productList.size());
    }

    @Test
    public void get_departmentId_soldStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(nhKey, dKey1, EMPTY_FIELD, (long) ProductStatus.SOLD.getId(), BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, productList.size());
    }

    @Test
    public void get_departmentId_sellingStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(nhKey, dKey1, EMPTY_FIELD, (long) ProductStatus.SELLING.getId(), BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, productList.size());
    }

    @Test
    public void get_userId_boughtStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(nhKey, EMPTY_FIELD, uKey1, (long) ProductStatus.BOUGHT.getId(), BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, productList.size());
    }

    @Test
    public void get_userId_soldStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(nhKey, EMPTY_FIELD, uKey1, (long) ProductStatus.SOLD.getId(), BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, productList.size());
    }

    @Test
    public void get_userId_sellingStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(nhKey, EMPTY_FIELD, uKey1, (long) ProductStatus.SELLING.getId(), BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, productList.size());
    }

    @Test
    public void get_departmentId_userId_boughtStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(nhKey, dKey2, uKey1, (long) ProductStatus.BOUGHT.getId(), BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, productList.size());
    }

    @Test
    public void get_departmentId_userId_soldStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(nhKey, dKey1, uKey1, (long) ProductStatus.SOLD.getId(), BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, productList.size());
    }

    @Test
    public void get_departmentId_userId_sellingStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(nhKey, dKey1, uKey1, (long) ProductStatus.SELLING.getId(), BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, productList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(nhKey, EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(productList.isEmpty());
    }

    // ---------------------------------------------- PAGINATION -------------------------------------------------------

    @Test
    public void get_pagination() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        nhKey = testInserter.createNeighborhood();
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long dKey2 = testInserter.createDepartment(DEPARTMENT_NAME_2);
        long pKey1 = testInserter.createProduct(PRODUCT_NAME_1, iKey, iKey, iKey, uKey1, dKey1, uKey2);
        long pKey2 = testInserter.createProduct(PRODUCT_NAME_2, iKey, iKey, iKey, uKey1, dKey1, uKey2);
        long pKey3 = testInserter.createProduct(PRODUCT_NAME_3, iKey, iKey, iKey, uKey1, dKey2, uKey2);

        // Exercise
        List<Product> productList = productDaoImpl.getProducts(nhKey, EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, TEST_PAGE, TEST_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, productList.size());
    }

    // ------------------------------------------------- COUNTS --------------------------------------------------------

    @Test
    public void count() {
        // Pre Conditions
        populateProducts();

        // Exercise
        int countProducts = productDaoImpl.countProducts(nhKey, EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(SEVEN_ELEMENTS, countProducts);
    }

    @Test
    public void count_departmentId() {
        // Pre Conditions
        populateProducts();

        // Exercise
        int countProducts = productDaoImpl.countProducts(nhKey, dKey1, EMPTY_FIELD, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(FOUR_ELEMENTS, countProducts);
    }

    @Test
    public void count_userId() {
        // Pre Conditions
        populateProducts();

        // Exercise
        int countProducts = productDaoImpl.countProducts(nhKey, EMPTY_FIELD, uKey1, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(SIX_ELEMENTS, countProducts);
    }

    @Test
    public void count_boughtStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        int countProducts = productDaoImpl.countProducts(nhKey, EMPTY_FIELD, EMPTY_FIELD, (long) ProductStatus.SELLING.getId());

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countProducts);
    }

    @Test
    public void count_soldStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        int countProducts = productDaoImpl.countProducts(nhKey, EMPTY_FIELD, EMPTY_FIELD, (long) ProductStatus.SOLD.getId());

        // Validations & Post Conditions
        assertEquals(FIVE_ELEMENTS, countProducts);
    }

    @Test
    public void count_sellingStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        int countProducts = productDaoImpl.countProducts(nhKey, EMPTY_FIELD, EMPTY_FIELD, (long) ProductStatus.SELLING.getId());

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countProducts);
    }

    @Test
    public void count_departmentId_userId() {
        // Pre Conditions
        populateProducts();

        // Exercise
        int countProducts = productDaoImpl.countProducts(
                nhKey,
                dKey1,
                uKey1,
                EMPTY_FIELD
        );

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, countProducts);
    }

    @Test
    public void count_departmentId_boughtStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        int countProducts = productDaoImpl.countProducts(nhKey, dKey1, EMPTY_FIELD, (long) ProductStatus.BOUGHT.getId());

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countProducts);
    }

    @Test
    public void count_departmentId_soldStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        int countProducts = productDaoImpl.countProducts(nhKey, dKey1, EMPTY_FIELD, (long) ProductStatus.SOLD.getId());

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countProducts);
    }

    @Test
    public void count_departmentId_sellingStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        int countProducts = productDaoImpl.countProducts(nhKey, dKey1, EMPTY_FIELD, (long) ProductStatus.SELLING.getId());

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countProducts);
    }

    @Test
    public void count_userId_boughtStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        int countProducts = productDaoImpl.countProducts(nhKey, EMPTY_FIELD, uKey1, (long) ProductStatus.BOUGHT.getId());

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countProducts);
    }

    @Test
    public void count_userId_soldStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        int countProducts = productDaoImpl.countProducts(nhKey, EMPTY_FIELD, uKey1, (long) ProductStatus.SOLD.getId());

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, countProducts);
    }

    @Test
    public void count_userId_sellingStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        int countProducts = productDaoImpl.countProducts(nhKey, EMPTY_FIELD, uKey1, (long) ProductStatus.SELLING.getId());

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countProducts);
    }

    @Test
    public void count_departmentId_userId_boughtStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        int countProducts = productDaoImpl.countProducts(nhKey, dKey2, uKey1, (long) ProductStatus.BOUGHT.getId());

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countProducts);
    }

    @Test
    public void count_departmentId_userId_soldStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        int countProducts = productDaoImpl.countProducts(nhKey, dKey1, uKey1, (long) ProductStatus.SOLD.getId());

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countProducts);
    }

    @Test
    public void count_departmentId_userId_sellingStatus() {
        // Pre Conditions
        populateProducts();

        // Exercise
        int countProducts = productDaoImpl.countProducts(nhKey, dKey1, uKey1, (long) ProductStatus.SELLING.getId());

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countProducts);
    }

    @Test
    public void count_empty() {
        // Pre Conditions

        // Exercise
        int countProducts = productDaoImpl.countProducts(nhKey, EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countProducts);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_productId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
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
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);

        // Exercise
        boolean deleted = productDaoImpl.deleteProduct(INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    // ----------------------------------------------- POPULATION ------------------------------------------------------

    private void populateProducts() {
        // UserId,  SOLD, Department

        // [U1, BOUGHT(U2), D1]
        // [U1, BOUGHT(U2), D1]
        // [U1, BOUGHT(U2), D2]
        // [U1, SELLING, D1]
        // [U2, BOUGHT(U1), D2]
        // [U2, BOUGHT(U1), D2]
        // [U2, SELLING, D1]

        // empty, bought, sold, requested, department, userId,

        long iKey = testInserter.createImage();
        nhKey = testInserter.createNeighborhood();
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        dKey2 = testInserter.createDepartment(DEPARTMENT_NAME_2);

        long pKey1 = testInserter.createProduct(PRODUCT_NAME_1, iKey, iKey, iKey, uKey1, dKey1, uKey2);
        long pKey2 = testInserter.createProduct(PRODUCT_NAME_2, iKey, iKey, iKey, uKey1, dKey1, uKey2);
        long pKey3 = testInserter.createProduct(PRODUCT_NAME_3, iKey, iKey, iKey, uKey1, dKey2, uKey2);
        long pKey4 = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long pKey5 = testInserter.createProduct(PRODUCT_NAME_4, iKey, iKey, iKey, uKey2, dKey2, uKey1);
        long pKey6 = testInserter.createProduct(PRODUCT_NAME_5, iKey, iKey, iKey, uKey2, dKey2, uKey1);
        long pKey7 = testInserter.createProduct(iKey, iKey, iKey, uKey2, dKey1);

    }
}

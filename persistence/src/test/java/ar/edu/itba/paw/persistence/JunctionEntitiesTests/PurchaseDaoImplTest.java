package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Purchase;
import ar.edu.itba.paw.persistence.JunctionDaos.PurchaseDaoImpl;
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
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class PurchaseDaoImplTest {
    public static final int UNITS_BOUGHT = 2;
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PurchaseDaoImpl purchaseDao;
    @PersistenceContext
    private EntityManager em;

    public static final String MAIL1 = "user1@gmail.com";
    public static final String MAIL2 = "user2@gmail.com";
    public static final String MAIL3 = "user3@gmail.com";
    public static final int BASE_PAGE = 1;
    public static final int BASE_SIZE = 10;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }
    @Test
    public void testCreatePurchase() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long uKey3 = testInserter.createUser(MAIL3, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, null, dKey1);

        // Exercise
        Purchase purchase = purchaseDao.createPurchase(pKey, uKey1, UNITS_BOUGHT);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(purchase);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_purchases.name()));
    }

    @Test
    public void testFindPurchase() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long uKey3 = testInserter.createUser(MAIL3, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, uKey2, dKey1);
        long pcKey = testInserter.createPurchase(pKey, uKey1, UNITS_BOUGHT);

        // Exercise
        Optional<Purchase> purchase = purchaseDao.findPurchase(pcKey);

        // Validations & Post Conditions
        assertTrue(purchase.isPresent());
    }

    @Test
    public void testFindPurchaseInvalidId() {
        // Pre Conditions

        // Exercise
        Optional<Purchase> purchase = purchaseDao.findPurchase(1);

        // Validations & Post Conditions
        assertFalse(purchase.isPresent());
    }

    @Test
    public void testGetPurchasesBySellerId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long uKey3 = testInserter.createUser(MAIL3, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, null, dKey1);
        long pcKey = testInserter.createPurchase(pKey, uKey2, UNITS_BOUGHT);

        // Exercise
        Set<Purchase> purchase = purchaseDao.getPurchasesBySellerId(uKey1, BASE_PAGE, BASE_SIZE);

        // Validations & Post Conditions
        assertFalse(purchase.isEmpty());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_purchases.name()));
    }

    @Test
    public void testGetNoPurchasesBySellerId() {
        // Pre Conditions

        // Exercise
        Set<Purchase> purchase = purchaseDao.getPurchasesBySellerId(1, BASE_PAGE, BASE_SIZE);

        // Validations & Post Conditions
        assertTrue(purchase.isEmpty());
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_purchases.name()));
    }

    @Test
    public void testGetPurchasesByBuyerId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long uKey3 = testInserter.createUser(MAIL3, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, null, dKey1);
        long pcKey = testInserter.createPurchase(pKey, uKey2, UNITS_BOUGHT);

        // Exercise
        Set<Purchase> purchase = purchaseDao.getPurchasesByBuyerId(uKey2, BASE_PAGE, BASE_SIZE);

        // Validations & Post Conditions
        assertFalse(purchase.isEmpty());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_purchases.name()));
    }

    @Test
    public void testGetPurchasesByBuyerInvalidId() {
        // Pre Conditions

        // Exercise
        Set<Purchase> purchase = purchaseDao.getPurchasesByBuyerId(1, BASE_PAGE, BASE_SIZE);

        // Validations & Post Conditions
        assertTrue(purchase.isEmpty());
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_purchases.name()));
    }
}

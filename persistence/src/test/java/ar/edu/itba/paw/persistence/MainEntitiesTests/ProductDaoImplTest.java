package ar.edu.itba.paw.persistence.MainEntitiesTests;

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

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class ProductDaoImplTest {
    public static final String MAIL1 = "user1@gmail.com";
    public static final String MAIL2 = "user2@gmail.com";
    public static final String MAIL3 = "user3@gmail.com";

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
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long uKey3 = testInserter.createUser(MAIL3, nhKey);

        // Exercise
//        Product product = productDao.createProduct(uKey1, iKey, iKey, iKey, uKey1, uKey2);

        // Validations & Post Conditions

    }

    @Test
    public void testFindProductById() {
        // Pre Conditions

        // Exercise

        // Validations & Post Conditions
    }

    @Test
    public void testFindProductByInvalidId() {
        // Pre Conditions

        // Exercise

        // Validations & Post Conditions
    }

    @Test
    public void testGetProductsByNeighborhood() {
        // Pre Conditions

        // Exercise

        // Validations & Post Conditions
    }

    @Test
    public void testGetProductsByInvalidNeighborhood() {
        // Pre Conditions

        // Exercise

        // Validations & Post Conditions
    }

    @Test
    public void testGetProductsByNeighborhoodByDepartment() {
        // Pre Conditions

        // Exercise

        // Validations & Post Conditions
    }

    @Test
    public void testGetProductsSelling() {
        // Pre Conditions

        // Exercise

        // Validations & Post Conditions
    }

    @Test
    public void testGetProductsSold() {
        // Pre Conditions

        // Exercise

        // Validations & Post Conditions
    }

    @Test
    public void testGetProductsBought() {
        // Pre Conditions

        // Exercise

        // Validations & Post Conditions
    }

    @Test
    public void testMarkAsBought() {
        // Pre Conditions

        // Exercise

        // Validations & Post Conditions
    }

    @Test
    public void testUpdateProduct() {
        // Pre Conditions

        // Exercise

        // Validations & Post Conditions
    }

    @Test
    public void testDeleteProduct() {
        // Pre Conditions

        // Exercise

        // Validations & Post Conditions
    }
}

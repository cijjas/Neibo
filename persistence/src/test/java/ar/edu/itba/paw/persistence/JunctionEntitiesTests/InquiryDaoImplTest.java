package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Inquiry;
import ar.edu.itba.paw.persistence.JunctionDaos.InquiryDaoImpl;
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
public class InquiryDaoImplTest {

    public static final String MESSAGE = "message";
    public static final String MAIL1 = "user1@gmail.com";
    public static final String MAIL2 = "user2@gmail.com";
    public static final String MAIL3 = "user3@gmail.com";
    public static final String REPLY = "This is a reply";

    private static final int BASE_PAGE = 1;
    private static final int BASE_PAGE_SIZE = 10;

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private InquiryDaoImpl inquiryDao;
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
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);

        // Exercise
        Inquiry inquiry = inquiryDao.createInquiry(uKey2, pKey, MESSAGE);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(inquiry);
        assertEquals(inquiry.getMessage(), MESSAGE);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_inquiries.name()));
    }

    @Test
    public void find_inquiryId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> maybeInquiry = inquiryDao.findInquiry(iqKey);

        // Validations & Post Conditions
        assertTrue(maybeInquiry.isPresent());
    }

    @Test
    public void find_inquiryId_invalid_inquiryId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        List<Inquiry> inquiries = inquiryDao.getInquiries(pKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertFalse(inquiries.isEmpty());
    }

       @Test
    public void find_inquiryId_productId_neighborhoodId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> maybeInquiry = inquiryDao.findInquiry(iqKey, pKey, nhKey);

        // Validations & Post Conditions
        assertTrue(maybeInquiry.isPresent());
    }

    @Test
    public void find_inquiryId_productId_neighborhoodId_invalid_inquiryId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> maybeInquiry = inquiryDao.findInquiry(INVALID_ID, pKey, nhKey);

        // Validations & Post Conditions
        assertFalse(maybeInquiry.isPresent());
    }

    @Test
    public void find_inquiryId_productId_neighborhoodId_invalid_productId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> maybeInquiry = inquiryDao.findInquiry(iqKey, INVALID_ID, nhKey);

        // Validations & Post Conditions
        assertFalse(maybeInquiry.isPresent());
    }

    @Test
    public void find_inquiryId_productId_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> maybeInquiry = inquiryDao.findInquiry(iqKey, pKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(maybeInquiry.isPresent());
    }

    @Test
    public void find_inquiryId_productId_neighborhoodId_invalid_inquiryId_productId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> maybeInquiry = inquiryDao.findInquiry(INVALID_ID, INVALID_ID, nhKey);

        // Validations & Post Conditions
        assertFalse(maybeInquiry.isPresent());
    }

    @Test
    public void find_inquiryId_productId_neighborhoodId_invalid_inquiryId_neighborhoodId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> maybeInquiry = inquiryDao.findInquiry(INVALID_ID, pKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(maybeInquiry.isPresent());
    }

    @Test
    public void find_inquiryId_productId_neighborhoodId_invalid_productId_neighborhoodId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> maybeInquiry = inquiryDao.findInquiry(iqKey, INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(maybeInquiry.isPresent());
    }

    @Test
    public void find_inquiryId_productId_neighborhoodId_invalid_inquiryId_productId_neighborhoodId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> maybeInquiry = inquiryDao.findInquiry(iqKey, INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(maybeInquiry.isPresent());
    }

    @Test
    public void delete_inquiryId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        boolean deleted = inquiryDao.deleteInquiry(iqKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_inquiries.name()));
    }

    @Test
    public void delete_inquiryId_invalid_inquiryId() {
        // Pre Conditions

        // Exercise
        boolean deleted = inquiryDao.deleteInquiry(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_inquiries.name()));
    }
}

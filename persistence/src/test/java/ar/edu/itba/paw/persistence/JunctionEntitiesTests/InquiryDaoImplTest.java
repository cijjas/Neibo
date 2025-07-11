package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

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

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class InquiryDaoImplTest {

    public static final String INQUIRY_MESSAGE = "message";

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private InquiryDaoImpl inquiryDaoImpl;
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
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);

        // Exercise
        Inquiry inquiry = inquiryDaoImpl.createInquiry(uKey2, pKey, INQUIRY_MESSAGE);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(inquiry);
        assertEquals(uKey2, inquiry.getUser().getUserId().longValue());
        assertEquals(pKey, inquiry.getProduct().getProductId().longValue());
        assertEquals(INQUIRY_MESSAGE, inquiry.getMessage());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_inquiries.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_neighborhoodId_productId_inquiryId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(nhKey, pKey, iqKey);

        // Validations & Post Conditions
        assertTrue(optionalInquiry.isPresent());
        assertEquals(iqKey, optionalInquiry.get().getInquiryId().longValue());
    }

    @Test
    public void find_neighborhoodId_productId_inquiryId_invalid_inquiryId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(nhKey, pKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalInquiry.isPresent());
    }

    @Test
    public void find_neighborhoodId_productId_inquiryId_invalid_productId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(nhKey, INVALID_ID, iqKey);

        // Validations & Post Conditions
        assertFalse(optionalInquiry.isPresent());
    }

    @Test
    public void find_neighborhoodId_productId_inquiryId_invalid_neighborhoodId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(INVALID_ID, pKey, iqKey);

        // Validations & Post Conditions
        assertFalse(optionalInquiry.isPresent());
    }

    @Test
    public void find_neighborhoodId_productId_inquiryId_invalid_productId_inquiryId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(nhKey, INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalInquiry.isPresent());
    }

    @Test
    public void find_neighborhoodId_productId_inquiryId_invalid_neighborhoodId_inquiryId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(INVALID_ID, pKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalInquiry.isPresent());
    }

    @Test
    public void find_neighborhoodId_productId_inquiryId_invalid_neighborhoodId_productId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(INVALID_ID, INVALID_ID, iqKey);

        // Validations & Post Conditions
        assertFalse(optionalInquiry.isPresent());
    }

    @Test
    public void find_neighborhoodId_productId_inquiryId_invalid_neighborhoodId_productId_inquiryId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(INVALID_ID, INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalInquiry.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey1 = testInserter.createInquiry(pKey, uKey2);
        long iqKey2 = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        List<Inquiry> inquiryList = inquiryDaoImpl.getInquiries(pKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, inquiryList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);

        // Exercise
        List<Inquiry> inquiryList = inquiryDaoImpl.getInquiries(pKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(inquiryList.isEmpty());
    }

    // ---------------------------------------------- PAGINATION -------------------------------------------------------

    @Test
    public void get_pagination() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long uKey3 = testInserter.createUser(USER_MAIL_3, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey1 = testInserter.createInquiry(pKey, uKey2);
        long iqKey2 = testInserter.createInquiry(pKey, uKey2);
        long iqKey3 = testInserter.createInquiry(pKey, uKey3);

        // Exercise
        List<Inquiry> inquiryList = inquiryDaoImpl.getInquiries(pKey, TEST_PAGE, TEST_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, inquiryList.size());
    }

    // ------------------------------------------------- COUNTS ---------------------------------------------------------

    @Test
    public void count() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey1 = testInserter.createInquiry(pKey, uKey2);
        long iqKey2 = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        int countInquiries = inquiryDaoImpl.countInquiries(pKey);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countInquiries);
    }

    @Test
    public void count_empty() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);

        // Exercise
        int countInquiries = inquiryDaoImpl.countInquiries(pKey);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countInquiries);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_neighborhoodId_productId_inquiryId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        boolean deleted = inquiryDaoImpl.deleteInquiry(nhKey, pKey, iqKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_inquiries.name()));
    }

    @Test
    public void delete_neighborhoodId_productId_inquiryId_invalid_neighborhoodId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        boolean deleted = inquiryDaoImpl.deleteInquiry(INVALID_ID, pKey, iqKey);

        // Validations & Post Conditions
        assertFalse(deleted);
    }

    @Test
    public void delete_neighborhoodId_productId_inquiryId_invalid_productId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        boolean deleted = inquiryDaoImpl.deleteInquiry(nhKey, INVALID_ID, iqKey);

        // Validations & Post Conditions
        assertFalse(deleted);
    }

    @Test
    public void delete_neighborhoodId_productId_inquiryId_invalid_inquiryId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        boolean deleted = inquiryDaoImpl.deleteInquiry(nhKey, pKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(deleted);
    }

    @Test
    public void delete_neighborhoodId_productId_inquiryId_invalid_neighborhoodId_and_productId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        boolean deleted = inquiryDaoImpl.deleteInquiry(INVALID_ID, INVALID_ID, iqKey);

        // Validations & Post Conditions
        assertFalse(deleted);
    }

    @Test
    public void delete_neighborhoodId_productId_inquiryId_invalid_neighborhoodId_and_inquiryId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        boolean deleted = inquiryDaoImpl.deleteInquiry(INVALID_ID, pKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(deleted);
    }

    @Test
    public void delete_neighborhoodId_productId_inquiryId_invalid_productId_and_inquiryId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        boolean deleted = inquiryDaoImpl.deleteInquiry(nhKey, INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(deleted);
    }

    @Test
    public void delete_neighborhoodId_productId_inquiryId_all_invalid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        boolean deleted = inquiryDaoImpl.deleteInquiry(INVALID_ID, INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(deleted);
    }
}

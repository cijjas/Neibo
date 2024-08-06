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
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);

        // Exercise
        Inquiry inquiry = inquiryDaoImpl.createInquiry(uKey2, pKey, INQUIRY_MESSAGE);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(inquiry);
        assertEquals(uKey2, inquiry.getUser().getUserId().longValue());
        assertEquals(pKey, inquiry.getProduct().getProductId().longValue());
        assertEquals(inquiry.getMessage(), INQUIRY_MESSAGE);
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_inquiries.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_inquiryId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(iqKey);

        // Validations & Post Conditions
        assertTrue(optionalInquiry.isPresent());
        assertEquals(iqKey, optionalInquiry.get().getInquiryId().longValue());
    }

    @Test
    public void find_inquiryId_invalid_inquiryId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalInquiry.isPresent());
    }

       @Test
    public void find_inquiryId_productId_neighborhoodId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(iqKey, pKey, nhKey);

        // Validations & Post Conditions
        assertTrue(optionalInquiry.isPresent());
        assertEquals(iqKey, optionalInquiry.get().getInquiryId().longValue());
    }

    @Test
    public void find_inquiryId_productId_neighborhoodId_invalid_inquiryId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(INVALID_ID, pKey, nhKey);

        // Validations & Post Conditions
        assertFalse(optionalInquiry.isPresent());
    }

    @Test
    public void find_inquiryId_productId_neighborhoodId_invalid_productId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(iqKey, INVALID_ID, nhKey);

        // Validations & Post Conditions
        assertFalse(optionalInquiry.isPresent());
    }

    @Test
    public void find_inquiryId_productId_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(iqKey, pKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalInquiry.isPresent());
    }

    @Test
    public void find_inquiryId_productId_neighborhoodId_invalid_inquiryId_productId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(INVALID_ID, INVALID_ID, nhKey);

        // Validations & Post Conditions
        assertFalse(optionalInquiry.isPresent());
    }

    @Test
    public void find_inquiryId_productId_neighborhoodId_invalid_inquiryId_neighborhoodId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(INVALID_ID, pKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalInquiry.isPresent());
    }

    @Test
    public void find_inquiryId_productId_neighborhoodId_invalid_productId_neighborhoodId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(iqKey, INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalInquiry.isPresent());
    }

    @Test
    public void find_inquiryId_productId_neighborhoodId_invalid_inquiryId_productId_neighborhoodId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        Optional<Inquiry> optionalInquiry = inquiryDaoImpl.findInquiry(iqKey, INVALID_ID, INVALID_ID);

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
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
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
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);

        // Exercise
        List<Inquiry> inquiryList = inquiryDaoImpl.getInquiries(pKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(inquiryList.isEmpty());
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_inquiryId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        boolean deleted = inquiryDaoImpl.deleteInquiry(iqKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_inquiries.name()));
    }

    @Test
    public void delete_inquiryId_invalid_inquiryId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey2);

        // Exercise
        boolean deleted = inquiryDaoImpl.deleteInquiry(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(deleted);
    }
}

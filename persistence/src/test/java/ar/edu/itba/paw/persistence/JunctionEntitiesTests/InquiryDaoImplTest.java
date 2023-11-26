package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.JunctionEntities.Inquiry;
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
    public void testCreateInquiry() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long uKey3 = testInserter.createUser(MAIL3, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, uKey2, dKey1);

        // Exercise
        Inquiry inquiry = inquiryDao.createInquiry(uKey3, pKey, MESSAGE);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(inquiry);
        assertEquals(inquiry.getMessage(), MESSAGE);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_inquiries.name()));
    }

    @Test
    public void testFindInquiryById() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long uKey3 = testInserter.createUser(MAIL3, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, uKey2, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey3);

        // Exercise
        Optional<Inquiry> maybeInquiry = inquiryDao.findInquiryById(iqKey);

        // Validations & Post Conditions
        assertTrue(maybeInquiry.isPresent());
    }

    @Test
    public void testFindInquiryByInvalidId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long uKey3 = testInserter.createUser(MAIL3, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, uKey2, dKey1);
        long iqKey = testInserter.createInquiry(pKey, uKey3);

        // Exercise
        List<Inquiry> inquiries = inquiryDao.getInquiriesByProduct(pKey);

        // Validations & Post Conditions
        assertFalse(inquiries.isEmpty());
    }

    @Test
    public void testGetInquiriesByProduct() {
        // Pre Conditions

        // Exercise
        Optional<Inquiry> maybeInquiry = inquiryDao.findInquiryById(1);

        // Validations & Post Conditions
        assertFalse(maybeInquiry.isPresent());
    }
}

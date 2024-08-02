package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Request;
import ar.edu.itba.paw.persistence.JunctionDaos.RequestDaoImpl;
import ar.edu.itba.paw.persistence.TestConstants;
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
public class RequestDaoImplTest {

    public static final String REQUEST_MESSAGE = "hola";
    public static final int REQUEST_QUANTITY = 1;

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RequestDaoImpl requestDaoImpl;
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
        long uKey1 = testInserter.createUser(TestConstants.USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(TestConstants.USER_MAIL_2, nhKey);
        long uKey3 = testInserter.createUser(USER_MAIL_3, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);

        // Exercise
        Request request = requestDaoImpl.createRequest(uKey3, pKey, REQUEST_MESSAGE, REQUEST_QUANTITY); //TODO: arreglar quantity?

        // Validations & Post Conditions
        em.flush();
        assertNotNull(request);
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_requests.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_requestId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(TestConstants.USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long rKey = testInserter.createRequest(pKey, uKey1);

        // Exercise
        Optional<Request> optionalRequest = requestDaoImpl.findRequest(rKey);

        // Validations & Post Conditions
        assertTrue(optionalRequest.isPresent());
        assertEquals(rKey, optionalRequest.get().getRequestId().longValue());
    }

    @Test
    public void find_requestId_invalid_requestId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long rKey = testInserter.createRequest(pKey, uKey1);

        // Exercise
        Optional<Request> purchase = requestDaoImpl.findRequest(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(purchase.isPresent());
    }

    @Test
    public void get_userId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long pKey2 = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        testInserter.createRequest(pKey1, uKey1);
        testInserter.createRequest(pKey1, uKey2);
        testInserter.createRequest(pKey2, uKey1);

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(uKey1, EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertFalse(requestList.isEmpty());
        assertEquals(TWO_ELEMENTS, requestList.size());
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

        @Test
    public void delete_requestId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long rKey = testInserter.createRequest(pKey, uKey1);

        // Exercise
        boolean deleted = requestDaoImpl.deleteRequest(rKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
    }

    @Test
    public void delete_requestId_invalid_requestId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long rKey = testInserter.createRequest(pKey, uKey1);

        // Exercise
        boolean deleted = requestDaoImpl.deleteRequest(INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
    }
}

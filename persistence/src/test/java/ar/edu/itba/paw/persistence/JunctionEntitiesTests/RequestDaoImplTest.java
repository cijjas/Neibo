package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Request;
import ar.edu.itba.paw.persistence.JunctionDaos.RequestDaoImpl;
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
public class RequestDaoImplTest {

    public static final String MAIL1 = "user1@gmail.com";
    public static final String MAIL2 = "user2@gmail.com";
    public static final String MAIL3 = "user3@gmail.com";
    public static final String REPLY = "This is a reply";
    public static final int PAGE = 1;
    public static final int SIZE = 10;
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RequestDaoImpl requestDao;
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
        long uKey3 = testInserter.createUser(MAIL3, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);

        // Exercise
        Request request = requestDao.createRequest(uKey3, pKey, "hola", 1); //TODO: arreglar quantity?

        // Validations & Post Conditions
        em.flush();
        assertNotNull(request);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_requests.name()));
    }

    @Test
    public void find_requestId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long rKey = testInserter.createRequest(pKey, uKey1);

        // Exercise
        Optional<Request> purchase = requestDao.findRequest(rKey);

        // Validations & Post Conditions
        assertTrue(purchase.isPresent());
    }

    @Test
    public void find_requestId_invalid_requestId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long rKey = testInserter.createRequest(pKey, uKey1);

        // Exercise
        Optional<Request> purchase = requestDao.findRequest(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(purchase.isPresent());
    }

    @Test
    public void get_userId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long uKey2 = testInserter.createUser(MAIL2, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long pKey2 = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        testInserter.createRequest(pKey1, uKey1);
        testInserter.createRequest(pKey1, uKey2);
        testInserter.createRequest(pKey2, uKey1);

        // Exercise
        List<Request> requests = requestDao.getRequests(uKey1, null, null, null, PAGE, SIZE);

        // Validations & Post Conditions
        assertFalse(requests.isEmpty());
        assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_requests.name()));
    }

        @Test
    public void delete_requestId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(MAIL1, nhKey);
        long dKey1 = testInserter.createDepartment(Department.ELECTRONICS);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long rKey = testInserter.createRequest(pKey, uKey1);

        // Exercise
        boolean deleted = requestDao.deleteRequest(rKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
    }

    @Test
    public void delete_requestId_invalid_requestId() {
        // Pre Conditions

        // Exercise
        boolean deleted = requestDao.deleteRequest(INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.posts_users_likes.name()));
    }
}

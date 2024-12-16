package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.RequestStatus;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.enums.TransactionType;
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
    private static long nhKey;
    private static long uKey1;
    private static long uKey2;
    private static long pKey1;
    private static long pKey2;
    private static long pKey3;
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
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);

        // Exercise
        Request request = requestDaoImpl.createRequest(uKey3, pKey, REQUEST_MESSAGE, REQUEST_QUANTITY);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(request);
        assertEquals(uKey3, request.getUser().getUserId().longValue());
        assertEquals(pKey, request.getProduct().getProductId().longValue());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_requests.name()));
        assertEquals(REQUEST_MESSAGE, request.getMessage());
        assertEquals(REQUEST_QUANTITY, request.getUnits().longValue());

    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_requestId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(TestConstants.USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
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
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long rKey = testInserter.createRequest(pKey, uKey1);

        // Exercise
        Optional<Request> purchase = requestDaoImpl.findRequest(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(purchase.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(NINETEEN_ELEMENTS, requestList.size());
    }

    @Test
    public void get_productId() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(EMPTY_FIELD, pKey1, EMPTY_FIELD, EMPTY_FIELD, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(SIX_ELEMENTS, requestList.size());
    }

    @Test
    public void get_requestedStatus() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, (long) RequestStatus.REQUESTED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(FOUR_ELEMENTS, requestList.size());
    }

    @Test
    public void get_declinedStatus() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, (long) RequestStatus.DECLINED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(NINE_ELEMENTS, requestList.size());
    }

    @Test
    public void get_acceptedStatus() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, (long) RequestStatus.ACCEPTED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(SIX_ELEMENTS, requestList.size());
    }

    @Test
    public void get_userId_purchaseType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(uKey1, EMPTY_FIELD, (long) TransactionType.PURCHASE.getId(), EMPTY_FIELD, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(THIRTEEN_ELEMENTS, requestList.size());
    }

    @Test
    public void get_userId_saleType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(uKey1, EMPTY_FIELD, (long) TransactionType.SALE.getId(), EMPTY_FIELD, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWELVE_ELEMENTS, requestList.size());
    }

    @Test
    public void get_productId_requestedStatus() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(EMPTY_FIELD, pKey2, EMPTY_FIELD, (long) RequestStatus.REQUESTED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, requestList.size());
    }

    @Test
    public void get_productId_declinedStatus() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(EMPTY_FIELD, pKey2, EMPTY_FIELD, (long) RequestStatus.DECLINED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(FOUR_ELEMENTS, requestList.size());
    }

    @Test
    public void get_productId_acceptedStatus() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(EMPTY_FIELD, pKey1, EMPTY_FIELD, (long) RequestStatus.ACCEPTED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, requestList.size());
    }

    @Test
    public void get_productId_userId_purchaseType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(uKey2, pKey1, (long) TransactionType.PURCHASE.getId(), EMPTY_FIELD, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(SIX_ELEMENTS, requestList.size());
    }

    @Test
    public void get_productId_userId_saleType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(uKey2, pKey2, (long) TransactionType.SALE.getId(), EMPTY_FIELD, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(SEVEN_ELEMENTS, requestList.size());
    }

    @Test
    public void get_requestedStatus_purchaseType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(uKey2, EMPTY_FIELD, (long) TransactionType.PURCHASE.getId(), (long) RequestStatus.REQUESTED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, requestList.size());
    }

    @Test
    public void get_requestedStatus_saleType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(uKey2, EMPTY_FIELD, (long) TransactionType.SALE.getId(), (long) RequestStatus.REQUESTED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, requestList.size());
    }

    @Test
    public void get_declinedStatus_purchaseType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(uKey2, EMPTY_FIELD, (long) TransactionType.PURCHASE.getId(), (long) RequestStatus.DECLINED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, requestList.size());
    }

    @Test
    public void get_declinedStatus_saleType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(uKey2, EMPTY_FIELD, (long) TransactionType.SALE.getId(), (long) RequestStatus.DECLINED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(FOUR_ELEMENTS, requestList.size());
    }

    @Test
    public void get_acceptedStatus_purchaseType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(uKey2, EMPTY_FIELD, (long) TransactionType.PURCHASE.getId(), (long) RequestStatus.ACCEPTED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, requestList.size());
    }

    @Test
    public void get_acceptedStatus_saleType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(uKey2, EMPTY_FIELD, (long) TransactionType.SALE.getId(), (long) RequestStatus.ACCEPTED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, requestList.size());
    }

    @Test
    public void get_productId_requestedStatus_purchaseType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(uKey2, pKey1, (long) TransactionType.PURCHASE.getId(), (long) RequestStatus.REQUESTED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, requestList.size());
    }

    @Test
    public void get_productId_requestedStatus_saleType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(uKey2, pKey2, (long) TransactionType.SALE.getId(), (long) RequestStatus.REQUESTED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, requestList.size());
    }

    @Test
    public void get_productId_declinedStatus_purchaseType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(uKey2, pKey1, (long) TransactionType.PURCHASE.getId(), (long) RequestStatus.DECLINED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, requestList.size());
    }

    @Test
    public void get_productId_declinedStatus_saleType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(uKey2, pKey2, (long) TransactionType.SALE.getId(), (long) RequestStatus.DECLINED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(FOUR_ELEMENTS, requestList.size());
    }

    @Test
    public void get_productId_acceptedStatus_purchaseType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(uKey2, pKey1, (long) TransactionType.PURCHASE.getId(), (long) RequestStatus.ACCEPTED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, requestList.size());
    }

    @Test
    public void get_productId_acceptedStatus_saleType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(uKey2, pKey2, (long) TransactionType.SALE.getId(), (long) RequestStatus.ACCEPTED.getId(), nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, requestList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(requestList.isEmpty());
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
        pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        pKey2 = testInserter.createProduct(iKey, iKey, iKey, uKey2, dKey1);
        pKey3 = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        testInserter.createRequest(pKey1, uKey2, RequestStatus.ACCEPTED);
        testInserter.createRequest(pKey1, uKey2, RequestStatus.ACCEPTED);
        testInserter.createRequest(pKey1, uKey2, RequestStatus.ACCEPTED);

        // Exercise
        List<Request> requestList = requestDaoImpl.getRequests(EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, nhKey, TEST_PAGE, TEST_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, requestList.size());
    }

    // ------------------------------------------------- COUNTS ---------------------------------------------------------

    @Test
    public void count() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, nhKey);

        // Validations & Post Conditions
        assertEquals(NINETEEN_ELEMENTS, countRequest);
    }

    @Test
    public void count_productId() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(EMPTY_FIELD, pKey1, EMPTY_FIELD, EMPTY_FIELD, nhKey);

        // Validations & Post Conditions
        assertEquals(SIX_ELEMENTS, countRequest);
    }

    @Test
    public void count_requestedStatus() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, (long) RequestStatus.REQUESTED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(FOUR_ELEMENTS, countRequest);
    }

    @Test
    public void count_declinedStatus() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, (long) RequestStatus.DECLINED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(NINE_ELEMENTS, countRequest);
    }

    @Test
    public void count_acceptedStatus() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, (long) RequestStatus.ACCEPTED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(SIX_ELEMENTS, countRequest);
    }

    @Test
    public void count_userId_purchaseType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(uKey1, EMPTY_FIELD, (long) TransactionType.PURCHASE.getId(), EMPTY_FIELD, nhKey);

        // Validations & Post Conditions
        assertEquals(THIRTEEN_ELEMENTS, countRequest);
    }

    @Test
    public void count_userId_saleType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(uKey1, EMPTY_FIELD, (long) TransactionType.SALE.getId(), EMPTY_FIELD, nhKey);

        // Validations & Post Conditions
        assertEquals(TWELVE_ELEMENTS, countRequest);
    }

    @Test
    public void count_productId_requestedStatus() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(EMPTY_FIELD, pKey2, EMPTY_FIELD, (long) RequestStatus.REQUESTED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countRequest);
    }

    @Test
    public void count_productId_declinedStatus() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(EMPTY_FIELD, pKey2, EMPTY_FIELD, (long) RequestStatus.DECLINED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(FOUR_ELEMENTS, countRequest);
    }

    @Test
    public void count_productId_acceptedStatus() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(EMPTY_FIELD, pKey1, EMPTY_FIELD, (long) RequestStatus.ACCEPTED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, countRequest);
    }

    @Test
    public void count_productId_userId_purchaseType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(uKey2, pKey1, (long) TransactionType.PURCHASE.getId(), EMPTY_FIELD, nhKey);

        // Validations & Post Conditions
        assertEquals(SIX_ELEMENTS, countRequest);
    }

    @Test
    public void count_productId_userId_saleType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(uKey2, pKey2, (long) TransactionType.SALE.getId(), EMPTY_FIELD, nhKey);

        // Validations & Post Conditions
        assertEquals(SEVEN_ELEMENTS, countRequest);
    }

    @Test
    public void count_requestedStatus_purchaseType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(uKey2, EMPTY_FIELD, (long) TransactionType.PURCHASE.getId(), (long) RequestStatus.REQUESTED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countRequest);
    }

    @Test
    public void count_requestedStatus_saleType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(uKey2, EMPTY_FIELD, (long) TransactionType.SALE.getId(), (long) RequestStatus.REQUESTED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countRequest);
    }

    @Test
    public void count_declinedStatus_purchaseType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(uKey2, EMPTY_FIELD, (long) TransactionType.PURCHASE.getId(), (long) RequestStatus.DECLINED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countRequest);
    }

    @Test
    public void count_declinedStatus_saleType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(uKey2, EMPTY_FIELD, (long) TransactionType.SALE.getId(), (long) RequestStatus.DECLINED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(FOUR_ELEMENTS, countRequest);
    }

    @Test
    public void count_acceptedStatus_purchaseType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(uKey2, EMPTY_FIELD, (long) TransactionType.PURCHASE.getId(), (long) RequestStatus.ACCEPTED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, countRequest);
    }

    @Test
    public void count_acceptedStatus_saleType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(uKey2, EMPTY_FIELD, (long) TransactionType.SALE.getId(), (long) RequestStatus.ACCEPTED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countRequest);
    }

    @Test
    public void count_productId_requestedStatus_purchaseType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(uKey2, pKey1, (long) TransactionType.PURCHASE.getId(), (long) RequestStatus.REQUESTED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countRequest);
    }

    @Test
    public void count_productId_requestedStatus_saleType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(uKey2, pKey2, (long) TransactionType.SALE.getId(), (long) RequestStatus.REQUESTED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countRequest);
    }

    @Test
    public void count_productId_declinedStatus_purchaseType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(uKey2, pKey1, (long) TransactionType.PURCHASE.getId(), (long) RequestStatus.DECLINED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countRequest);
    }

    @Test
    public void count_productId_declinedStatus_saleType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(uKey2, pKey2, (long) TransactionType.SALE.getId(), (long) RequestStatus.DECLINED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(FOUR_ELEMENTS, countRequest);
    }

    @Test
    public void count_productId_acceptedStatus_purchaseType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(uKey2, pKey1, (long) TransactionType.PURCHASE.getId(), (long) RequestStatus.ACCEPTED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, countRequest);
    }

    @Test
    public void count_productId_acceptedStatus_saleType() {
        // Pre Conditions
        populateRequests();

        // Exercise
        int countRequest = requestDaoImpl.countRequests(uKey2, pKey2, (long) TransactionType.SALE.getId(), (long) RequestStatus.ACCEPTED.getId(), nhKey);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countRequest);
    }

    @Test
    public void count_empty() {
        // Pre Conditions

        // Exercise
        int countRequest = requestDaoImpl.countRequests(EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, nhKey);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countRequest);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_neighborhoodId_requestId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long rKey = testInserter.createRequest(pKey, uKey1);

        // Exercise
        boolean deleted = requestDaoImpl.deleteRequest(nhKey, rKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.products_users_requests.name()));
    }

    @Test
    public void delete_neighborhoodId_requestId_invalid_neighborhoodId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long rKey = testInserter.createRequest(pKey, uKey1);

        // Exercise
        boolean deleted = requestDaoImpl.deleteRequest(INVALID_ID, rKey);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_neighborhoodId_requestId_invalid_requestId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long rKey = testInserter.createRequest(pKey, uKey1);

        // Exercise
        boolean deleted = requestDaoImpl.deleteRequest(nhKey, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_neighborhoodId_requestId_invalid_neighborhoodId_requestId() {
        // Pre Conditions
        long iKey = testInserter.createImage();
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long pKey = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        long rKey = testInserter.createRequest(pKey, uKey1);

        // Exercise
        boolean deleted = requestDaoImpl.deleteRequest(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    // ----------------------------------------------- POPULATION ------------------------------------------------------

    private void populateRequests() {
        // userId, productId, typeId(PURCHASE, SALE), statusId(REQUESTED, DECLINED, ACCEPTED)
        long iKey = testInserter.createImage();
        nhKey = testInserter.createNeighborhood();
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        pKey1 = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);
        pKey2 = testInserter.createProduct(iKey, iKey, iKey, uKey2, dKey1);
        pKey3 = testInserter.createProduct(iKey, iKey, iKey, uKey1, dKey1);

        testInserter.createRequest(pKey1, uKey2, RequestStatus.ACCEPTED);
        testInserter.createRequest(pKey1, uKey2, RequestStatus.ACCEPTED);
        testInserter.createRequest(pKey1, uKey2, RequestStatus.ACCEPTED);
        testInserter.createRequest(pKey1, uKey2, RequestStatus.DECLINED);
        testInserter.createRequest(pKey1, uKey2, RequestStatus.DECLINED);
        testInserter.createRequest(pKey1, uKey2, RequestStatus.REQUESTED);

        testInserter.createRequest(pKey2, uKey1, RequestStatus.DECLINED);
        testInserter.createRequest(pKey2, uKey1, RequestStatus.DECLINED);
        testInserter.createRequest(pKey2, uKey1, RequestStatus.DECLINED);
        testInserter.createRequest(pKey2, uKey1, RequestStatus.REQUESTED);
        testInserter.createRequest(pKey2, uKey1, RequestStatus.REQUESTED);
        testInserter.createRequest(pKey2, uKey1, RequestStatus.ACCEPTED);
        testInserter.createRequest(pKey2, uKey1, RequestStatus.DECLINED);

        testInserter.createRequest(pKey3, uKey1, RequestStatus.DECLINED);
        testInserter.createRequest(pKey3, uKey1, RequestStatus.DECLINED);
        testInserter.createRequest(pKey3, uKey1, RequestStatus.DECLINED);
        testInserter.createRequest(pKey3, uKey1, RequestStatus.ACCEPTED);
        testInserter.createRequest(pKey3, uKey1, RequestStatus.ACCEPTED);
        testInserter.createRequest(pKey3, uKey1, RequestStatus.REQUESTED);
    }
}

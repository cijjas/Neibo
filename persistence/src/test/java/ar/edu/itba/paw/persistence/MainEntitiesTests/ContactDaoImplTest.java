package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Contact;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.ContactDaoImpl;
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
public class ContactDaoImplTest {


    private static final String CONTACT_NAME = "Sample Contact";
    private static final String ADDRESS = "Sample Address";
    private static final String NUMBER = "123456789";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ContactDaoImpl contactDao;
    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void create_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        Contact c = contactDao.createContact(nhKey, CONTACT_NAME, ADDRESS, NUMBER);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.contacts.name()));
        assertEquals(CONTACT_NAME, c.getContactName());
        assertEquals(ADDRESS, c.getContactAddress());
        assertEquals(NUMBER, c.getContactPhone());
    }

	@Test
	public void find_contactId_valid() {
	    // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long cKey = testInserter.createContact(nhKey);

	    // Exercise
	    Optional<Contact> optional = contactDao.findContact(cKey);

	    // Validations & Post Conditions
	    assertTrue(optional.isPresent());
		assertEquals(cKey, optional.get().getContactId().longValue());
	}

    @Test
	public void find_contactId_invalid_contactId() {
	    // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

	    // Exercise
	    Optional<Contact> optional = contactDao.findContact(INVALID_ID);

	    // Validations & Post Conditions
	    assertFalse(optional.isPresent());
	}

	@Test
	public void find_contactId_neighborhoodId_valid() {
	    // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long cKey = testInserter.createContact(nhKey);

	    // Exercise
	    Optional<Contact> optional = contactDao.findContact(cKey, nhKey);

	    // Validations & Post Conditions
	    assertTrue(optional.isPresent());
		assertEquals(cKey, optional.get().getContactId().longValue());
	}

    @Test
	public void find_contactId_neighborhoodId_invalid_contactId() {
	    // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long cKey = testInserter.createContact(nhKey);

	    // Exercise
	    Optional<Contact> optional = contactDao.findContact(INVALID_ID, nhKey);

	    // Validations & Post Conditions
	    assertFalse(optional.isPresent());
	}

    @Test
	public void find_contactId_neighborhoodId_invalid_neighborhoodId() {
	    // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long cKey = testInserter.createContact(nhKey);

	    // Exercise
	    Optional<Contact> optional = contactDao.findContact(cKey, INVALID_ID);

	    // Validations & Post Conditions
	    assertFalse(optional.isPresent());
	}

    @Test
	public void find_contactId_neighborhoodId_invalid_contactId_neighborhoodId() {
	    // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long cKey = testInserter.createContact(nhKey);

	    // Exercise
	    Optional<Contact> optional = contactDao.findContact(INVALID_ID, INVALID_ID);

	    // Validations & Post Conditions
	    assertFalse(optional.isPresent());
	}

    @Test
    public void get_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        testInserter.createContact(nhKey, CONTACT_NAME, ADDRESS, NUMBER);

        // Exercise
        List<Contact> contacts = contactDao.getContacts(nhKey);

        // Validations & Post Conditions
        assertEquals(1, contacts.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions

        // Exercise
        List<Contact> contacts = contactDao.getContacts(1);

        // Validations & Post Conditions
        assertEquals(0, contacts.size());
    }

    @Test
    public void delete_contactId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long contactId = testInserter.createContact(nhKey);

        // Exercise
        boolean deleted = contactDao.deleteContact(contactId);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.contacts.name()));
    }

    @Test
    public void delete_contactId_invalid_contactId() {
        // Pre Conditions

        // Exercise
        boolean deleted = contactDao.deleteContact(1);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.contacts.name()));
    }
}

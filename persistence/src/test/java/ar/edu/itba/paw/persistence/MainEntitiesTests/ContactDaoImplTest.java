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

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class ContactDaoImplTest {


    private static final String CONTACT_NAME = "Sample Contact";
    private static final String CONTACT_ADDRESS = "Sample Address";
    private static final String CONTACT_NUMBER = "123456789";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ContactDaoImpl contactDaoImpl;
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
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        Contact contact = contactDaoImpl.createContact(nhKey, CONTACT_NAME, CONTACT_ADDRESS, CONTACT_NUMBER);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(contact);
        assertEquals(nhKey, contact.getNeighborhood().getNeighborhoodId().longValue());
        assertEquals(CONTACT_NAME, contact.getContactName());
        assertEquals(CONTACT_ADDRESS, contact.getContactAddress());
        assertEquals(CONTACT_NUMBER, contact.getContactPhone());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.contacts.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

	@Test
	public void find_contactId_valid() {
	    // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long cKey = testInserter.createContact(nhKey);

	    // Exercise
	    Optional<Contact> optionalContact = contactDaoImpl.findContact(cKey);

	    // Validations & Post Conditions
	    assertTrue(optionalContact.isPresent());
		assertEquals(cKey, optionalContact.get().getContactId().longValue());
	}

    @Test
	public void find_contactId_invalid_contactId() {
	    // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

	    // Exercise
	    Optional<Contact> optionalContact = contactDaoImpl.findContact(INVALID_ID);

	    // Validations & Post Conditions
	    assertFalse(optionalContact.isPresent());
	}

	@Test
	public void find_contactId_neighborhoodId_valid() {
	    // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long cKey = testInserter.createContact(nhKey);

	    // Exercise
	    Optional<Contact> optionalContact = contactDaoImpl.findContact(cKey, nhKey);

	    // Validations & Post Conditions
	    assertTrue(optionalContact.isPresent());
		assertEquals(cKey, optionalContact.get().getContactId().longValue());
	}

    @Test
	public void find_contactId_neighborhoodId_invalid_contactId() {
	    // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long cKey = testInserter.createContact(nhKey);

	    // Exercise
	    Optional<Contact> optionalContact = contactDaoImpl.findContact(INVALID_ID, nhKey);

	    // Validations & Post Conditions
	    assertFalse(optionalContact.isPresent());
	}

    @Test
	public void find_contactId_neighborhoodId_invalid_neighborhoodId() {
	    // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long cKey = testInserter.createContact(nhKey);

	    // Exercise
	    Optional<Contact> optionalContact = contactDaoImpl.findContact(cKey, INVALID_ID);

	    // Validations & Post Conditions
	    assertFalse(optionalContact.isPresent());
	}

    @Test
	public void find_contactId_neighborhoodId_invalid_contactId_neighborhoodId() {
	    // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long cKey = testInserter.createContact(nhKey);

	    // Exercise
	    Optional<Contact> optionalContact = contactDaoImpl.findContact(INVALID_ID, INVALID_ID);

	    // Validations & Post Conditions
	    assertFalse(optionalContact.isPresent());
	}

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        testInserter.createContact(nhKey, CONTACT_NAME, CONTACT_ADDRESS, CONTACT_NUMBER);

        // Exercise
        List<Contact> contactList = contactDaoImpl.getContacts(nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, contactList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        List<Contact> contactList = contactDaoImpl.getContacts(nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(contactList.isEmpty());
    }

    // ------------------------------------------------- COUNTS --------------------------------------------------------

    @Test
    public void count() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        testInserter.createContact(nhKey, CONTACT_NAME, CONTACT_ADDRESS, CONTACT_NUMBER);

        // Exercise
        int countContacts = contactDaoImpl.countContacts(nhKey);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countContacts);
    }

    @Test
    public void count_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        int countContacts = contactDaoImpl.countContacts(nhKey);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countContacts);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_contactId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long contactId = testInserter.createContact(nhKey);

        // Exercise
        boolean deleted = contactDaoImpl.deleteContact(contactId);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.contacts.name()));
    }

    @Test
    public void delete_contactId_invalid_contactId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long contactId = testInserter.createContact(nhKey);

        // Exercise
        boolean deleted = contactDaoImpl.deleteContact(INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}

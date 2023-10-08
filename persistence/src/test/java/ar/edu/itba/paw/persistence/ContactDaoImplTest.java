package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ContactDao;
import ar.edu.itba.paw.models.Contact;
import ar.edu.itba.paw.persistence.ContactDaoImpl;
import ar.edu.itba.paw.persistence.Table;
import ar.edu.itba.paw.persistence.TestInsertionUtils;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class ContactDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private ContactDaoImpl contactDao;

    private static final String CONTACT_NAME = "Sample Contact";
    private static final  String ADDRESS = "Sample Address";
    private static final  String NUMBER = "123456789";


    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        contactDao = new ContactDaoImpl(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
    }

    @Test
    public void testCreateContact() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();

        // Exercise
        Contact c = contactDao.createContact(nhKey.longValue(), CONTACT_NAME, ADDRESS, NUMBER);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.contacts.name()));
        assertEquals(CONTACT_NAME, c.getContactName());
        assertEquals(ADDRESS, c.getContactAddress());
        assertEquals(NUMBER, c.getContactPhone());

    }

    @Test
    public void testGetContacts() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        testInsertionUtils.createContact(nhKey.longValue(), CONTACT_NAME, ADDRESS, NUMBER);

        // Exercise
        List<Contact> contacts = contactDao.getContacts(nhKey.longValue());

        // Validations & Post Conditions
        assertEquals(1, contacts.size());
    }

    @Test
    public void testGetNoContacts() {
        // Pre Conditions

        // Exercise
        List<Contact> contacts = contactDao.getContacts(1);

        // Validations & Post Conditions
        assertEquals(0, contacts.size());
    }

    @Test
    public void testDeleteContact() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number contactId = testInsertionUtils.createContact(nhKey.longValue());

        // Exercise
        boolean deleted = contactDao.deleteContact(contactId.longValue());

        // Validations & Post Conditions
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.contacts.name()));
    }

    @Test
    public void testDeleteInvalidContact() {
        // Pre Conditions

        // Exercise
        boolean deleted = contactDao.deleteContact(1);

        // Validations & Post Conditions
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.contacts.name()));
    }

    @Test
    public void testDeleteNonExistentContact() {
        // Pre Conditions

        // Exercise
        boolean deleted = contactDao.deleteContact(1);

        // Validations & Post Conditions
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.contacts.name()));
    }
}

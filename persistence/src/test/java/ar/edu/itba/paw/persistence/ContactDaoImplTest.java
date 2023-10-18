package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Contact;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Sql("classpath:hsqlValueCleanUp.sql")
public class ContactDaoImplTest {


    private static final String CONTACT_NAME = "Sample Contact";
    private static final String ADDRESS = "Sample Address";
    private static final String NUMBER = "123456789";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    private ContactDaoImpl contactDao;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        contactDao = new ContactDaoImpl(ds);
    }

    @Test
    public void testCreateContact() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        Contact c = contactDao.createContact(nhKey, CONTACT_NAME, ADDRESS, NUMBER);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.contacts.name()));
        assertEquals(CONTACT_NAME, c.getContactName());
        assertEquals(ADDRESS, c.getContactAddress());
        assertEquals(NUMBER, c.getContactPhone());
    }

    @Test
    public void testGetContacts() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        testInserter.createContact(nhKey, CONTACT_NAME, ADDRESS, NUMBER);

        // Exercise
        List<Contact> contacts = contactDao.getContacts(nhKey);

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
        long nhKey = testInserter.createNeighborhood();
        long contactId = testInserter.createContact(nhKey);

        // Exercise
        boolean deleted = contactDao.deleteContact(contactId);

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

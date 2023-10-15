package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.ContactDao;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ContactDaoImpl implements ContactDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final String CONTACTS =
            "select ct.* " +
            "from contacts ct";

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactDaoImpl.class);

    @Autowired
    public ContactDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("contactid")
                .withTableName("contacts");
    }

    // --------------------------------------------- CONTACT INSERT ----------------------------------------------------

    @Override
    public Contact createContact(long neighborhoodId, String contactName, String contactAddress, String contactPhone) {
        LOGGER.debug("Inserting Contact {}", contactName);
        Map<String, Object> data = new HashMap<>();
        data.put("neighborhoodid", neighborhoodId);
        data.put("contactname", contactName);
        data.put("contactaddress", contactAddress);
        data.put("contactphone", contactPhone);

        try {
            final Number key = jdbcInsert.executeAndReturnKey(data);
            return new Contact.Builder()
                    .contactId(key.longValue())
                    .contactAddress(contactAddress)
                    .contactName(contactName)
                    .contactPhone(contactPhone)
                    .neighborhoodId(neighborhoodId)
                    .build();
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Contact", ex);
            throw new InsertionException("An error occurred whilst creating the contact");
        }

    }

    // --------------------------------------------- CONTACT SELECT ----------------------------------------------------

    private static final RowMapper<Contact> ROW_MAPPER = (rs, rowNum) ->
            new Contact.Builder()
                    .contactId(rs.getLong("contactid"))
                    .contactAddress(rs.getString("contactaddress"))
                    .contactName(rs.getString("contactname"))
                    .contactPhone(rs.getString("contactphone"))
                    .neighborhoodId(rs.getLong("neighborhoodid"))
                    .build();
    @Override
    public List<Contact> getContacts(final long neighborhoodId) {
        LOGGER.debug("Selecting Contacts from Neighborhood {}", neighborhoodId);
        return jdbcTemplate.query(CONTACTS + " WHERE ct.neighborhoodid = ?", ROW_MAPPER, neighborhoodId);
    }

    // --------------------------------------------- CONTACT DELETE ----------------------------------------------------

    @Override
    public boolean deleteContact(long contactId) {
        LOGGER.debug("Deleting Contact with id {}", contactId);
        return jdbcTemplate.update("DELETE FROM contacts WHERE contactid = ?", contactId) > 0;
    }
}

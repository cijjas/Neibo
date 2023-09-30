package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.interfaces.persistence.ContactDao;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

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
        Map<String, Object> data = new HashMap<>();
        data.put("neighborhoodid", neighborhoodId);
        data.put("contactname", contactName);
        data.put("contactaddress", contactAddress);
        data.put("contactphone", contactPhone);

        final Number key = jdbcInsert.executeAndReturnKey(data);
        return new Contact.Builder()
                .contactAddress(contactAddress)
                .contactName(contactName)
                .contactPhone(contactPhone)
                .neighborhoodId(neighborhoodId)
                .build();
    }

    // --------------------------------------------- CONTACT SELECT ----------------------------------------------------

    private static final RowMapper<Contact> ROW_MAPPER = (rs, rowNum) ->
            new Contact.Builder()
                    .contactAddress(rs.getString("contactaddress"))
                    .contactName(rs.getString("contactname"))
                    .contactPhone(rs.getString("contactphone"))
                    .neighborhoodId(rs.getLong("neighborhoodid"))
                    .build();
    @Override
    public List<Contact> getContacts(final long neighborhoodId) {
        return jdbcTemplate.query(CONTACTS + " WHERE ct.neighborhoodid = ?", ROW_MAPPER, neighborhoodId);
    }
}

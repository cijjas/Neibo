package ar.edu.itba.paw.persistence.MainEntitiesDaos;


import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.ContactDao;
import ar.edu.itba.paw.models.MainEntities.Contact;
import ar.edu.itba.paw.models.MainEntities.Neighborhood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ContactDaoImpl implements ContactDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactDaoImpl.class);
    @PersistenceContext
    private EntityManager em;
    private static final RowMapper<Contact> ROW_MAPPER = (rs, rowNum) ->
            new Contact.Builder()
                    .contactId(rs.getLong("contactid"))
                    .contactAddress(rs.getString("contactaddress"))
                    .contactName(rs.getString("contactname"))
                    .contactPhone(rs.getString("contactphone"))
                    .build();
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String CONTACTS = "SELECT ct.* FROM contacts ct";

    // --------------------------------------------- CONTACT INSERT ----------------------------------------------------

    @Autowired
    public ContactDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("contactid")
                .withTableName("contacts");
    }

    // --------------------------------------------- CONTACT SELECT ----------------------------------------------------

    @Override
    public Contact createContact(long neighborhoodId, String contactName, String contactAddress, String contactPhone) {
        LOGGER.debug("Inserting Contact {}", contactName);
        Contact contact = new Contact.Builder()
                .contactAddress(contactAddress)
                .contactName(contactName)
                .contactPhone(contactPhone)
                .neighborhood(em.find(Neighborhood.class, neighborhoodId))
                .build();
        em.persist(contact);
        return contact;
    }

    @Override
    public List<Contact> getContacts(final long neighborhoodId) {
        LOGGER.debug("Selecting Contacts from Neighborhood {}", neighborhoodId);
        TypedQuery<Contact> query = em.createQuery("SELECT c FROM Contact c WHERE c.neighborhood.neighborhoodId = :neighborhoodId", Contact.class);
        query.setParameter("neighborhoodId", neighborhoodId);
        return query.getResultList();
    }


    // --------------------------------------------- CONTACT DELETE ----------------------------------------------------

    @Override
    public boolean deleteContact(long contactId) {
        LOGGER.debug("Deleting Contact with id {}", contactId);
        Contact contact = em.find(Contact.class, contactId);
        if (contact != null) {
            em.remove(contact);
            return true;
        }
        return false;
    }
}

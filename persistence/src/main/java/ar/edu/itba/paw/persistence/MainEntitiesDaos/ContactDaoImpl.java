package ar.edu.itba.paw.persistence.MainEntitiesDaos;


import ar.edu.itba.paw.interfaces.persistence.ContactDao;
import ar.edu.itba.paw.models.Entities.Contact;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class ContactDaoImpl implements ContactDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // --------------------------------------------- CONTACT INSERT ----------------------------------------------------

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

    // --------------------------------------------- CONTACT SELECT ----------------------------------------------------

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

    @Override
    public Optional<Contact> findContact(final long contactId) {
        LOGGER.debug("Selecting Contact with id {}", contactId);
        return Optional.ofNullable(em.find(Contact.class, contactId));
    }
}

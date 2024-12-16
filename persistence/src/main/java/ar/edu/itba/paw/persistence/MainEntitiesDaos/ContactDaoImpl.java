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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
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
    public Optional<Contact> findContact(final long contactId) {
        LOGGER.debug("Selecting Contact with id {}", contactId);

        return Optional.ofNullable(em.find(Contact.class, contactId));
    }

    @Override
    public Optional<Contact> findContact(long contactId, long neighborhoodId) {
        LOGGER.debug("Selecting Contact with contactId {}, neighborhoodId {}", contactId, neighborhoodId);

        TypedQuery<Contact> query = em.createQuery(
                "SELECT c FROM Contact c WHERE c.id = :contactId AND c.neighborhood.id = :neighborhoodId",
                Contact.class
        );

        query.setParameter("contactId", contactId);
        query.setParameter("neighborhoodId", neighborhoodId);

        List<Contact> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Contact> getContacts(long neighborhoodId, int page, int size) {
        LOGGER.debug("Selecting paginated Contacts from Neighborhood {}", neighborhoodId);

        // Initialize Query Builder
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // First Query: Retrieve paginated Contact IDs
        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<Contact> idRoot = idQuery.from(Contact.class);
        idQuery.select(idRoot.get("contactId"));
        idQuery.where(cb.equal(idRoot.get("neighborhood").get("neighborhoodId"), neighborhoodId));
        idQuery.orderBy(cb.asc(idRoot.get("contactId")));
        TypedQuery<Long> idTypedQuery = em.createQuery(idQuery);
        idTypedQuery.setFirstResult((page - 1) * size);
        idTypedQuery.setMaxResults(size);
        List<Long> contactIds = idTypedQuery.getResultList();

        if (contactIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Second Query: Retrieve Contacts by IDs
        CriteriaQuery<Contact> dataQuery = cb.createQuery(Contact.class);
        Root<Contact> dataRoot = dataQuery.from(Contact.class);
        dataQuery.where(dataRoot.get("contactId").in(contactIds));
        dataQuery.orderBy(cb.asc(dataRoot.get("contactId")));
        TypedQuery<Contact> dataTypedQuery = em.createQuery(dataQuery);

        return dataTypedQuery.getResultList();
    }

    @Override
    public int countContacts(long neighborhoodId) {
        LOGGER.debug("Counting Contacts from Neighborhood {}", neighborhoodId);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<Contact> root = criteriaQuery.from(Contact.class);
        criteriaQuery.select(cb.count(root));
        criteriaQuery.where(cb.equal(root.get("neighborhood").get("neighborhoodId"), neighborhoodId));
        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        return query.getSingleResult().intValue();
    }

    // --------------------------------------------- CONTACT DELETE ----------------------------------------------------

    @Override
    public boolean deleteContact(long neighborhoodId, long contactId) {
        LOGGER.debug("Deleting Contact with contactId {} and neighborhoodId {}", contactId, neighborhoodId);

        String hql = "DELETE FROM Contact c WHERE c.contactId = :contactId " +
                "AND c.neighborhood.id = :neighborhoodId";

        int deletedCount = em.createQuery(hql)
                .setParameter("contactId", contactId)
                .setParameter("neighborhoodId", neighborhoodId)
                .executeUpdate();

        return deletedCount > 0;
    }
}

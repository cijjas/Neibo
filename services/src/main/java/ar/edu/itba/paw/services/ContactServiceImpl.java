package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.ContactDao;
import ar.edu.itba.paw.interfaces.services.ContactService;
import ar.edu.itba.paw.models.Entities.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContactServiceImpl implements ContactService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactServiceImpl.class);

    private final ContactDao contactDao;

    @Autowired
    public ContactServiceImpl(final ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Contact createContact(long neighborhoodId, String contactName, String contactAddress, String contactPhone) {
        LOGGER.info("Creating Contact {} for Neighborhood {}", contactName, neighborhoodId);

        return contactDao.createContact(neighborhoodId, contactName, contactAddress, contactPhone);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Contact> findContact(long contactId) {
        LOGGER.info("Finding Contact {}", contactId);

        return contactDao.findContact(contactId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Contact> findContact(long contactId, long neighborhoodId) {
        LOGGER.info("Finding Contact {} from Neighborhood {}", contactId, neighborhoodId);

        return contactDao.findContact(contactId, neighborhoodId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contact> getContacts(long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Contacts for Neighborhood {}", neighborhoodId);

        return contactDao.getContacts(neighborhoodId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateContactPages(long neighborhoodId, int size) {
        LOGGER.info("Calculating Contact Pages for Neighborhood {}", neighborhoodId);

        return PaginationUtils.calculatePages(contactDao.countContacts(neighborhoodId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Contact updateContact(long contactId, String contactName, String contactAddress, String contactPhone) {
        LOGGER.info("Updating Contact {}", contactId);

        Contact contact = findContact(contactId).orElseThrow(NotFoundException::new);

        if (contactName != null && !contactName.isEmpty())
            contact.setContactName(contactName);
        if (contactAddress != null && !contactAddress.isEmpty())
            contact.setContactAddress(contactAddress);
        if (contactPhone != null && !contactPhone.isEmpty())
            contact.setContactPhone(contactPhone);

        return contact;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteContact(long contactId) {
        LOGGER.info("Deleting Contact {}", contactId);

        return contactDao.deleteContact(contactId);
    }
}

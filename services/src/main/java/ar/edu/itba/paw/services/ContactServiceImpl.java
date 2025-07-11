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
    public ContactServiceImpl(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Contact createContact(long neighborhoodId, String contactName, String contactAddress, String contactPhone) {
        LOGGER.info("Creating Contact {} with address {} and phone {} for Neighborhood {}", contactName, contactAddress, contactPhone, neighborhoodId);

        return contactDao.createContact(neighborhoodId, contactName, contactAddress, contactPhone);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Contact> findContact(long neighborhoodId, long contactId) {
        LOGGER.info("Finding Contact {} from Neighborhood {}", contactId, neighborhoodId);

        return contactDao.findContact(neighborhoodId, contactId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contact> getContacts(long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Contacts for Neighborhood {}", neighborhoodId);

        return contactDao.getContacts(neighborhoodId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int countContacts(long neighborhoodId) {
        LOGGER.info("Counting Contacts for Neighborhood {}", neighborhoodId);

        return contactDao.countContacts(neighborhoodId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Contact updateContact(long neighborhoodId, long contactId, String contactName, String contactAddress, String contactPhone) {
        LOGGER.info("Updating Contact {} from Neighborhood {}", contactId, neighborhoodId);

        Contact contact = findContact(neighborhoodId, contactId).orElseThrow(NotFoundException::new);

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
    public boolean deleteContact(long neighborhoodId, long contactId) {
        LOGGER.info("Deleting Contact {} from Neighborhood {}", contactId, neighborhoodId);

        return contactDao.deleteContact(neighborhoodId, contactId);
    }
}

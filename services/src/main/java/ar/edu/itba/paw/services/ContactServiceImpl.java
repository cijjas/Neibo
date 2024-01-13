package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.ContactDao;
import ar.edu.itba.paw.interfaces.services.ContactService;
import ar.edu.itba.paw.models.Entities.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<Contact> getContacts(final long neighborhoodId) {
        LOGGER.info("Getting Contacts for Neighborhood {}", neighborhoodId);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return contactDao.getContacts(neighborhoodId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Contact updateContact(long contactId, String contactName, String contactAddress, String contactPhone) {
        LOGGER.info("Updating Contact {}", contactId);

        Contact contact = getContact(contactId);

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

        ValidationUtils.checkContactId(contactId);

        return contactDao.deleteContact(contactId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    private Contact getContact(long contactId) {
        LOGGER.info("Getting Contact {}", contactId);

        ValidationUtils.checkContactId(contactId);

        return contactDao.findContactById(contactId).orElseThrow(() -> new NotFoundException("Contact not found"));
    }
}

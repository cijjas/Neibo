package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ContactDao;
import ar.edu.itba.paw.interfaces.services.ContactService;
import ar.edu.itba.paw.models.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContactServiceImpl implements ContactService {
    private final ContactDao contactDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactServiceImpl.class);

    @Autowired
    public ContactServiceImpl(final ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Contact createContact(long neighborhoodId, String contactName, String contactAddress, String contactPhone){
        LOGGER.info("Creating Contact {} for Neighborhood {}", contactName, neighborhoodId);
        return contactDao.createContact(neighborhoodId, contactName, contactAddress, contactPhone);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public List<Contact> getContacts(final long neighborhoodId){
        LOGGER.info("Getting Contacts for Neighborhood {}", neighborhoodId);
        return contactDao.getContacts(neighborhoodId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteContact(long contactId) {
        LOGGER.info("Deleting Contact {}", contactId);
        return contactDao.deleteContact(contactId);
    }
}

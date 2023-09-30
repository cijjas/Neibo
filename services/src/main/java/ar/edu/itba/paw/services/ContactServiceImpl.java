package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ContactDao;
import ar.edu.itba.paw.interfaces.services.ContactService;
import ar.edu.itba.paw.models.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {
    private final ContactDao contactDao;

    @Autowired
    public ContactServiceImpl(final ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    @Override
    public Contact createContact(long neighborhoodId, String contactName, String contactAddress, String contactPhone){
        return contactDao.createContact(neighborhoodId, contactName, contactAddress, contactPhone);
    }

    @Override
    public List<Contact> getContacts(final long neighborhoodId){
        return contactDao.getContacts(neighborhoodId);
    }

}

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Contact;

import java.util.List;

public interface ContactDao {
    Contact createContact(long neighborhoodId, String contactName, String contactAddress, String contactPhone);

    List<Contact> getContacts(final long neighborhoodId);

    boolean deleteContact(long contactId);
}

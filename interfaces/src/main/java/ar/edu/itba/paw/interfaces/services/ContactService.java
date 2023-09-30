package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Contact;

import java.util.List;

public interface ContactService {
    Contact createContact(long neighborhoodId, String contactName, String contactAddress, String contactPhone);

    List<Contact> getContacts(final long neighborhoodId);
}

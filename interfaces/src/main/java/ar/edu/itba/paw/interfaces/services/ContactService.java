package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Contact;

import java.util.List;
import java.util.Optional;

public interface ContactService {

    Contact createContact(long neighborhoodId, String contactName, String contactAddress, String contactPhone);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Contact> findContact(long neighborhoodId, long contactId);

    List<Contact> getContacts(long neighborhoodId, int page, int size);

    int calculateContactPages(long neighborhoodId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Contact updateContact(long neighborhoodId, long contactId, String contactName, String contactAddress, String contactPhone);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteContact(long neighborhoodId, long contactId);
}

package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Contact;

import java.util.List;
import java.util.Optional;

public interface ContactService {

    Contact createContact(long neighborhoodId, String contactName, String contactAddress, String contactPhone);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Contact> findContact(long contactId);

    Optional<Contact> findContact(long contactId, long neighborhoodId);

    List<Contact> getContacts(final long neighborhoodId);

    // -----------------------------------------------------------------------------------------------------------------

    Contact updateContact(long contactId, String contactName, String contactAddress, String contactPhone);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteContact(long contactId);
}

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Contact;

import java.util.List;
import java.util.Optional;

public interface ContactDao {

    // --------------------------------------------- CONTACT INSERT ----------------------------------------------------

    Contact createContact(long neighborhoodId, String contactName, String contactAddress, String contactPhone);

    // --------------------------------------------- CONTACT SELECT ----------------------------------------------------

    Optional<Contact> findContact(final long contactId);

    Optional<Contact> findContact(final long contactId, long neighborhoodId);

    List<Contact> getContacts(final long neighborhoodId, final int page, final int size);

    int countContacts(final long neighborhoodId);

    // --------------------------------------------- CONTACT DELETE ----------------------------------------------------

    boolean deleteContact(long contactId);
}

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Contact;

import java.util.List;
import java.util.Optional;

public interface ContactDao {

    // --------------------------------------------- CONTACT INSERT ----------------------------------------------------

    Contact createContact(long neighborhoodId, String contactName, String contactAddress, String contactPhone);

    // --------------------------------------------- CONTACT SELECT ----------------------------------------------------

    Optional<Contact> findContact(long neighborhoodId, long contactId);

    List<Contact> getContacts(long neighborhoodId, int page, int size);

    int countContacts(long neighborhoodId);

    // --------------------------------------------- CONTACT DELETE ----------------------------------------------------

    boolean deleteContact(long neighborhoodId, long contactId);
}

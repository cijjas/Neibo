package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.MainEntities.Contact;

import java.util.List;

public interface ContactDao {

    // --------------------------------------------- CONTACT INSERT ----------------------------------------------------

    Contact createContact(long neighborhoodId, String contactName, String contactAddress, String contactPhone);

    // --------------------------------------------- CONTACT SELECT ----------------------------------------------------

    List<Contact> getContacts(final long neighborhoodId);

    // --------------------------------------------- CONTACT DELETE ----------------------------------------------------

    boolean deleteContact(long contactId);
}

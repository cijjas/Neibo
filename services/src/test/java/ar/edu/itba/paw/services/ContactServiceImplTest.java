package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ContactDao;
import ar.edu.itba.paw.models.Contact;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class) // Le decimos a JUnit que corra los tests con el runner de Mockito
public class ContactServiceImplTest {

    private static final long ID = 1;
    private static final String NAME = "Ismael";
    private static final String ADDRESS = "Calle Falsa 123";
    private static final long NEIGHBORHOOD_ID = 1;
    private static String PHONE = "1234567890";
    @Mock
    private ContactDao contactDao;
    @InjectMocks
    private ContactServiceImpl cs;

    @Test
    public void testCreate() {
        // 1. Preconditions
        when(contactDao.createContact(anyLong(), anyString(), anyString(), anyString())).thenReturn(new Contact.Builder()
                .contactId(ID)
                .contactName(NAME)
                .contactAddress(ADDRESS)
                .contactPhone(PHONE)
                .neighborhoodId(NEIGHBORHOOD_ID)
                .build()
        );

        // 2. Exercise
        Contact newContact = cs.createContact(NEIGHBORHOOD_ID, NAME, ADDRESS, PHONE);

        // 3. Postconditions
        Assert.assertNotNull(newContact);
        Assert.assertEquals(newContact.getNeighborhoodId(), NEIGHBORHOOD_ID);
        Assert.assertEquals(newContact.getContactName(), NAME);
        Assert.assertEquals(newContact.getContactAddress(), ADDRESS);
        Assert.assertEquals(newContact.getContactPhone(), PHONE);

    }

    @Test(expected = RuntimeException.class)
    public void testCreateAlreadyExists() {
        // 1. Preconditions
        when(contactDao.createContact(eq(NEIGHBORHOOD_ID), eq(NAME), eq(ADDRESS), eq(PHONE))).thenThrow(RuntimeException.class);

        // 2. Exercise
        Contact newContact = cs.createContact(NEIGHBORHOOD_ID, NAME, ADDRESS, PHONE);

        // 3. Postconditions
    }

}

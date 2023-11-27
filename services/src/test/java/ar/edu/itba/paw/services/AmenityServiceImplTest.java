package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.models.Entities.*;
import ar.edu.itba.paw.services.email.EmailServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class AmenityServiceImplTest {
    private static final Long ID = 1L;
    private static final String NAME = "Pileta";
    private static final String DESCRIPTION = "Pileta de natación";
    private static final Long NEIGHBORHOOD_ID = 1L;
    private Neighborhood mockNeighborhood;

    @Mock
    private EmailService es;
    @Mock
    private AmenityDao amenityDao;
    @InjectMocks
    private AmenityServiceImpl as;

    @Before
    public void setUp() {
        mockNeighborhood = mock(Neighborhood.class);
    }

    @Test
    public void testCreate() {
        // 1. Preconditions
        /*when(es.sendNewAmenityMail(anyLong(), anyString(), anyString(), anyList()));
        when(amenityDao.createAmenity(anyString(), anyString(), anyLong())).thenReturn(new Amenity.Builder()
                .amenityId(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .neighborhood(mockNeighborhood)
                .build()
        );

        // 2. Exercise
        Amenity newAmenity = as.createAmenity(NAME, DESCRIPTION, NEIGHBORHOOD_ID, new ArrayList<>());

        // 3. Postconditions
        Assert.assertNotNull(newAmenity);
        Assert.assertEquals(newAmenity.getAmenityId(), ID);
        Assert.assertEquals(newAmenity.getName(), NAME);
        Assert.assertEquals(newAmenity.getDescription(), DESCRIPTION);*/
    }

    @Test(expected = RuntimeException.class)
    public void testCreateAlreadyExists() {
        // 1. Preconditions
        when(amenityDao.createAmenity(eq(NAME), eq(DESCRIPTION), eq(NEIGHBORHOOD_ID))).thenThrow(RuntimeException.class);

        // 2. Exercise
        Amenity newAmenity = as.createAmenity(NAME, DESCRIPTION, NEIGHBORHOOD_ID, null);

        // 3. Postconditions
    }

}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.models.Amenity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AmenityServiceImplTest {
    private static final Long ID = 1L;
    private static final String NAME = "Pileta";
    private static final String DESCRIPTION = "Pileta de natación";
    private static final Long NEIGHBORHOOD_ID = 1L;

    @Mock
    private AmenityDao amenityDao;
    @InjectMocks
    private AmenityServiceImpl as;

    @Test
    public void testCreate() {
        // 1. Preconditions
        when(amenityDao.createAmenity(anyString(), anyString(), anyLong())).thenReturn(new Amenity.Builder()
                .amenityId(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .neighborhoodId(NEIGHBORHOOD_ID)
                .build()
        );

        // 2. Exercise
        Amenity newAmenity = as.createAmenity(NAME, DESCRIPTION, NEIGHBORHOOD_ID, new ArrayList<>());

        // 3. Postconditions
        Assert.assertNotNull(newAmenity);
        Assert.assertEquals(newAmenity.getNeighborhoodId(), ID);
        Assert.assertEquals(newAmenity.getName(), NAME);
        Assert.assertEquals(newAmenity.getDescription(), DESCRIPTION);
        Assert.assertEquals(newAmenity.getNeighborhoodId(), NEIGHBORHOOD_ID);

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

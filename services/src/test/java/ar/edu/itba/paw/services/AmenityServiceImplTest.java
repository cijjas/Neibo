package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

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
    private static final Long SHIFT_ID = 1L;
    private static final String SHIFT_STRING = "1-1";
    private Neighborhood mockNeighborhood;

    @Mock
    private EmailService es;
    @Mock
    private UserService us;
    @Mock
    private AmenityDao amenityDao;
    @Mock
    private ShiftDao shiftDao;
    @Mock
    private AvailabilityDao availabilityDao;
    @InjectMocks
    private AmenityServiceImpl as;

    @Before
    public void setUp() {
        mockNeighborhood = mock(Neighborhood.class);
    }

    @Test
    public void testCreate() {
        // 1. Preconditions
        when(amenityDao.createAmenity(anyString(), anyString(), anyLong())).thenReturn(new Amenity.Builder()
                .amenityId(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .neighborhood(mockNeighborhood)
                .build()
        );

        when(shiftDao.findShift(anyLong(), anyLong())).thenReturn(Optional.empty());

        when(shiftDao.createShift(anyLong(), anyLong())).thenReturn(new Shift.Builder()
                .shiftId(SHIFT_ID)
                .build()
        );

        when(availabilityDao.createAvailability(anyLong(), anyLong())).thenReturn(new Availability.Builder()
                .amenityAvailabilityId(ID)
                .build()
        );

        when(us.getNeighbors(anyLong())).thenReturn(new ArrayList<>());

        ArrayList<String> shiftArray = new ArrayList<>(Arrays.asList(SHIFT_STRING));

        // 2. Exercise
        Amenity newAmenity = as.createAmenity(NAME, DESCRIPTION, NEIGHBORHOOD_ID, shiftArray);

        // 3. Postconditions
        Assert.assertNotNull(newAmenity);
        Assert.assertEquals(newAmenity.getAmenityId(), ID);
        Assert.assertEquals(newAmenity.getName(), NAME);
        Assert.assertEquals(newAmenity.getDescription(), DESCRIPTION);
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

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Shift;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AvailabilityServiceImplTest {

    private static final long ID = 1;
    private static final List<Shift> SCHEDULE = new ArrayList<>();
    private static final long AMENITY_ID = 1;
    private static final long SHIFT_ID = 1;
    private Amenity mockAmenity;
    @Mock
    private AvailabilityDao availabilityDao;
    @Mock
    private ShiftDao shiftDao;
/*    @InjectMocks
    private AvailabilityServiceImpl as;*/

    @Before
    public void setUp() {
        mockAmenity = mock(Amenity.class);
    }

/*    @Test
    public void testUpdate() {
        // 1. Preconditions
        when(shiftDao.getShifts(anyLong())).thenReturn(SCHEDULE);

        // 2. Exercise
        boolean result = as.updateAvailability(AMENITY_ID, new ArrayList<>());

        // 3. Postconditions
        Assert.assertEquals(result, true);

    }*/

}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NeighborhoodServiceImplTest {

    private static final long ID = 1;
    private static final String NAME = "Varsovia";

    @Mock
    private NeighborhoodDao neighborhoodDao;
    @InjectMocks
    private NeighborhoodServiceImpl ns;

    @Test
    public void testCreate() {
        // 1. Preconditions
        when(neighborhoodDao.createNeighborhood(anyString())).thenReturn(new Neighborhood.Builder()
                .neighborhoodId(ID)
                .name(NAME)
                .build()
        );

        // 2. Exercise
        Neighborhood newNeighborhood = ns.createNeighborhood(NAME);

        // 3. Postconditions
        Assert.assertNotNull(newNeighborhood);
        Assert.assertEquals(newNeighborhood.getNeighborhoodId().longValue(), ID);
        Assert.assertEquals(newNeighborhood.getName(), NAME);

    }

    @Test(expected = RuntimeException.class)
    public void testCreateAlreadyExists() {
        // 1. Preconditions
        when(neighborhoodDao.createNeighborhood(eq(NAME))).thenThrow(RuntimeException.class);

        // 2. Exercise
        Neighborhood newNeighborhood = ns.createNeighborhood(NAME);

        // 3. Postconditions
    }

}

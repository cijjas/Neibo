package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ResourceDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.MainEntities.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ResourceServiceImplTest {

    private static final long ID = 1;
    private static final String DESCRIPTION = "Varsovia de noche";
    private static final long IMAGE_ID = 1;
    private static final String TITLE = "Varsovia";
    private static final long NEIGHBORHOOD_ID = 1;
    MultipartFile mockImageFile;
    @Mock
    private ResourceDao resourceDao;
    @Mock
    private ImageService imageService;
    @InjectMocks
    private ResourceServiceImpl rs;

    @Test
    public void testCreate() {
        /*// 1. Preconditions
        when(resourceDao.createResource(anyLong(), anyString(), anyString(), anyLong())).thenReturn(new Resource.Builder()
                .resourceId(ID)
                .description(DESCRIPTION)
                .imageId(IMAGE_ID)
                .title(TITLE)
                .neighborhoodId(NEIGHBORHOOD_ID)
                .build()
        );

        // 2. Exercise
        Resource newResource = rs.createResource(NEIGHBORHOOD_ID, TITLE, DESCRIPTION, mockImageFile);

        // 3. Postconditions
        Assert.assertNotNull(newResource);
        Assert.assertEquals(newResource.getResourceId().longValue(), ID);
        Assert.assertEquals(newResource.getDescription(), DESCRIPTION);
        Assert.assertEquals(newResource.getImageId().longValue(), IMAGE_ID);
        Assert.assertEquals(newResource.getTitle(), TITLE);
        Assert.assertEquals(newResource.getNeighborhoodId().longValue(), NEIGHBORHOOD_ID);
*/
    }

}

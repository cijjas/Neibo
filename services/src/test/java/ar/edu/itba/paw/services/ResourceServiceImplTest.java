package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ResourceDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ResourceServiceImplTest {

    @Mock
    private ResourceDao resourceDao;
    @Mock
    private ImageService imageService;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    public void update_noImage() {
        // Pre Conditions
        long neighborhoodId = 1L;
        long resourceId = 1L;
        String newTitle = "Updated Title";
        String newDescription = "Updated Description";

        Resource resource = new Resource.Builder().build();
        when(resourceDao.findResource(neighborhoodId, resourceId)).thenReturn(Optional.of(resource));

        // Exercise
        resourceService.updateResource(neighborhoodId, resourceId, newTitle, newDescription, null);

        // Validations & Post Conditions
        assertEquals(newTitle, resource.getTitle());
        assertEquals(newDescription, resource.getDescription());
        assertNull(resource.getImage());

        verify(resourceDao, times(1)).findResource(neighborhoodId, resourceId);
        verify(imageService, never()).findImage(anyLong());
    }

    @Test
    public void update_withImage() {
        // Pre Conditions
        long resourceId = 1L;
        long neighborhoodId = 1L;
        String newTitle = "Updated Title";
        String newDescription = "Updated Description";
        long imageId = 100L;

        Resource resource = new Resource.Builder().build();
        Image image = new Image.Builder().build();
        when(resourceDao.findResource(neighborhoodId, resourceId)).thenReturn(Optional.of(resource));
        when(imageService.findImage(imageId)).thenReturn(Optional.of(image));

        // Exercise
        resourceService.updateResource(neighborhoodId, resourceId, newTitle, newDescription, imageId);

        // Validations & Post Conditions
        assertEquals(newTitle, resource.getTitle());
        assertEquals(newDescription, resource.getDescription());
        assertEquals(image, resource.getImage());

        verify(resourceDao, times(1)).findResource(neighborhoodId, resourceId);
        verify(imageService, times(1)).findImage(imageId);
    }
}

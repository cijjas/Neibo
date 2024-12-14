package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ResourceDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Entities.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
    public void testUpdateResourceWithoutImage() {
        long resourceId = 1L;
        String newTitle = "Updated Title";
        String newDescription = "Updated Description";

        Resource resource = new Resource.Builder().build();
        when(resourceDao.findResource(resourceId)).thenReturn(Optional.of(resource));

        resourceService.updateResource(resourceId, newTitle, newDescription, null);

        // Verify updates
        assertEquals(newTitle, resource.getTitle());
        assertEquals(newDescription, resource.getDescription());
        assertNull(resource.getImage());

        // Verify DAO and service interactions
        verify(resourceDao, times(1)).findResource(resourceId);
        verify(imageService, never()).findImage(anyLong());
    }

    @Test
    public void testUpdateResourceWithImage() {
        long resourceId = 1L;
        String newTitle = "Updated Title";
        String newDescription = "Updated Description";
        long imageId = 100L;

        Resource resource = new Resource.Builder().build();
        Image image = new Image.Builder().build();
        when(resourceDao.findResource(resourceId)).thenReturn(Optional.of(resource));
        when(imageService.findImage(imageId)).thenReturn(Optional.of(image));

        resourceService.updateResource(resourceId, newTitle, newDescription, imageId);

        // Verify updates
        assertEquals(newTitle, resource.getTitle());
        assertEquals(newDescription, resource.getDescription());
        assertEquals(image, resource.getImage());

        // Verify DAO and service interactions
        verify(resourceDao, times(1)).findResource(resourceId);
        verify(imageService, times(1)).findImage(imageId);
    }
}

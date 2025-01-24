package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.WorkerDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.models.Entities.Worker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkerServiceImplTest {

    @Mock
    private User mockUser;
    @Mock
    private WorkerDao workerDao;
    @Mock
    private ImageService imageService;

    @InjectMocks
    private WorkerServiceImpl workerService;

    @Test
    public void update_image() {
        // Pre Conditions
        long workerId = 1L;
        String phoneNumber = "123456789";
        String address = "123 Main St";
        String businessName = "Worker's Business";
        String bio = "Experienced worker";
        long backgroundPictureId = 101L;

        Worker worker = new Worker.Builder().build();
        Image backgroundPicture = new Image.Builder().imageId(backgroundPictureId).build();
        when(workerDao.findWorker(workerId)).thenReturn(Optional.of(worker));
        when(imageService.findImage(backgroundPictureId)).thenReturn(Optional.of(backgroundPicture));

        // Exercise
        workerService.updateWorker(workerId, businessName, address, phoneNumber, backgroundPictureId, bio);

        // Validation & Post Conditions
        assertEquals(phoneNumber, worker.getPhoneNumber());
        assertEquals(address, worker.getAddress());
        assertEquals(businessName, worker.getBusinessName());
        assertEquals(bio, worker.getBio());
        assertEquals((Long) backgroundPictureId, worker.getBackgroundPicture().getImageId());

        verify(workerDao, times(1)).findWorker(workerId);
        verify(imageService, times(1)).findImage(backgroundPictureId);
    }

    @Test
    public void update_noImage() {
        // Pre Conditions
        long workerId = 1L;
        String phoneNumber = "987654321";

        Worker worker = new Worker.Builder().build();
        when(workerDao.findWorker(workerId)).thenReturn(Optional.of(worker));

        // Exercise
        workerService.updateWorker(workerId, null, null, phoneNumber, null, null);

        // Validation & Post Conditions
        assertEquals(phoneNumber, worker.getPhoneNumber());
        assertNull(worker.getBusinessName());
        assertNull(worker.getBio());
        assertNull(worker.getBackgroundPicture());
        assertNull(worker.getAddress());

        verify(workerDao, times(1)).findWorker(workerId);
        verify(imageService, never()).findImage(anyLong());
    }

    @Test
    public void update_empty() {
        // Pre Conditions
        long workerId = 1L;

        Worker worker = new Worker.Builder().build();
        when(workerDao.findWorker(workerId)).thenReturn(Optional.of(worker));

        // Exercise
        workerService.updateWorker(workerId, null, "", "", null, "");

        // Validation & Post Conditions
        assertNull(worker.getPhoneNumber());
        assertNull(worker.getBusinessName());
        assertNull(worker.getBio());
        assertNull(worker.getBackgroundPicture());
        assertNull(worker.getAddress());

        verify(workerDao, times(1)).findWorker(workerId);
        verify(imageService, never()).findImage(anyLong());
    }
}

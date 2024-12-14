package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.persistence.AffiliationDao;
import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.WorkerDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.models.Entities.Worker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
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
    public void testUpdateWorkerPartiallyWithAllFields() {
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

        workerService.updateWorkerPartially(workerId, phoneNumber, address, businessName, backgroundPictureId, bio);

        // Verify updates
        assertEquals(phoneNumber, worker.getPhoneNumber());
        assertEquals(address, worker.getAddress());
        assertEquals(businessName, worker.getBusinessName());
        assertEquals(bio, worker.getBio());
        assertEquals(backgroundPictureId, worker.getBackgroundPictureId().longValue());

        // Verify DAO and service interactions
        verify(workerDao, times(1)).findWorker(workerId);
        verify(imageService, times(1)).findImage(backgroundPictureId);
    }

    @Test
    public void testUpdateWorkerPartiallyWithPartialFields() {
        long workerId = 1L;
        String phoneNumber = "987654321";

        Worker worker = new Worker.Builder().build();
        when(workerDao.findWorker(workerId)).thenReturn(Optional.of(worker));

        workerService.updateWorkerPartially(workerId, phoneNumber, null, null, null, null);

        // Verify updates
        assertEquals(phoneNumber, worker.getPhoneNumber());
        assertNull(worker.getBusinessName());
        assertNull(worker.getBio());
        assertNull(worker.getBackgroundPictureId());
        assertNull(worker.getAddress());

        // Verify DAO and service interactions
        verify(workerDao, times(1)).findWorker(workerId);
        verify(imageService, never()).findImage(anyLong());
    }

    @Test
    public void testUpdateWorkerPartiallyWithEmptyFields() {
        long workerId = 1L;

        Worker worker = new Worker.Builder().build();
        when(workerDao.findWorker(workerId)).thenReturn(Optional.of(worker));

        workerService.updateWorkerPartially(workerId, "", "", null, null, "");

        // Verify no updates were made
        assertNull(worker.getPhoneNumber());
        assertNull(worker.getBusinessName());
        assertNull(worker.getBio());
        assertNull(worker.getBackgroundPictureId());
        assertNull(worker.getAddress());

        // Verify DAO and service interactions
        verify(workerDao, times(1)).findWorker(workerId);
        verify(imageService, never()).findImage(anyLong());
    }

}

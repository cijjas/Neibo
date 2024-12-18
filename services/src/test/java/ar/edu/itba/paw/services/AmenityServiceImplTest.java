package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Shift;
import ar.edu.itba.paw.models.Entities.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class AmenityServiceImplTest {

    @Mock
    private AmenityDao amenityDao;
    @Mock
    private UserDao userDao;
    @Mock
    private AvailabilityDao availabilityDao;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AmenityServiceImpl amenityService;

    @Test
    public void create_selectedShifts() {
        // Pre Conditions
        String name = "Swimming Pool";
        String description = "A community swimming pool";
        long neighborhoodId = 1L;
        List<Long> selectedShiftsIds = Arrays.asList(1L, 2L, 3L);
        int page = 1;
        int size = 500;
        Amenity mockAmenity = new Amenity.Builder().amenityId(1L).build();

        List<User> mockUsers = new ArrayList<>();
        for (int i = 0; i < 750; i++)
            mockUsers.add(new User.Builder().build());

        when(amenityDao.createAmenity(neighborhoodId, description, name)).thenReturn(mockAmenity);
        when(userDao.countUsers(neighborhoodId, (long) UserRole.NEIGHBOR.getId())).thenReturn(750);
        when(userDao.getUsers(eq(neighborhoodId), eq((long) UserRole.NEIGHBOR.getId()), eq(page), eq(size)))
                .thenReturn(mockUsers);
        when(userDao.getUsers(eq(neighborhoodId), eq((long) UserRole.NEIGHBOR.getId()), eq(page + 1), eq(size)))
                .thenReturn(Collections.emptyList());

        // Exercise
        Amenity result = amenityService.createAmenity(neighborhoodId, name, description, selectedShiftsIds);

        // Validations & Post Conditions
        verify(amenityDao, times(1)).createAmenity(neighborhoodId, description, name);

        for (Long shiftId : selectedShiftsIds) {
            verify(availabilityDao).createAvailability(shiftId, mockAmenity.getAmenityId());
        }

        verify(availabilityDao, times(selectedShiftsIds.size()))
                .createAvailability(anyLong(), eq(mockAmenity.getAmenityId()));

        verify(emailService, times(1)).sendBatchNewAmenityMail(neighborhoodId, name, description);
        verify(emailService, times(1)).sendNewAmenityMail(neighborhoodId, name, description, mockUsers);

        verify(userDao, times(2)).getUsers(eq(neighborhoodId), eq((long) UserRole.NEIGHBOR.getId()), anyInt(), eq(size));

        assertNotNull(result);
        assertEquals(mockAmenity, result);
    }

    @Test
    public void create_emptySelectedShifts() {
        // Pre Conditions
        String name = "Swimming Pool";
        String description = "A community swimming pool";
        long neighborhoodId = 1L;
        List<Long> selectedShiftsIds = Collections.emptyList();
        int page = 1;
        int size = 500;

        Amenity mockAmenity = new Amenity.Builder().amenityId(1L).build();
        List<User> mockUsers = new ArrayList<>();

        for (int i = 0; i < 750; i++)
            mockUsers.add(new User.Builder().build());

        when(amenityDao.createAmenity(neighborhoodId, description, name)).thenReturn(mockAmenity);
        when(userDao.countUsers(neighborhoodId, (long) UserRole.NEIGHBOR.getId())).thenReturn(750);
        when(userDao.getUsers(eq(neighborhoodId), eq((long) UserRole.NEIGHBOR.getId()), eq(page), eq(size)))
                .thenReturn(mockUsers);
        when(userDao.getUsers(eq(neighborhoodId), eq((long) UserRole.NEIGHBOR.getId()), eq(page + 1), eq(size)))
                .thenReturn(Collections.emptyList());

        // Exercise
        Amenity result = amenityService.createAmenity(neighborhoodId, name, description, selectedShiftsIds);

        // Validation & Post Conditions
        verify(amenityDao, times(1)).createAmenity(neighborhoodId, description, name);
        verify(availabilityDao, times(0)).createAvailability(anyLong(), anyLong());
        verify(emailService, times(1)).sendBatchNewAmenityMail(neighborhoodId, name, description);
        verify(emailService, times(1)).sendNewAmenityMail(neighborhoodId, name, description, mockUsers);
        verify(userDao, times(2)).getUsers(eq(neighborhoodId), eq((long) UserRole.NEIGHBOR.getId()), anyInt(), eq(size));

        assertNotNull(result);
        assertEquals(mockAmenity, result);
    }

    @Test
    public void create_nullSelectedShifts() {
        // Pre Conditions
        String name = "Tennis Court";
        String description = "A well-maintained tennis court";
        long neighborhoodId = 2L;
        List<Long> selectedShiftsIds = null; // Simulating the null case
        int page = 1;
        int size = 500;

        Amenity mockAmenity = new Amenity.Builder().amenityId(2L).build();
        List<User> mockUsers = new ArrayList<>();

        for (int i = 0; i < 750; i++)
            mockUsers.add(new User.Builder().build());

        when(amenityDao.createAmenity(neighborhoodId, description, name)).thenReturn(mockAmenity);
        when(userDao.countUsers(neighborhoodId, (long) UserRole.NEIGHBOR.getId())).thenReturn(750);
        when(userDao.getUsers(eq(neighborhoodId), eq((long) UserRole.NEIGHBOR.getId()), eq(page), eq(size)))
                .thenReturn(mockUsers);
        when(userDao.getUsers(eq(neighborhoodId), eq((long) UserRole.NEIGHBOR.getId()), eq(page + 1), eq(size)))
                .thenReturn(Collections.emptyList());

        // Exercise
        Amenity result = amenityService.createAmenity(neighborhoodId, name, description, selectedShiftsIds);

        // Validation & Post Conditions
        verify(amenityDao, times(1)).createAmenity(neighborhoodId, description, name);
        verify(availabilityDao, times(0)).createAvailability(anyLong(), anyLong()); // Ensure no availability is created
        verify(emailService, times(1)).sendBatchNewAmenityMail(neighborhoodId, name, description);
        verify(emailService, times(1)).sendNewAmenityMail(neighborhoodId, name, description, mockUsers);
        verify(userDao, times(2)).getUsers(eq(neighborhoodId), eq((long) UserRole.NEIGHBOR.getId()), anyInt(), eq(size));

        assertNotNull(result);
        assertEquals(mockAmenity, result);
    }

    @Test
    public void update_emptySelectedShifts_emptyAvailableShifts() {
        // Pre Conditions
        long amenityId = 1L;
        Amenity amenity = new Amenity.Builder().build();
        amenity.setAvailableShifts(Collections.emptyList());
        when(amenityDao.findAmenity(amenityId)).thenReturn(Optional.of(amenity));

        // Exercise
        Amenity updatedAmenity = amenityService.updateAmenityPartially(amenityId, "New Name", "New Description", null);

        // Validation & Post Conditions
        assertEquals("New Name", updatedAmenity.getName());
        assertEquals("New Description", updatedAmenity.getDescription());
        verify(availabilityDao, never()).findAvailability(anyLong(), anyLong());
        verify(availabilityDao, never()).createAvailability(anyLong(), anyLong());
    }

    @Test
    public void update_selectedShifts_emptyAvailableShifts() {
        // Pre Conditions
        long amenityId = 1L;

        Amenity amenity = new Amenity.Builder().build();
        amenity.setAvailableShifts(Collections.emptyList());

        when(amenityDao.findAmenity(amenityId)).thenReturn(Optional.of(amenity));

        // Exercise
        Amenity updatedAmenity = amenityService.updateAmenityPartially(amenityId, null, null, Arrays.asList(1L, 2L));

        // Validation & Post Conditions
        verify(availabilityDao).createAvailability(1L, amenityId);
        verify(availabilityDao).createAvailability(2L, amenityId);
    }

    @Test
    public void update_selectedShifts_availableShifts() {
        // Pre Conditions
        long amenityId = 1L;
        Shift shift1 = new Shift.Builder().shiftId(1L).build();
        Shift shift2 = new Shift.Builder().shiftId(2L).build();
        Shift shift3 = new Shift.Builder().shiftId(3L).build();

        Amenity amenity = new Amenity.Builder().build();

        amenity.setAvailableShifts(Arrays.asList(shift1, shift2));

        when(amenityDao.findAmenity(amenityId)).thenReturn(Optional.of(amenity));

        // Exercise
        Amenity updatedAmenity = amenityService.updateAmenityPartially(amenityId, null, null, Arrays.asList(2L, 3L));

        // Validation & Post Conditions
        verify(availabilityDao).deleteAvailability(1L, amenityId);
        verify(availabilityDao).createAvailability(3L, amenityId);
    }

    @Test
    public void update_emptySelectedShifts_availableShifts() {
        // Pre Conditions
        long amenityId = 1L;
        Shift shift1 = new Shift.Builder().shiftId(1L).build();
        Shift shift2 = new Shift.Builder().shiftId(2L).build();

        Amenity amenity = new Amenity.Builder().build();
        amenity.setAvailableShifts(Arrays.asList(shift1, shift2));

        when(amenityDao.findAmenity(amenityId)).thenReturn(Optional.of(amenity));

        // Exercise
        Amenity updatedAmenity = amenityService.updateAmenityPartially(amenityId, null, null, Collections.emptyList());

        // Validation & Post Conditions
        verify(availabilityDao).deleteAvailability(1L, amenityId);
        verify(availabilityDao).deleteAvailability(2L, amenityId);
        verifyNoMoreInteractions(availabilityDao);
    }
}

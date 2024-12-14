package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Shift;
import ar.edu.itba.paw.models.Entities.User;
import javassist.NotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

import org.mockito.junit.MockitoJUnitRunner;

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
    public void testCreateAmenity() {
        // Arrange
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

        when(amenityDao.createAmenity(name, description, neighborhoodId)).thenReturn(mockAmenity);
        when(userDao.countUsers((long) UserRole.NEIGHBOR.getId(), neighborhoodId)).thenReturn(750);
        when(userDao.getUsers(eq((long) UserRole.NEIGHBOR.getId()), eq(neighborhoodId), eq(page), eq(size)))
                .thenReturn(mockUsers);
        when(userDao.getUsers(eq((long) UserRole.NEIGHBOR.getId()), eq(neighborhoodId), eq(page + 1), eq(size)))
                .thenReturn(Collections.emptyList());

        // Act
        Amenity result = amenityService.createAmenity(name, description, neighborhoodId, selectedShiftsIds);

        // Assert
        verify(amenityDao, times(1)).createAmenity(name, description, neighborhoodId);

        for (Long shiftId : selectedShiftsIds) {
            verify(availabilityDao).createAvailability(mockAmenity.getAmenityId(), shiftId);
        }

        verify(availabilityDao, times(selectedShiftsIds.size()))
                .createAvailability(eq(mockAmenity.getAmenityId()), anyLong());

        verify(emailService, times(1)).sendBatchNewAmenityMail(neighborhoodId, name, description);
        verify(emailService, times(1)).sendNewAmenityMail(neighborhoodId, name, description, mockUsers);

        verify(userDao, times(2)).getUsers(eq((long) UserRole.NEIGHBOR.getId()), eq(neighborhoodId), anyInt(), eq(size));

        assertNotNull(result);
        assertEquals(mockAmenity, result);
    }

    @Test
    public void testCreateAmenityWithNoSelectedShifts() {
        // Arrange
        String name = "Swimming Pool";
        String description = "A community swimming pool";
        long neighborhoodId = 1L;
        List<Long> selectedShiftsIds = Collections.emptyList(); // No shifts selected
        int page = 1;
        int size = 500;

        Amenity mockAmenity = new Amenity.Builder().amenityId(1L).build();
        List<User> mockUsers = new ArrayList<>();

        for (int i = 0; i < 750; i++)
            mockUsers.add(new User.Builder().build());

        when(amenityDao.createAmenity(name, description, neighborhoodId)).thenReturn(mockAmenity);
        when(userDao.countUsers((long) UserRole.NEIGHBOR.getId(), neighborhoodId)).thenReturn(750);
        when(userDao.getUsers(eq((long) UserRole.NEIGHBOR.getId()), eq(neighborhoodId), eq(page), eq(size)))
                .thenReturn(mockUsers);
        when(userDao.getUsers(eq((long) UserRole.NEIGHBOR.getId()), eq(neighborhoodId), eq(page + 1), eq(size)))
                .thenReturn(Collections.emptyList());

        // Act
        Amenity result = amenityService.createAmenity(name, description, neighborhoodId, selectedShiftsIds);

        // Assert
        verify(amenityDao, times(1)).createAmenity(name, description, neighborhoodId);

        // Since there are no selected shifts, `createAvailability` should not be called
        verify(availabilityDao, times(0)).createAvailability(anyLong(), anyLong());

        verify(emailService, times(1)).sendBatchNewAmenityMail(neighborhoodId, name, description);

        verify(emailService, times(1)).sendNewAmenityMail(neighborhoodId, name, description, mockUsers);

        verify(userDao, times(2)).getUsers(eq((long) UserRole.NEIGHBOR.getId()), eq(neighborhoodId), anyInt(), eq(size));

        assertNotNull(result);
        assertEquals(mockAmenity, result);
    }


    @Test
    public void testUpdateAmenityPartially_ShiftIdsIsNull() {
        long amenityId = 1L;

        Amenity amenity = new Amenity.Builder().build();

        amenity.setAvailableShifts(Collections.emptyList());

        when(amenityDao.findAmenity(amenityId)).thenReturn(Optional.of(amenity));

        Amenity updatedAmenity = amenityService.updateAmenityPartially(amenityId, "New Name", "New Description", null);

        assertEquals("New Name", updatedAmenity.getName());
        assertEquals("New Description", updatedAmenity.getDescription());
        verify(availabilityDao, never()).findAvailability(anyLong(), anyLong());
        verify(availabilityDao, never()).createAvailability(anyLong(), anyLong());
    }

    @Test
    public void testUpdateAmenityPartially_ShiftIdsIsEmpty() {
        long amenityId = 1L;
        Shift shift1 = new Shift.Builder().shiftId(1L).build();
        Shift shift2 = new Shift.Builder().shiftId(2L).build();

        Amenity amenity = new Amenity.Builder().build();
        amenity.setAvailableShifts(Arrays.asList(shift1, shift2));

        when(amenityDao.findAmenity(amenityId)).thenReturn(Optional.of(amenity));

        Amenity updatedAmenity = amenityService.updateAmenityPartially(amenityId, null, null, Collections.emptyList());

        verify(availabilityDao).deleteAvailability(amenityId, 1L);
        verify(availabilityDao).deleteAvailability(amenityId, 2L);
        verifyNoMoreInteractions(availabilityDao);
    }

    @Test
    public void testUpdateAmenityPartially_NoShiftsToRemove() {
        long amenityId = 1L;

        Amenity amenity = new Amenity.Builder().build();
        amenity.setAvailableShifts(Collections.emptyList());

        when(amenityDao.findAmenity(amenityId)).thenReturn(Optional.of(amenity));

        Amenity updatedAmenity = amenityService.updateAmenityPartially(amenityId, null, null, Arrays.asList(1L, 2L));

        verify(availabilityDao).createAvailability(amenityId, 1L);
        verify(availabilityDao).createAvailability(amenityId, 2L);
    }

    @Test
    public void testUpdateAmenityPartially_AddAndRemoveShifts() {
        long amenityId = 1L;
        Shift shift1 = new Shift.Builder().shiftId(1L).build();
        Shift shift2 = new Shift.Builder().shiftId(2L).build();
        Shift shift3 = new Shift.Builder().shiftId(3L).build();

        Amenity amenity = new Amenity.Builder().build();

        amenity.setAvailableShifts(Arrays.asList(shift1, shift2));

        when(amenityDao.findAmenity(amenityId)).thenReturn(Optional.of(amenity));

        Amenity updatedAmenity = amenityService.updateAmenityPartially(amenityId, null, null, Arrays.asList(2L, 3L));

        verify(availabilityDao).deleteAvailability(amenityId, 1L);
        verify(availabilityDao).createAvailability(amenityId, 3L);
    }

    @Test
    public void testUpdateAmenityPartially_AllShiftsRemoved() {
        long amenityId = 1L;
        Shift shift1 = new Shift.Builder().shiftId(1L).build();
        Shift shift2 = new Shift.Builder().shiftId(2L).build();

        Amenity amenity = new Amenity.Builder().build();
        amenity.setAvailableShifts(Arrays.asList(shift1, shift2));

        when(amenityDao.findAmenity(amenityId)).thenReturn(Optional.of(amenity));

        Amenity updatedAmenity = amenityService.updateAmenityPartially(amenityId, null, null, Collections.emptyList());

        verify(availabilityDao).deleteAvailability(amenityId, 1L);
        verify(availabilityDao).deleteAvailability(amenityId, 2L);
    }
}

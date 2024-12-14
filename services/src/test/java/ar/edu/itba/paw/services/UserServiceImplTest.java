package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private ImageService imageService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testCreateNeighborWithLanguageSpecified() {
        // Arrange
        String email = "neighbor1@example.com";
        String password = "password";
        String name = "John";
        String surname = "Doe";
        long neighborhoodId = 1L;
        long languageId = 2L; // Non-null languageId
        Integer identification = 123;

        Language expectedLanguage = Language.fromId(languageId);
        User mockUser = mock(User.class);

        when(userDao.createUser(eq(email), anyString(), eq(name), eq(surname), eq(neighborhoodId), eq(expectedLanguage),
                eq(false), eq(UserRole.UNVERIFIED_NEIGHBOR), eq(identification)))
                .thenReturn(mockUser);

        when(userDao.findUser(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(anyString());

        // Act
        User result = userService.createNeighbor(email, password, name, surname, neighborhoodId, languageId, identification);

        // Assert
        verify(userDao, times(1)).createUser(eq(email), anyString(), eq(name), eq(surname), eq(neighborhoodId),
                eq(expectedLanguage), eq(false), eq(UserRole.UNVERIFIED_NEIGHBOR), eq(identification));
        verify(emailService, times(1)).sendNewUserMail(eq(neighborhoodId), eq(name), eq(UserRole.NEIGHBOR));
        assertNotNull(result);
        assertEquals(mockUser, result);
    }

    @Test
    public void testCreateNeighborWithoutLanguageSpecified() {
        // Arrange
        String email = "neighbor2@example.com";
        String password = "password";
        String name = "John";
        String surname = "Doe";
        long neighborhoodId = 1L;
        Long languageId = null; // No languageId
        Integer identification = 1234;

        Language defaultLanguage = Language.ENGLISH;
        User mockUser = mock(User.class);

        when(userDao.createUser(eq(email), anyString(), eq(name), eq(surname), eq(neighborhoodId), eq(defaultLanguage),
                eq(false), eq(UserRole.UNVERIFIED_NEIGHBOR), eq(identification)))
                .thenReturn(mockUser);

        when(userDao.findUser(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(anyString());

        // Act
        User result = userService.createNeighbor(email, password, name, surname, neighborhoodId, languageId, identification);

        // Assert
        verify(userDao, times(1)).createUser(eq(email), anyString(), eq(name), eq(surname), eq(neighborhoodId),
                eq(defaultLanguage), eq(false), eq(UserRole.UNVERIFIED_NEIGHBOR), eq(identification));
        verify(emailService, times(1)).sendNewUserMail(eq(neighborhoodId), eq(name), eq(UserRole.NEIGHBOR));
        assertNotNull(result);
        assertEquals(mockUser, result);
    }

    @Test
    public void testCreateNeighborWhenEmailNotFoundAndSendNewUserMailCalled() {
        // Arrange
        String email = "neighbor3@example.com";
        String password = "password";
        String name = "John";
        String surname = "Doe";
        long neighborhoodId = 1L;
        Long languageId = null; // No languageId
        Integer identification = 123;

        User mockUser = mock(User.class);

        when(userDao.findUser(email)).thenReturn(Optional.empty());
        when(userDao.createUser(eq(email), anyString(), eq(name), eq(surname), eq(neighborhoodId), eq(Language.ENGLISH),
                eq(false), eq(UserRole.UNVERIFIED_NEIGHBOR), eq(identification)))
                .thenReturn(mockUser);
        when(passwordEncoder.encode(password)).thenReturn(anyString());

        // Act
        User result = userService.createNeighbor(email, password, name, surname, neighborhoodId, languageId, identification);

        // Assert
        verify(userDao, times(1)).createUser(eq(email), anyString(), eq(name), eq(surname), eq(neighborhoodId),
                eq(Language.ENGLISH), eq(false), eq(UserRole.UNVERIFIED_NEIGHBOR), eq(identification));
        verify(emailService, times(1)).sendNewUserMail(eq(neighborhoodId), eq(name), eq(UserRole.NEIGHBOR)); // sendNewUserMail is called
        assertNotNull(result);
        assertEquals(mockUser, result);
    }

    @Test
    public void testCreateNeighborWhenEmailNotFoundAndSendNewUserMailNotCalled() {
        // Arrange
        String email = "neighbor4@example.com";
        String password = "password";
        String name = "John";
        String surname = "Doe";
        long neighborhoodId = 0L; // Invalid neighborhoodId (No send mail)
        Long languageId = null; // No languageId
        Integer identification = 123;

        User mockUser = mock(User.class);

        when(userDao.findUser(email)).thenReturn(Optional.empty());
        when(userDao.createUser(eq(email), anyString(), eq(name), eq(surname), eq(neighborhoodId), eq(Language.ENGLISH),
                eq(false), eq(UserRole.UNVERIFIED_NEIGHBOR), eq(identification)))
                .thenReturn(mockUser);
        when(passwordEncoder.encode(password)).thenReturn(anyString());

        // Act
        User result = userService.createNeighbor(email, password, name, surname, neighborhoodId, languageId, identification);

        // Assert
        verify(userDao, times(1)).createUser(eq(email), anyString(), eq(name), eq(surname), eq(neighborhoodId),
                eq(Language.ENGLISH), eq(false), eq(UserRole.UNVERIFIED_NEIGHBOR), eq(identification));
        verify(emailService, times(0)).sendNewUserMail(eq(neighborhoodId), eq(name), eq(UserRole.NEIGHBOR)); // sendNewUserMail not called
        assertNotNull(result);
        assertEquals(mockUser, result);
    }

    @Test
    public void testCreateNeighbor_UserExistsWithNullPassword() {
        // Arrange
        String mail = "test@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        String name = "John";
        String surname = "Doe";
        long neighborhoodId = 123L;
        Long languageId = 1L; // Assuming 1L corresponds to Language.ENGLISH
        Integer identification = 456;

        User existingUser = new User.Builder().build();
        existingUser.setMail(mail);
        existingUser.setPassword(null); // Simulating a user with a null password

        when(userDao.findUser(mail)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        // Act
        User result = userService.createNeighbor(mail, password, name, surname, neighborhoodId, languageId, identification);

        // Assert
        assertNotNull(result);
        assertEquals(encodedPassword, existingUser.getPassword());
        assertEquals(Language.ENGLISH, existingUser.getLanguage());
        assertEquals(UserRole.UNVERIFIED_NEIGHBOR, existingUser.getRole());
        assertEquals(name, existingUser.getName());
        assertEquals(surname, existingUser.getSurname());
        assertEquals(identification, existingUser.getIdentification());
        assertFalse(existingUser.isDarkMode());

        verify(userDao, times(1)).findUser(mail);
        verify(passwordEncoder, times(1)).encode(password);
    }

    @Test
    public void testUpdateUserWithAllFields() {
        long userId = 1L;
        String mail = "newmail@example.com";
        String name = "John";
        String surname = "Doe";
        String password = "newpassword";
        Boolean darkMode = true;
        String phoneNumber = "1234567890";
        long profilePictureId = 101L;
        int identification = 12345;
        long languageId = 1L;
        long userRoleId = 2L;

        User user = new User.Builder().build();
        Image profilePicture = new Image.Builder().build();
        when(userDao.findUser(userId)).thenReturn(Optional.of(user));
        when(imageService.findImage(profilePictureId)).thenReturn(Optional.of(profilePicture));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        userService.updateUser(userId, mail, name, surname, password, darkMode, phoneNumber, profilePictureId, identification, languageId, userRoleId);

        // Verify updates
        assertEquals(mail, user.getMail());
        assertEquals(name, user.getName());
        assertEquals(surname, user.getSurname());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals(darkMode, user.getDarkMode());
        assertEquals(phoneNumber, user.getPhoneNumber());
        assertEquals(profilePicture, user.getProfilePicture());
        assertEquals(identification, user.getIdentification().longValue());
        assertEquals(Language.fromId(languageId), user.getLanguage());
        assertEquals(UserRole.fromId(userRoleId), user.getRole());

        // Verify DAO and service interactions
        verify(userDao, times(1)).findUser(userId);
        verify(imageService, times(1)).findImage(profilePictureId);
        verify(passwordEncoder, times(1)).encode(password);
    }

    @Test
    public void testUpdateUserWithPartialFields() {
        long userId = 1L;
        String mail = "newmail@example.com";
        Boolean darkMode = false;

        User user = new User.Builder().build();
        when(userDao.findUser(userId)).thenReturn(Optional.of(user));

        userService.updateUser(userId, mail, null, null, null, darkMode, null, null, null, null, null);

        // Verify updates
        assertEquals(mail, user.getMail());
        assertEquals(darkMode, user.getDarkMode());
        assertNull(user.getName());
        assertNull(user.getSurname());
        assertNull(user.getPhoneNumber());
        assertNull(user.getProfilePicture());
        assertNull(user.getPassword());

        // Verify DAO and service interactions
        verify(userDao, times(1)).findUser(userId);
        verify(imageService, never()).findImage(anyLong());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    public void testUpdateUserWithPasswordEncoding() {
        long userId = 1L;
        String password = "newpassword";

        User user = new User.Builder().build();
        when(userDao.findUser(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        userService.updateUser(userId, null, null, null, password, null, null, null, null, null, null);

        // Verify password is encoded and set
        assertEquals("encodedPassword", user.getPassword());

        // Verify DAO and service interactions
        verify(userDao, times(1)).findUser(userId);
        verify(passwordEncoder, times(1)).encode(password);
    }
}

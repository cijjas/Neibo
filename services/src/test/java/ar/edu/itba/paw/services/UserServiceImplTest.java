package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
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
}

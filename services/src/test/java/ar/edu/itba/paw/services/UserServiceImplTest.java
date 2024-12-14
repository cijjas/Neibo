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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    public void testCreateNeighborWhenEmailFoundAndPasswordSet() {
/*
        // Arrange
        String email = "neighbor5@example.com";
        String password = "password";
        String name = "John";
        String surname = "Doe";
        long neighborhoodId = 1L;
        Long languageId = null; // No languageId
        Integer identification = 123;

        // Mock the existing user and its behavior
        User existingUser = mock(User.class);
        when(userDao.findUser(email)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(existingUser.getPassword()).thenReturn("password");

        // Act
        User result = userService.createNeighbor(email, password, name, surname, neighborhoodId, languageId, identification);

        // Assert
        System.out.println(userDao);
        UserDao u = verify(userDao, never());
        System.out.println(u == null? "is null" : "not null");
        assert u != null;
        u.createUser(any(), any(), any(), any(), any(), any(), any(), any(), any()); // Ensure no user creation
        verify(existingUser, times(1)).setPassword(eq("encodedPassword")); // Ensure password is set
        assertNotNull(result);
        assertEquals(existingUser, result); // Ensure the returned user is the same
*/
    }
}

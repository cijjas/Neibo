package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    public void create() {
        // Pre Conditions
        String email = "neighbor1@example.com";
        String password = "password";
        String name = "John";
        String surname = "Doe";
        long neighborhoodId = 1L;
        long languageId = 2L;
        long userRoleId = 1L;
        Integer identification = 123;

        Language expectedLanguage = Language.fromId(languageId);
        UserRole expectedUserRole = UserRole.fromId(userRoleId);
        User mockUser = mock(User.class);

        when(userDao.createUser(eq(neighborhoodId), eq(email), eq(name), eq(surname), anyString(), eq(identification), eq(expectedLanguage),
                eq(false), eq(expectedUserRole)))
                .thenReturn(mockUser);

        when(userDao.findUser(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(anyString());

        // Exercise
        User result = userService.createUser(neighborhoodId, email, name, surname, password, identification, languageId, userRoleId);

        // Validation & Post Conditions
        verify(userDao, times(1)).createUser(eq(neighborhoodId), eq(email), eq(name), eq(surname), anyString(),
                eq(identification), eq(expectedLanguage), eq(false), eq(expectedUserRole));
        verify(emailService, times(1)).sendNewUserMail(eq(neighborhoodId), eq(name), eq(UserRole.NEIGHBOR));
        assertNotNull(result);
        assertEquals(mockUser, result);
    }

    @Test
    public void create_noLanguage() {
        // Pre Conditions
        String email = "neighbor2@example.com";
        String password = "password";
        String name = "John";
        String surname = "Doe";
        long neighborhoodId = 1L;
        long userRoleId = 1L;
        Long languageId = null;
        Integer identification = 1234;

        Language defaultLanguage = Language.ENGLISH;
        UserRole expectedUserRole = UserRole.fromId(userRoleId);
        User mockUser = mock(User.class);

        when(userDao.createUser(eq(neighborhoodId), eq(email), eq(name), eq(surname), anyString(), eq(identification), eq(defaultLanguage),
                eq(false), eq(expectedUserRole)))
                .thenReturn(mockUser);

        when(userDao.findUser(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(anyString());

        // Exercise
        User result = userService.createUser(neighborhoodId, email, name, surname, password, identification, languageId, userRoleId);

        // Validation & Post Conditions
        verify(userDao, times(1)).createUser(eq(neighborhoodId), eq(email), eq(name), eq(surname), anyString(),
                eq(identification), eq(defaultLanguage), eq(false), eq(expectedUserRole));
        verify(emailService, times(1)).sendNewUserMail(eq(neighborhoodId), eq(name), eq(UserRole.NEIGHBOR));
        assertNotNull(result);
        assertEquals(mockUser, result);
    }

    @Test
    public void create_noRole() {
        // Pre Conditions
        String email = "neighbor2@example.com";
        String password = "password";
        String name = "John";
        String surname = "Doe";
        long neighborhoodId = 1L;
        Long userRoleId = null;
        long languageId = 1L;
        Integer identification = 1234;

        Language expectedLanguage = Language.fromId(languageId);
        UserRole defaultUserRole = UserRole.UNVERIFIED_NEIGHBOR;
        User mockUser = mock(User.class);

        when(userDao.createUser(eq(neighborhoodId), eq(email), eq(name), eq(surname), anyString(), eq(identification), eq(expectedLanguage),
                eq(false), eq(defaultUserRole)))
                .thenReturn(mockUser);

        when(userDao.findUser(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(anyString());

        // Exercise
        User result = userService.createUser(neighborhoodId, email, name, surname, password, identification, languageId, userRoleId);

        // Validation & Post Conditions
        verify(userDao, times(1)).createUser(eq(neighborhoodId), eq(email), eq(name), eq(surname), anyString(),
                eq(identification), eq(expectedLanguage), eq(false), eq(defaultUserRole));
        verify(emailService, times(1)).sendNewUserMail(eq(neighborhoodId), eq(name), eq(UserRole.NEIGHBOR));
        assertNotNull(result);
        assertEquals(mockUser, result);
    }

    @Test
    public void create_noLanguage_noRole() {
        // Pre Conditions
        String email = "neighbor2@example.com";
        String password = "password";
        String name = "John";
        String surname = "Doe";
        long neighborhoodId = 1L;
        Long userRoleId = null;
        Long languageId = null;
        Integer identification = 1234;

        Language defaultLanguage = Language.ENGLISH;
        UserRole defaultUserRole = UserRole.UNVERIFIED_NEIGHBOR;
        User mockUser = mock(User.class);

        when(userDao.createUser(eq(neighborhoodId), eq(email), eq(name), eq(surname), anyString(), eq(identification), eq(defaultLanguage),
                eq(false), eq(defaultUserRole)))
                .thenReturn(mockUser);

        when(userDao.findUser(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(anyString());

        // Exercise
        User result = userService.createUser(neighborhoodId, email, name, surname, password, identification, languageId, userRoleId);

        // Validation & Post Conditions
        verify(userDao, times(1)).createUser(eq(neighborhoodId), eq(email), eq(name), eq(surname), anyString(),
                eq(identification), eq(defaultLanguage), eq(false), eq(defaultUserRole));
        verify(emailService, times(1)).sendNewUserMail(eq(neighborhoodId), eq(name), eq(UserRole.NEIGHBOR));
        assertNotNull(result);
        assertEquals(mockUser, result);
    }

    @Test
    public void create_newUser_uniqueNeighborhood() {
        // Pre Conditions
        String email = "neighbor4@example.com";
        String password = "password";
        String name = "John";
        String surname = "Doe";
        long neighborhoodId = 0L; // Worker Neighborhood so no mail is sent
        Long languageId = null;
        Long userRoleId = null;
        Integer identification = 123;

        User mockUser = mock(User.class);

        when(userDao.findUser(email)).thenReturn(Optional.empty());
        when(userDao.createUser(eq(neighborhoodId), eq(email), eq(name), eq(surname), anyString(), eq(identification), eq(Language.ENGLISH),
                eq(false), eq(UserRole.UNVERIFIED_NEIGHBOR)))
                .thenReturn(mockUser);
        when(passwordEncoder.encode(password)).thenReturn(anyString());

        // Exercise
        User result = userService.createUser(neighborhoodId, email, name, surname, password, identification, languageId, userRoleId);

        // Validation & Post Conditions
        verify(userDao, times(1)).createUser(eq(neighborhoodId), eq(email), eq(name), eq(surname), anyString(),
                eq(identification), eq(Language.ENGLISH), eq(false), eq(UserRole.UNVERIFIED_NEIGHBOR));
        verify(emailService, times(0)).sendNewUserMail(eq(neighborhoodId), eq(name), eq(UserRole.NEIGHBOR)); // sendNewUserMail not called
        assertNotNull(result);
        assertEquals(mockUser, result);
    }

    @Test
    public void create_betaUser() {
        // Pre Conditions
        String mail = "test@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        String name = "John";
        String surname = "Doe";
        long neighborhoodId = 123L;
        long languageId = Language.ENGLISH.getId();
        long userRoleId = UserRole.UNVERIFIED_NEIGHBOR.getId();
        Integer identification = 456;

        User existingUser = new User.Builder().build();
        existingUser.setMail(mail);
        existingUser.setPassword(null);

        when(userDao.findUser(mail)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        // Exercise
        User result = userService.createUser(neighborhoodId, mail, name, surname, password, identification, languageId, userRoleId);

        // Validation & Post Conditions
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
    public void update_image() {
        // Pre Conditions
        long neighborhoodId = 1L;
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

        // Exercise
        userService.updateUser(neighborhoodId, userId, mail, name, surname, password, identification, languageId, profilePictureId, darkMode, phoneNumber, userRoleId);

        // Validation & Post Conditions
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

        verify(userDao, times(1)).findUser(userId);
        verify(imageService, times(1)).findImage(profilePictureId);
        verify(passwordEncoder, times(1)).encode(password);
    }

    @Test
    public void update_noImage() {
        // Pre Conditions
        long neighborhoodId = 1L;
        long userId = 1L;
        String mail = "newmail@example.com";
        Boolean darkMode = false;

        User user = new User.Builder().build();
        when(userDao.findUser(userId)).thenReturn(Optional.of(user));

        // Exercise
        userService.updateUser(neighborhoodId, userId, mail, null, null, null, null, null, null, darkMode, null, null);

        // Validation & Post Conditions
        assertEquals(mail, user.getMail());
        assertEquals(darkMode, user.getDarkMode());
        assertNull(user.getName());
        assertNull(user.getSurname());
        assertNull(user.getPhoneNumber());
        assertNull(user.getProfilePicture());
        assertNull(user.getPassword());

        verify(userDao, times(1)).findUser(userId);
        verify(imageService, never()).findImage(anyLong());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    public void update_password() {
        // Pre Conditions
        long neighborhoodId = 1L;
        long userId = 1L;
        String password = "newpassword";

        User user = new User.Builder().build();
        when(userDao.findUser(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        // Exercise
        userService.updateUser(neighborhoodId, userId, null, null, null, password, null, null, null, null, null, null);

        // Validation & Post Conditions
        assertEquals("encodedPassword", user.getPassword());

        verify(userDao, times(1)).findUser(userId);
        verify(passwordEncoder, times(1)).encode(password);
    }
}

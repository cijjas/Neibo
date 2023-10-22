package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.models.MainEntities.Neighborhood;
import ar.edu.itba.paw.models.MainEntities.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final long ID = 1;
    private static final String EMAIL = "pedro@mcpedro.com";
    private static final String NAME = "Pedro";
    private static final String SURNAME = "Pedrovsky";
    private static final String NEIGHBORHOOD_NAME = "Varsovia";
    private static final long NEIGHBORHOOD_ID = 1;
    private final String PASSWORD = "123456";
    private final boolean DARK_MODE = false;
    private final Language LANGUAGE = Language.ENGLISH;
    private final UserRole ROLE = UserRole.UNVERIFIED_NEIGHBOR;
    private final Date CREATION_DATE = new Date(2023, 9, 11);
    private final String IDENTIFICATION_STRING = "123456789";
    private final int IDENTIFICATION = 123456789;
    @Mock
    ImageService imageService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    EmailService emailService;
    @Mock
    NeighborhoodService neighborhoodService;
    private Neighborhood mockNeighborhood;
    @Mock
    private UserDao userDao;
    @InjectMocks
    private UserServiceImpl us;

    @Before
    public void setUp() {
        mockNeighborhood = mock(Neighborhood.class);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
    }

    @Test
    public void testCreate() {
        // 1. Preconditions
        when(userDao.createUser(anyString(), any(), anyString(), anyString(), anyLong(), any(), anyBoolean(), any(), anyInt())).thenReturn(new User.Builder()
                .userId(ID)
                .mail(EMAIL)
                .name(NAME)
                .surname(SURNAME)
                .password(PASSWORD)
                .neighborhoodId(NEIGHBORHOOD_ID)
                .darkMode(DARK_MODE)
                .language(LANGUAGE)
                .role(ROLE)
                .creationDate(CREATION_DATE)
                .identification(IDENTIFICATION)
                .build()
        );

        // 2. Exercise
        User newUser = us.createNeighbor(EMAIL, PASSWORD, NAME, SURNAME, NEIGHBORHOOD_ID, LANGUAGE, IDENTIFICATION_STRING);

        // 3. Postconditions
        Assert.assertNotNull(newUser);
        Assert.assertEquals(newUser.getUserId().longValue(), ID);
        Assert.assertEquals(newUser.getMail(), EMAIL);
        Assert.assertEquals(newUser.getName(), NAME);
        Assert.assertEquals(newUser.getSurname(), SURNAME);
        Assert.assertEquals(newUser.getLanguage(), LANGUAGE);
        Assert.assertEquals(newUser.isDarkMode(), DARK_MODE);
        Assert.assertEquals(newUser.getRole(), ROLE);
        Assert.assertEquals(newUser.getCreationDate(), CREATION_DATE);
        Assert.assertEquals(newUser.getIdentification(), IDENTIFICATION);

    }

    @Test(expected = RuntimeException.class)
    public void testCreateAlreadyExists() {
        // 1. Preconditions
        when(userDao.createUser(eq(EMAIL), eq(PASSWORD), eq(NAME), eq(SURNAME), eq(NEIGHBORHOOD_ID), eq(LANGUAGE), eq(DARK_MODE), eq(ROLE), eq(IDENTIFICATION))).thenThrow(RuntimeException.class);

        // 2. Exercise
        User newUser = us.createNeighbor(EMAIL, PASSWORD, NAME, SURNAME, NEIGHBORHOOD_ID, LANGUAGE, IDENTIFICATION_STRING);

        // 3. Postconditions
    }

}

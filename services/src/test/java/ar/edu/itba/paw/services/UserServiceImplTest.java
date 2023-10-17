package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
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

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class) // Le decimos a JUnit que corra los tests con el runner de Mockito
public class UserServiceImplTest {

    private Neighborhood mockNeighborhood;
    private static final long ID = 1;
    private static final String EMAIL = "pedro@mcpedro.com";
    private static final String NAME = "Pedro";
    private static final String SURNAME = "Pedrovsky";
    private final String PASSWORD = "123456";
    private final boolean DARK_MODE = false;
    private final Language LANGUAGE = Language.ENGLISH;
    private final UserRole ROLE = UserRole.NEIGHBOR;
    private final Date CREATION_DATE = new Date(2023, 9, 11);
    private final int IDENTIFICATION = 123456789;
    private static final String NEIGHBORHOOD_NAME = "Varsovia";

    private static final long NEIGHBORHOOD_ID = 1;


    // private final UserServiceImpl us = new UserServiceImpl(null);
    // Qué usamos como UserDao para el UserServiceImpl? No queremos conectarlo al Postgres de verdad, es una pérdida de
    // tiempo escribir un propio, por ejemplo, InMemoryTestUserDao que guarde los usuarios en un mapa en memoria...
    // Para esto generamos un mock con Mockito, y le pedimos que nos cree el UserServiceImpl inyectando la clase
    // mock-eada:
    @Mock // Le pedimos que nos genere una clase mock de UserDao
    private UserDao userDao;
    @Mock
    ImageService imageService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    EmailService emailService;
    @Mock
    NeighborhoodService neighborhoodService;
    @InjectMocks // Le pedimos que cree un UserServiceImpl, y que en el ctor (que toma un UserDao) inyecte un mock.
    private UserServiceImpl us;

    @Before
    public void setUp() {
        // Create and set up the mock Neighborhood object
        mockNeighborhood = mock(Neighborhood.class);
        when(mockNeighborhood.getName()).thenReturn(NEIGHBORHOOD_NAME);
        when(mockNeighborhood.getNeighborhoodId()).thenReturn(NEIGHBORHOOD_ID);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
    }

    @Test
    public void testCreate() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
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

        // 2. Ejercitar
        // Pruebo la funcionalidad de usuarios
        User newUser = us.createNeighbor(EMAIL, PASSWORD, NAME, SURNAME, NEIGHBORHOOD_ID, LANGUAGE, IDENTIFICATION);

        // 3. Postcondiciones
        Assert.assertNotNull(newUser);
        Assert.assertEquals(newUser.getUserId(), ID);
        Assert.assertEquals(newUser.getMail(), EMAIL);
        Assert.assertEquals(newUser.getName(), NAME);
        Assert.assertEquals(newUser.getSurname(), SURNAME);
        Assert.assertEquals(newUser.getLanguage(), LANGUAGE);
        Assert.assertEquals(newUser.isDarkMode(), DARK_MODE);
        Assert.assertEquals(newUser.getRole(), ROLE);
        Assert.assertEquals(newUser.getCreationDate(), CREATION_DATE);
        Assert.assertEquals(newUser.getIdentification(), IDENTIFICATION);

        // Verifico que se haya llamado create del UserDao una vez
        // NUNCA HAGAN ESTO, PORQUE ESTAS PROBANDO EL UserServiceImpl QUE TE IMPORTA CÓMO EL USA EL UserDao
        // Mockito.verify(userDao, times(1)).create(EMAIL, PASSWORD);
    }
    @Test(expected = RuntimeException.class) // "Espero que este test lance y falle con una exception tal"
    public void testCreateAlreadyExists() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(userDao.createUser(eq(EMAIL), eq(PASSWORD), eq(NAME), eq(SURNAME), eq(NEIGHBORHOOD_ID), eq(LANGUAGE), eq(DARK_MODE), eq(ROLE), eq(IDENTIFICATION))).thenThrow(RuntimeException.class);

        // 2. Ejercitar
        User newUser = us.createNeighbor(EMAIL, PASSWORD, NAME, SURNAME, NEIGHBORHOOD_ID, LANGUAGE, IDENTIFICATION);

        // 3. Postcondiciones
        // (Nada, espero que lo anterior tire exception)
    }

    @Test
    public void testFindById() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(userDao.findUserById(eq(ID))).thenReturn(Optional.of(new User.Builder()
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
        ));

        // 2. Ejercitar
        Optional<User> newUser = us.findUserById(ID);

        // 3. Postcondiciones
        Assert.assertTrue(newUser.isPresent());
        Assert.assertEquals(ID, newUser.get().getUserId());
    }
}

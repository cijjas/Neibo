package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.NeighborhoodWorkerDao;
import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.WorkerDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.models.Worker;
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
public class WorkerServiceImplTest {

    private User mockUser;
    private static final long USER_ID = 1;
    private static final String PHONE_NUMBER = "123456789";
    private static final String BUSINESS_NAME = "Paw";
    private static final String ADDRESS = "Calle Falsa 123";
    private static final String BIO = "Soy un trabajador, te lo juro";

    private static final String EMAIL = "pedro@mcpedro.com";
    private static final String NAME = "Pedro";
    private static final String SURNAME = "Pedrovsky";
    private final String PASSWORD = "123456";
    private final boolean DARK_MODE = false;
    private final Language LANGUAGE = Language.ENGLISH;
    private final UserRole ROLE = UserRole.NEIGHBOR;
    private final Date CREATION_DATE = new Date(2023, 9, 11);
    private final int IDENTIFICATION = 123456789;

    private static final long NEIGHBORHOOD_ID = 1;

    // private final UserServiceImpl us = new UserServiceImpl(null);
    // Qué usamos como UserDao para el UserServiceImpl? No queremos conectarlo al Postgres de verdad, es una pérdida de
    // tiempo escribir un propio, por ejemplo, InMemoryTestUserDao que guarde los usuarios en un mapa en memoria...
    // Para esto generamos un mock con Mockito, y le pedimos que nos cree el UserServiceImpl inyectando la clase
    // mock-eada:
    @Mock // Le pedimos que nos genere una clase mock de UserDao
    private WorkerDao workerDao;
    @Mock
    private ProfessionWorkerDao professionWorkerDao;
    @Mock
    private UserDao userDao;
    @Mock
    private ImageService imageService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private NeighborhoodWorkerDao neighborhoodWorkerDao;
    @InjectMocks // Le pedimos que cree un UserServiceImpl, y que en el ctor (que toma un UserDao) inyecte un mock.
    private WorkerServiceImpl ws;

    @Before
    public void setUp() {
        // Create and set up the mock Neighborhood object
        mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn(USER_ID);
    }

    @Test
    public void testCreate() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(userDao.createUser(anyString(), any(), anyString(), anyString(), anyLong(), any(), anyBoolean(), any(), anyInt())).thenReturn(mockUser);
        when(workerDao.createWorker(anyLong(), anyString(), anyString(), anyString())).thenReturn(new Worker.Builder()
                .user(mockUser)
                .phoneNumber(PHONE_NUMBER)
                .address(ADDRESS)
                .businessName(BUSINESS_NAME)
                .bio(BIO)
                .build()
        );

        // 2. Ejercitar
        // Pruebo la funcionalidad de usuarios
        Worker newWorker = ws.createWorker(EMAIL, NAME, SURNAME, PASSWORD, IDENTIFICATION, PHONE_NUMBER, ADDRESS, LANGUAGE, new long[]{1}, BUSINESS_NAME);

        // 3. Postcondiciones
        Assert.assertNotNull(newWorker);
        Assert.assertEquals(newWorker.getUser().getUserId(), USER_ID);
        Assert.assertEquals(newWorker.getUser(), mockUser);
        Assert.assertEquals(newWorker.getPhoneNumber(), PHONE_NUMBER);
        Assert.assertEquals(newWorker.getAddress(), ADDRESS);
        Assert.assertEquals(newWorker.getBusinessName(), BUSINESS_NAME);
        Assert.assertEquals(newWorker.getBio(), BIO);

        // Verifico que se haya llamado create del UserDao una vez
        // NUNCA HAGAN ESTO, PORQUE ESTAS PROBANDO EL UserServiceImpl QUE TE IMPORTA CÓMO EL USA EL UserDao
        // Mockito.verify(userDao, times(1)).create(EMAIL, PASSWORD);
    }
    @Test(expected = RuntimeException.class) // "Espero que este test lance y falle con una exception tal"
    public void testCreateAlreadyExists() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(userDao.createUser(anyString(), any(), anyString(), anyString(), anyLong(), any(), anyBoolean(), any(), anyInt())).thenReturn(mockUser);
        when(workerDao.createWorker(eq(mockUser.getUserId()),eq(PHONE_NUMBER),eq(ADDRESS),eq(BUSINESS_NAME))).thenThrow(RuntimeException.class);

        // 2. Ejercitar
        Worker newWorker = ws.createWorker(EMAIL, NAME, SURNAME, PASSWORD, IDENTIFICATION, PHONE_NUMBER, ADDRESS, LANGUAGE, new long[]{1}, BUSINESS_NAME);

        // 3. Postcondiciones
        // (Nada, espero que lo anterior tire exception)
    }


}

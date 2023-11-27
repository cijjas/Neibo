package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodWorkerDao;
import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.WorkerDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
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

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkerServiceImplTest {

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
    private final String IDENTIFICATION = "123456789";
    private User mockUser;
    @Mock
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
    @InjectMocks
    private WorkerServiceImpl ws;

    @Before
    public void setUp() {
        mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn(USER_ID);
    }

    @Test
    public void testCreate() {
        // 1. Preconditions
        when(userDao.createUser(anyString(), any(), anyString(), anyString(), anyLong(), any(), anyBoolean(), any(), anyInt())).thenReturn(mockUser);
        when(workerDao.createWorker(anyLong(), anyString(), anyString(), anyString())).thenReturn(new Worker.Builder()
                .user(mockUser)
                .phoneNumber(PHONE_NUMBER)
                .address(ADDRESS)
                .businessName(BUSINESS_NAME)
                .bio(BIO)
                .build()
        );

        // 2. Exercise
        Worker newWorker = ws.createWorker(EMAIL, NAME, SURNAME, PASSWORD, IDENTIFICATION, PHONE_NUMBER, ADDRESS, LANGUAGE, new Long[]{Integer.toUnsignedLong(1)}, BUSINESS_NAME);

        // 3. Postconditions
        Assert.assertNotNull(newWorker);
        Assert.assertEquals(newWorker.getUser(), mockUser);
        Assert.assertEquals(newWorker.getPhoneNumber(), PHONE_NUMBER);
        Assert.assertEquals(newWorker.getAddress(), ADDRESS);
        Assert.assertEquals(newWorker.getBusinessName(), BUSINESS_NAME);
        Assert.assertEquals(newWorker.getBio(), BIO);

    }

    @Test(expected = RuntimeException.class)
    public void testCreateAlreadyExists() {
        // 1. Preconditions
        when(userDao.createUser(anyString(), any(), anyString(), anyString(), anyLong(), any(), anyBoolean(), any(), anyInt())).thenReturn(mockUser);
        when(workerDao.createWorker(eq(mockUser.getUserId()), eq(PHONE_NUMBER), eq(ADDRESS), eq(BUSINESS_NAME))).thenThrow(RuntimeException.class);

        // 2. Exercise
        Worker newWorker = ws.createWorker(EMAIL, NAME, SURNAME, PASSWORD, IDENTIFICATION, PHONE_NUMBER, ADDRESS, LANGUAGE, new Long[]{1L}, BUSINESS_NAME);

        // 3. Postconditions
    }


}

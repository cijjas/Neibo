package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.NeighborDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.models.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class) // Le decimos a JUnit que corra los tests con el runner de Mockito
public class NeighborServiceImplTest {

    private Neighborhood mockNeighborhood;
    private static final long ID = 1;
    private static final String EMAIL = "pedro@mcpedro.com";
    private static final String NAME = "Pedro";
    private static final String SURNAME = "Pedrovsky";

    private static final String NEIGHBORHOOD_NAME = "Varsovia";

    private static final long NEIGHBORHOOD_ID = 1;


    // private final UserServiceImpl us = new UserServiceImpl(null);
    // Qué usamos como UserDao para el UserServiceImpl? No queremos conectarlo al Postgres de verdad, es una pérdida de
    // tiempo escribir un propio, por ejemplo, InMemoryTestUserDao que guarde los usuarios en un mapa en memoria...
    // Para esto generamos un mock con Mockito, y le pedimos que nos cree el UserServiceImpl inyectando la clase
    // mock-eada:
    @Mock // Le pedimos que nos genere una clase mock de UserDao
    private NeighborDao neighborDao;
    @InjectMocks // Le pedimos que cree un UserServiceImpl, y que en el ctor (que toma un UserDao) inyecte un mock.
    private NeighborServiceImpl ns;

    @Before
    public void setUp() {
        // Create and set up the mock Neighborhood object
        mockNeighborhood = mock(Neighborhood.class);
        when(mockNeighborhood.getName()).thenReturn(NEIGHBORHOOD_NAME);
        when(mockNeighborhood.getNeighborhoodId()).thenReturn(NEIGHBORHOOD_ID);
    }

    @Test
    public void testCreate() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(neighborDao.createNeighbor(anyString(), anyString(), anyString(), anyLong())).thenReturn(new Neighbor.Builder()
                .neighborId(ID)
                .mail(EMAIL)
                .name(NAME)
                .surname(SURNAME)
                .build()
        );

        // 2. Ejercitar
        // Pruebo la funcionalidad de usuarios
        Neighbor newNeighbor = ns.createNeighbor(EMAIL, NAME, SURNAME, mockNeighborhood.getNeighborhoodId());

        // 3. Postcondiciones
        Assert.assertNotNull(newNeighbor);
        Assert.assertEquals(newNeighbor.getNeighborId(), ID);
        Assert.assertEquals(newNeighbor.getMail(), EMAIL);
        Assert.assertEquals(newNeighbor.getName(), NAME);
        Assert.assertEquals(newNeighbor.getSurname(), SURNAME);

        // Verifico que se haya llamado create del UserDao una vez
        // NUNCA HAGAN ESTO, PORQUE ESTAS PROBANDO EL UserServiceImpl QUE TE IMPORTA CÓMO EL USA EL UserDao
        // Mockito.verify(userDao, times(1)).create(EMAIL, PASSWORD);
    }
    @Test(expected = RuntimeException.class) // "Espero que este test lance y falle con una exception tal"
    public void testCreateAlreadyExists() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(neighborDao.createNeighbor(eq(EMAIL), eq(NAME), eq(SURNAME), eq(mockNeighborhood.getNeighborhoodId()))).thenThrow(RuntimeException.class);

        // 2. Ejercitar
        Neighbor newNeighbor = ns.createNeighbor(EMAIL, NAME, SURNAME, mockNeighborhood.getNeighborhoodId());

        // 3. Postcondiciones
        // (Nada, espero que lo anterior tire exception)
    }

    @Test
    public void testFindById() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(neighborDao.findNeighborById(eq(ID))).thenReturn(Optional.of(new Neighbor.Builder()
                .neighborId(ID)
                .mail(EMAIL)
                .name(NAME)
                .surname(SURNAME)
                .build()
        ));

        // 2. Ejercitar
        Optional<Neighbor> newNeighbor = ns.findNeighborById(ID);

        // 3. Postcondiciones
        Assert.assertTrue(newNeighbor.isPresent());
        Assert.assertEquals(ID, newNeighbor.get().getNeighborId());
    }
}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.NeighborDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.models.Neighborhood;
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
public class NeighborhoodServiceImplTest {

    private static final long ID = 1;
    private static final String NAME = "Varsovia";


    // private final UserServiceImpl us = new UserServiceImpl(null);
    // Qué usamos como UserDao para el UserServiceImpl? No queremos conectarlo al Postgres de verdad, es una pérdida de
    // tiempo escribir un propio, por ejemplo, InMemoryTestUserDao que guarde los usuarios en un mapa en memoria...
    // Para esto generamos un mock con Mockito, y le pedimos que nos cree el UserServiceImpl inyectando la clase
    // mock-eada:
    @Mock // Le pedimos que nos genere una clase mock de UserDao
    private NeighborhoodDao neighborhoodDao;
    @InjectMocks // Le pedimos que cree un UserServiceImpl, y que en el ctor (que toma un UserDao) inyecte un mock.
    private NeighborhoodServiceImpl ns;
    @Test
    public void testCreate() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(neighborhoodDao.createNeighborhood(anyString())).thenReturn(new Neighborhood.Builder()
                .neighborhoodId(ID)
                .name(NAME)
                .build()
        );

        // 2. Ejercitar
        // Pruebo la funcionalidad de usuarios
        Neighborhood newNeighborhood = ns.createNeighborhood(NAME);

        // 3. Postcondiciones
        Assert.assertNotNull(newNeighborhood);
        Assert.assertEquals(newNeighborhood.getNeighborhoodId(), ID);
        Assert.assertEquals(newNeighborhood.getName(), NAME);

        // Verifico que se haya llamado create del UserDao una vez
        // NUNCA HAGAN ESTO, PORQUE ESTAS PROBANDO EL UserServiceImpl QUE TE IMPORTA CÓMO EL USA EL UserDao
        // Mockito.verify(userDao, times(1)).create(EMAIL, PASSWORD);
    }
    @Test(expected = RuntimeException.class) // "Espero que este test lance y falle con una exception tal"
    public void testCreateAlreadyExists() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(neighborhoodDao.createNeighborhood(eq(NAME))).thenThrow(RuntimeException.class);

        // 2. Ejercitar
        Neighborhood newNeighborhood = ns.createNeighborhood(NAME);

        // 3. Postcondiciones
        // (Nada, espero que lo anterior tire exception)
    }

    @Test
    public void testFindById() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(neighborhoodDao.findNeighborhoodById(eq(ID))).thenReturn(Optional.of(new Neighborhood.Builder()
                .neighborhoodId(ID)
                .name(NAME)
                .build()
        ));

        // 2. Ejercitar
        Optional<Neighborhood> newNeighborhood = ns.findNeighborhoodById(ID);

        // 3. Postcondiciones
        Assert.assertTrue(newNeighborhood.isPresent());
        Assert.assertEquals(ID, newNeighborhood.get().getNeighborhoodId());
    }
}

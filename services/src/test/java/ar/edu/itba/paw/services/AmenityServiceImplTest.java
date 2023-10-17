package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.models.Amenity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AmenityServiceImplTest {
    private static final long ID = 1;
    private static final String NAME = "Pileta";
    private static final String DESCRIPTION = "Pileta de natación";

    private static final long NEIGHBORHOOD_ID = 1;


    // private final UserServiceImpl us = new UserServiceImpl(null);
    // Qué usamos como UserDao para el UserServiceImpl? No queremos conectarlo al Postgres de verdad, es una pérdida de
    // tiempo escribir un propio, por ejemplo, InMemoryTestUserDao que guarde los usuarios en un mapa en memoria...
    // Para esto generamos un mock con Mockito, y le pedimos que nos cree el UserServiceImpl inyectando la clase
    // mock-eada:
    @Mock // Le pedimos que nos genere una clase mock de UserDao
    private AmenityDao amenityDao;
    @InjectMocks // Le pedimos que cree un UserServiceImpl, y que en el ctor (que toma un UserDao) inyecte un mock.
    private AmenityServiceImpl as;
    /*@Test
    public void testCreate() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(amenityDao.createAmenity(anyString(),anyString(),any(),anyLong())).thenReturn(new Amenity.Builder()
                .amenityId(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .neighborhoodId(NEIGHBORHOOD_ID)
                .build()
        );

        // 2. Ejercitar
        // Pruebo la funcionalidad de usuarios
        Amenity newAmenity = as.createAmenity(NAME,DESCRIPTION,null,NEIGHBORHOOD_ID);

        // 3. Postcondiciones
        Assert.assertNotNull(newAmenity);
        Assert.assertEquals(newAmenity.getNeighborhoodId(), ID);
        Assert.assertEquals(newAmenity.getName(), NAME);
        Assert.assertEquals(newAmenity.getDescription(), DESCRIPTION);
        Assert.assertEquals(newAmenity.getNeighborhoodId(), NEIGHBORHOOD_ID);


        // Verifico que se haya llamado create del UserDao una vez
        // NUNCA HAGAN ESTO, PORQUE ESTAS PROBANDO EL UserServiceImpl QUE TE IMPORTA CÓMO EL USA EL UserDao
        // Mockito.verify(userDao, times(1)).create(EMAIL, PASSWORD);
    }
    @Test(expected = RuntimeException.class) // "Espero que este test lance y falle con una exception tal"
    public void testCreateAlreadyExists() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(amenityDao.createAmenity(eq(NAME),eq(DESCRIPTION),any(),eq(NEIGHBORHOOD_ID))).thenThrow(RuntimeException.class);

        // 2. Ejercitar
        Amenity newAmenity = as.createAmenity(NAME,DESCRIPTION,null,NEIGHBORHOOD_ID);

        // 3. Postcondiciones
        // (Nada, espero que lo anterior tire exception)
    }*/

    @Test
    public void testFindById() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(amenityDao.findAmenityById(eq(ID))).thenReturn(Optional.of(new Amenity.Builder()
                .amenityId(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .neighborhoodId(NEIGHBORHOOD_ID)
                .build()
        ));

        // 2. Ejercitar
        Optional<Amenity> newAmenity = as.findAmenityById(ID);

        // 3. Postcondiciones
        Assert.assertTrue(newAmenity.isPresent());
        Assert.assertEquals(ID, newAmenity.get().getNeighborhoodId());
    }
}

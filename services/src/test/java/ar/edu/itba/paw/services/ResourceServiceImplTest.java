package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.ResourceDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.models.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class) // Le decimos a JUnit que corra los tests con el runner de Mockito
public class ResourceServiceImplTest {

    MultipartFile mockImageFile;
    private static final long ID = 1;
    private static final String DESCRIPTION = "Varsovia de noche";
    private static final long IMAGE_ID = 1;
    private static final String TITLE = "Varsovia";
    private static final long NEIGHBORHOOD_ID = 1;


    // private final UserServiceImpl us = new UserServiceImpl(null);
    // Qué usamos como UserDao para el UserServiceImpl? No queremos conectarlo al Postgres de verdad, es una pérdida de
    // tiempo escribir un propio, por ejemplo, InMemoryTestUserDao que guarde los usuarios en un mapa en memoria...
    // Para esto generamos un mock con Mockito, y le pedimos que nos cree el UserServiceImpl inyectando la clase
    // mock-eada:
    @Mock // Le pedimos que nos genere una clase mock de UserDao
    private ResourceDao resourceDao;
    @Mock
    private ImageService imageService;
    @InjectMocks // Le pedimos que cree un UserServiceImpl, y que en el ctor (que toma un UserDao) inyecte un mock.
    private ResourceServiceImpl rs;
    @Test
    public void testCreate() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(resourceDao.createResource(anyLong(),anyString(),anyString(),anyLong())).thenReturn(new Resource.Builder()
                .resourceId(ID)
                .description(DESCRIPTION)
                .imageId(IMAGE_ID)
                .title(TITLE)
                .neighborhoodId(NEIGHBORHOOD_ID)
                .build()
        );

        // 2. Ejercitar
        // Pruebo la funcionalidad de usuarios
        Resource newResource = rs.createResource(NEIGHBORHOOD_ID,TITLE,DESCRIPTION,mockImageFile);

        // 3. Postcondiciones
        Assert.assertNotNull(newResource);
        Assert.assertEquals(newResource.getResourceId(), ID);
        Assert.assertEquals(newResource.getDescription(), DESCRIPTION);
        Assert.assertEquals(newResource.getImageId(), IMAGE_ID);
        Assert.assertEquals(newResource.getTitle(), TITLE);
        Assert.assertEquals(newResource.getNeighborhoodId(), NEIGHBORHOOD_ID);

        // Verifico que se haya llamado create del UserDao una vez
        // NUNCA HAGAN ESTO, PORQUE ESTAS PROBANDO EL UserServiceImpl QUE TE IMPORTA CÓMO EL USA EL UserDao
        // Mockito.verify(userDao, times(1)).create(EMAIL, PASSWORD);
    }

}

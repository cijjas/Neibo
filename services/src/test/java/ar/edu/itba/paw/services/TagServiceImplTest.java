package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.models.Tag;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TagServiceImplTest {

    private static final long ID = 1;
    private static final String TAG = "Varsovia";
    private static final long POST_ID = 1;



    // private final UserServiceImpl us = new UserServiceImpl(null);
    // Qué usamos como UserDao para el UserServiceImpl? No queremos conectarlo al Postgres de verdad, es una pérdida de
    // tiempo escribir un propio, por ejemplo, InMemoryTestUserDao que guarde los usuarios en un mapa en memoria...
    // Para esto generamos un mock con Mockito, y le pedimos que nos cree el UserServiceImpl inyectando la clase
    // mock-eada:
    @Mock // Le pedimos que nos genere una clase mock de UserDao
    private TagDao tagDao;
    @InjectMocks // Le pedimos que cree un UserServiceImpl, y que en el ctor (que toma un UserDao) inyecte un mock.
    private TagServiceImpl ts;
    @Test
    public void testCreate() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(tagDao.createTag(anyString())).thenReturn(new Tag.Builder()
                .tagId(ID)
                .tag(TAG)
                .build()
        );

        // 2. Ejercitar
        // Pruebo la funcionalidad de usuarios
        Tag newTag = ts.createTag(TAG);

        // 3. Postcondiciones
        Assert.assertNotNull(newTag);
        Assert.assertEquals(newTag.getTagId(), ID);
        Assert.assertEquals(newTag.getTag(), TAG);

        // Verifico que se haya llamado create del UserDao una vez
        // NUNCA HAGAN ESTO, PORQUE ESTAS PROBANDO EL UserServiceImpl QUE TE IMPORTA CÓMO EL USA EL UserDao
        // Mockito.verify(userDao, times(1)).create(EMAIL, PASSWORD);
    }
    @Test(expected = RuntimeException.class) // "Espero que este test lance y falle con una exception tal"
    public void testCreateAlreadyExists() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(tagDao.createTag(eq(TAG))).thenThrow(RuntimeException.class);

        // 2. Ejercitar
        Tag newTag = ts.createTag(TAG);

        // 3. Postcondiciones
        // (Nada, espero que lo anterior tire exception)
    }

    /*@Test
    public void testFindById() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        List<Tag> tagsList = Collections.singletonList(new Tag.Builder()
                .tagId(ID)
                .tag(TAG)
                .build());

        when(tagDao.findTagsByPostId(eq(POST_ID))).thenReturn(Optional.of(tagsList));

        // 2. Ejercitar
        Optional<List<Tag>> newTagList = ts.findTagsByPostId(POST_ID);

        // 3. Postcondiciones
        Assert.assertTrue(newTagList.isPresent());
        Assert.assertEquals(ID, newTagList.get().get(0).getTagId());
    }*/
}

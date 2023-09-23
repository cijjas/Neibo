/*
package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.User;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class) // Le decimos a JUnit que corra los tests con el runner de Mockito
public class PostServiceImplTest {

    private User mockUser;
    private Channel mockChannel;
    private static final long ID = 1;
    private static final String TITLE = "LOBO RONDANDO";
    private static final String DESCRIPTION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final Date DATE = new Date(2023, 9, 11);
    private static final long NEIGHBOR_ID = 1;
    private static final long CHANNEL_ID = 1;
    private static final String  IMAGE_FILE = "image.jpg";


    // private final UserServiceImpl us = new UserServiceImpl(null);
    // Qué usamos como UserDao para el UserServiceImpl? No queremos conectarlo al Postgres de verdad, es una pérdida de
    // tiempo escribir un propio, por ejemplo, InMemoryTestUserDao que guarde los usuarios en un mapa en memoria...
    // Para esto generamos un mock con Mockito, y le pedimos que nos cree el UserServiceImpl inyectando la clase
    // mock-eada:
    @Mock
    private PostDao postDao;
    @InjectMocks // Le pedimos que cree un UserServiceImpl, y que en el ctor (que toma un UserDao) inyecte un mock.
    private PostServiceImpl ps;

    @Before
    public void setUp() {
        // Create and set up the mock Neighborhood object
        mockUser = mock(User.class);
        when(mockUser.getNeighborId()).thenReturn(NEIGHBOR_ID);
        mockChannel = mock(Channel.class);
        when(mockChannel.getChannelId()).thenReturn(CHANNEL_ID);
    }
*/
/*
    @Test
    public void testCreate() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(postDao.create(anyString(), anyString(), anyLong(), anyLong(), anyString())).thenReturn(new Post.Builder()
                .postId(ID)
                .title(TITLE)
                .description(DESCRIPTION)
                .date(DATE)
                .neighbor(mockNeighbor)
                .channel(mockChannel)
                .imageFile(IMAGE_FILE)
                .build()
        );

        // 2. Ejercitar
        // Pruebo la funcionalidad de usuarios
        Post newPost = ps.createPost(TITLE, DESCRIPTION, mockNeighbor.getNeighborId(), mockChannel.getChannelId(), IMAGE_FILE);

        // 3. Postcondiciones
        Assert.assertNotNull(newPost);
        Assert.assertEquals(newPost.getPostId(), ID);
        Assert.assertEquals(newPost.getTitle(), TITLE);
        Assert.assertEquals(newPost.getDescription(), DESCRIPTION);
        Assert.assertEquals(newPost.getDate(), DATE);
        Assert.assertEquals(newPost.getNeighbor(), mockNeighbor);
        Assert.assertEquals(newPost.getChannel(), mockChannel);
        Assert.assertEquals(newPost.getImageFile(), IMAGE_FILE);
        // Verifico que se haya llamado create del UserDao una vez
        // NUNCA HAGAN ESTO, PORQUE ESTAS PROBANDO EL UserServiceImpl QUE TE IMPORTA CÓMO EL USA EL UserDao
        // Mockito.verify(userDao, times(1)).create(EMAIL, PASSWORD);
    }
    @Test(expected = RuntimeException.class) // "Espero que este test lance y falle con una exception tal"
    public void testCreateAlreadyExists() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(postDao.create(eq(TITLE), eq(DESCRIPTION), eq(mockNeighbor.getNeighborId()), eq(mockChannel.getChannelId()), IMAGE_FILE)).thenThrow(RuntimeException.class);

        // 2. Ejercitar
        Post newPost = ps.createPost(TITLE, DESCRIPTION, mockNeighbor.getNeighborId(), mockChannel.getChannelId(), IMAGE_FILE);

        // 3. Postcondiciones
        // (Nada, espero que lo anterior tire exception)
    }

    @Test
    public void testFindById() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(postDao.findPostById(eq(ID))).thenReturn(Optional.of(new Post.Builder()
                .postId(ID)
                .title(TITLE)
                .description(DESCRIPTION)
                .date(DATE)
                .neighbor(mockNeighbor)
                .channel(mockChannel)
                .imageFile(IMAGE_FILE)
                .build()
        ));

        // 2. Ejercitar
        Optional<Post> newPost = ps.findPostById(ID);

        // 3. Postcondiciones
        Assert.assertTrue(newPost.isPresent());
        Assert.assertEquals(ID, newPost.get().getPostId());
    }

 *//*

}
*/

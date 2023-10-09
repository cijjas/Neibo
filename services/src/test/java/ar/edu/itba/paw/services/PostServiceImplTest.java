package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Tag;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Post;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class) // Le decimos a JUnit que corra los tests con el runner de Mockito
public class PostServiceImplTest {

    private User mockUser;
    private Channel mockChannel;
    private List mockTagList;
    private Tag mockTag;
    private static final long ID = 1;
    private static final String TITLE = "LOBO RONDANDO";
    private static final String DESCRIPTION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final Date DATE = new Date(2023, 9, 11);
    private static final long USER_ID = 1;
    private static final long CHANNEL_ID = 1;
    private static final long  POST_PICTURE_ID = 1;
    private static final int LIKES = 500;
    private static final MultipartFile IMAGE_FILE = null;


    // private final UserServiceImpl us = new UserServiceImpl(null);
    // Qué usamos como UserDao para el UserServiceImpl? No queremos conectarlo al Postgres de verdad, es una pérdida de
    // tiempo escribir un propio, por ejemplo, InMemoryTestUserDao que guarde los usuarios en un mapa en memoria...
    // Para esto generamos un mock con Mockito, y le pedimos que nos cree el UserServiceImpl inyectando la clase
    // mock-eada:
    @Mock
    private PostDao postDao;
    @Mock
    private ChannelDao channelDao;
    @Mock
    private UserService userService;
    @Mock
    private EmailService emailService;
    @Mock
    private TagService tagService;
    @Mock
    private ImageService imageService;
    @InjectMocks // Le pedimos que cree un UserServiceImpl, y que en el ctor (que toma un UserDao) inyecte un mock.
    private PostServiceImpl ps;

    @Before
    public void setUp() {
        // Create and set up the mock Neighborhood object
        mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn(USER_ID);
        mockChannel = mock(Channel.class);
        when(mockChannel.getChannelId()).thenReturn(CHANNEL_ID);
        mockTagList = mock(List.class);
        when(mockTagList.get(0)).thenReturn(mockTag);
        when(mockTagList.size()).thenReturn(1);
        when(mockTagList.toString()).thenReturn("tag1");
        mockTag = mock(Tag.class);
        when(mockTag.getTagId()).thenReturn(1L);
    }


    @Test
    public void testCreate() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(postDao.createPost(anyString(), anyString(), anyLong(), anyLong(), anyLong())).thenReturn(new Post.Builder()
                .postId(ID)
                .title(TITLE)
                .description(DESCRIPTION)
                .date(DATE)
                .user(mockUser)
                .channel(mockChannel)
                .postPictureId(POST_PICTURE_ID)
                .likes(LIKES)
                .build()
        );

        // 2. Ejercitar
        // Pruebo la funcionalidad de usuarios
        Post newPost = ps.createPost(TITLE, DESCRIPTION, mockUser.getUserId(), mockChannel.getChannelId(), null, IMAGE_FILE);

        // 3. Postcondiciones
        Assert.assertNotNull(newPost);
        Assert.assertEquals(newPost.getPostId(), ID);
        Assert.assertEquals(newPost.getTitle(), TITLE);
        Assert.assertEquals(newPost.getDescription(), DESCRIPTION);
        Assert.assertEquals(newPost.getDate(), DATE);
        Assert.assertEquals(newPost.getUser(), mockUser);
        Assert.assertEquals(newPost.getChannel(), mockChannel);
        //Assert.assertEquals(newPost.getTags().get(0), mockTag);
        Assert.assertEquals(newPost.getPostPictureId(), POST_PICTURE_ID);
        // Verifico que se haya llamado create del UserDao una vez
        // NUNCA HAGAN ESTO, PORQUE ESTAS PROBANDO EL UserServiceImpl QUE TE IMPORTA CÓMO EL USA EL UserDao
        // Mockito.verify(userDao, times(1)).create(EMAIL, PASSWORD);
    }
    @Test(expected = RuntimeException.class) // "Espero que este test lance y falle con una exception tal"
    public void testCreateAlreadyExists() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(postDao.createPost(eq(TITLE), eq(DESCRIPTION), eq(mockUser.getUserId()), eq(mockChannel.getChannelId()), eq(POST_PICTURE_ID))).thenThrow(RuntimeException.class);

        // 2. Ejercitar
        Post newPost = ps.createPost(TITLE, DESCRIPTION, mockUser.getUserId(), mockChannel.getChannelId(), mockTagList.toString(), IMAGE_FILE);

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
                .user(mockUser)
                .channel(mockChannel)
                .postPictureId(POST_PICTURE_ID)
                .tags(mockTagList)
                .likes(LIKES)
                .build()
        ));

        // 2. Ejercitar
        Optional<Post> newPost = ps.findPostById(ID);

        // 3. Postcondiciones
        Assert.assertTrue(newPost.isPresent());
        Assert.assertEquals(ID, newPost.get().getPostId());
    }

}


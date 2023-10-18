package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
public class CommentServiceImplTest {

    private User mockUser;
    private Post mockPost;
    private static final long ID = 1;
    private static final String COMMENT = "aguante pescado rabioso";
    private static final Date DATE = new Date();
    private static final long POST_ID = 1;
    private static final long NEIGHBOR_ID = 1;
    private static final String NEIGHBOR_NAME = "Pepe";
    private static final String POST_TITLE = "Pescado rabioso";
    private static final String NEIGHBOR_MAIL = "neighbor@mail.com";

    // private final UserServiceImpl us = new UserServiceImpl(null);
    // Qué usamos como UserDao para el UserServiceImpl? No queremos conectarlo al Postgres de verdad, es una pérdida de
    // tiempo escribir un propio, por ejemplo, InMemoryTestUserDao que guarde los usuarios en un mapa en memoria...
    // Para esto generamos un mock con Mockito, y le pedimos que nos cree el UserServiceImpl inyectando la clase
    // mock-eada:
    @Mock // Le pedimos que nos genere una clase mock de UserDao
    private CommentDao commentDao;
    @Mock
    private EmailService emailService;
    @Mock
    private PostService postService;
    @Mock
    private UserService userService;
    @InjectMocks // Le pedimos que cree un UserServiceImpl, y que en el ctor (que toma un UserDao) inyecte un mock.
    private CommentServiceImpl cs;
    @Before
    public void setUp() {
        // Create and set up the mock Neighborhood object
        mockUser = mock(User.class);
        mockPost = mock(Post.class);
        when(postService.findPostById(POST_ID)).thenReturn(Optional.of(mockPost));
        when(mockPost.getUser()).thenReturn(mockUser);
        when(mockUser.getName()).thenReturn(NEIGHBOR_NAME);
        when(mockUser.getMail()).thenReturn(NEIGHBOR_MAIL);
        when(mockPost.getTitle()).thenReturn(POST_TITLE);
    }
    @Test
    public void testCreate() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(commentDao.createComment(anyString(),anyLong(),anyLong())).thenReturn(new Comment.Builder()
                .commentId(ID)
                .comment(COMMENT)
                .date(DATE)
                .user(mockUser)
                .postId(POST_ID)
                .build()
        );

        // 2. Ejercitar
        // Pruebo la funcionalidad de usuarios
        Comment newComment = cs.createComment(COMMENT, ID, POST_ID);

        // 3. Postcondiciones
        Assert.assertNotNull(newComment);
        Assert.assertEquals(newComment.getCommentId(), ID);
        Assert.assertEquals(newComment.getComment(), COMMENT);
        Assert.assertEquals(newComment.getDate(), DATE);
        Assert.assertEquals(newComment.getUser(), mockUser);
        Assert.assertEquals(newComment.getPostId(), POST_ID);

        // Verifico que se haya llamado create del UserDao una vez
        // NUNCA HAGAN ESTO, PORQUE ESTAS PROBANDO EL UserServiceImpl QUE TE IMPORTA CÓMO EL USA EL UserDao
        // Mockito.verify(userDao, times(1)).create(EMAIL, PASSWORD);
    }
    @Test(expected = RuntimeException.class) // "Espero que este test lance y falle con una exception tal"
    public void testCreateAlreadyExists() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(commentDao.createComment(eq(COMMENT),eq(NEIGHBOR_ID),eq(POST_ID))).thenThrow(RuntimeException.class);

        // 2. Ejercitar
        Comment newComment = cs.createComment(COMMENT, NEIGHBOR_ID, POST_ID);

        // 3. Postcondiciones
        // (Nada, espero que lo anterior tire exception)
    }

}

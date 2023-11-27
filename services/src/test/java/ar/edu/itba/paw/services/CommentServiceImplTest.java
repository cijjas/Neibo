package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Comment;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.models.Entities.User;
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

    private static final long ID = 1;
    private static final String COMMENT = "aguante pescado rabioso";
    private static final Date DATE = new Date();
    private static final long POST_ID = 1;
    private static final long NEIGHBOR_ID = 1;
    private static final String NEIGHBOR_NAME = "Pepe";
    private static final String POST_TITLE = "Pescado rabioso";
    private static final String NEIGHBOR_MAIL = "neighbor@mail.com";
    private User mockUser;
    private Post mockPost;
    @Mock
    private CommentDao commentDao;
    @Mock
    private EmailService emailService;
    @Mock
    private PostService postService;
    @Mock
    private UserService userService;
    @InjectMocks
    private CommentServiceImpl cs;

    @Before
    public void setUp() {
        mockUser = mock(User.class);
        mockPost = mock(Post.class);
        when(postService.findPostById(POST_ID)).thenReturn(Optional.of(mockPost));
    }

    @Test
    public void testCreate() {
        /*// 1. Preconditions
        when(commentDao.createComment(anyString(), anyLong(), anyLong())).thenReturn(new Comment.Builder()
                .commentId(ID)
                .comment(COMMENT)
                .date(DATE)
                .user(mockUser)
                .postId(POST_ID)
                .build()
        );

        // 2. Exercise
        Comment newComment = cs.createComment(COMMENT, ID, POST_ID);

        // 3. Postconditions
        Assert.assertNotNull(newComment);
        Assert.assertEquals(newComment.getCommentId().longValue(), ID);
        Assert.assertEquals(newComment.getComment(), COMMENT);
        Assert.assertEquals(newComment.getDate(), DATE);
        Assert.assertEquals(newComment.getUser(), mockUser);
        Assert.assertEquals(newComment.getPostId().longValue(), POST_ID);*/

    }

    @Test(expected = RuntimeException.class)
    public void testCreateAlreadyExists() {
        // 1. Preconditions
        when(commentDao.createComment(eq(COMMENT), eq(NEIGHBOR_ID), eq(POST_ID))).thenThrow(RuntimeException.class);

        // 2. Exercise
        Comment newComment = cs.createComment(COMMENT, NEIGHBOR_ID, POST_ID);

        // 3. Postconditions
    }

}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceImplTest {

    private static final long ID = 1;
    private static final String TITLE = "LOBO RONDANDO";
    private static final String DESCRIPTION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final Date DATE = new Timestamp(System.currentTimeMillis());
    private static final long USER_ID = 1;
    private static final long CHANNEL_ID = 1;
    private static final long POST_PICTURE_ID = 0;
    private static final int LIKES = 500;
    private User mockUser;
    private Channel mockChannel;
    private List mockTagList;
    private Image mockImage;
    private Tag mockTag;
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
    @InjectMocks
    private PostServiceImpl ps;

    @Before
    public void setUp() {
        mockUser = mock(User.class);
        mockChannel = mock(Channel.class);
        mockTagList = mock(List.class);
        mockTag = mock(Tag.class);
        mockImage = mock(Image.class);
    }

    @Test
    public void testCreate() {
        // 1. Preconditions
        when(postDao.createPost(anyString(), anyString(), anyLong(), anyLong(), anyLong())).thenReturn(new Post.Builder()
                .postId(ID)
                .title(TITLE)
                .description(DESCRIPTION)
                .user(mockUser)
                .channel(mockChannel)
                .postPicture(mockImage)
                .build()
        );

        // 2. Exercise
        Post newPost = ps.createPost(TITLE, DESCRIPTION, mockUser.getUserId(), mockChannel.getChannelId(), null, null);

        // 3. Postconditions
        Assert.assertNotNull(newPost);
        Assert.assertEquals(ID, newPost.getPostId().longValue());
        Assert.assertEquals(TITLE, newPost.getTitle());
        Assert.assertEquals(DESCRIPTION,newPost.getDescription());
        Assert.assertEquals(mockUser, newPost.getUser());
        Assert.assertEquals(mockChannel, newPost.getChannel());
    }

    @Test(expected = RuntimeException.class)
    public void testCreateAlreadyExists() {
        // 1. Preconditions
        when(postDao.createPost(eq(TITLE), eq(DESCRIPTION), eq(mockUser.getUserId()), eq(mockChannel.getChannelId()), eq(POST_PICTURE_ID))).thenThrow(RuntimeException.class);

        // 2. Exercise
        Post newPost = ps.createPost(TITLE, DESCRIPTION, mockUser.getUserId(), mockChannel.getChannelId(), mockTagList.toString(), null);

        // 3. Postconditions
    }

}


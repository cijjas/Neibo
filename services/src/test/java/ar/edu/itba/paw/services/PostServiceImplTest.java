package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.Tag;
import ar.edu.itba.paw.models.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceImplTest {

    private static final long ID = 1;
    private static final String TITLE = "LOBO RONDANDO";
    private static final String DESCRIPTION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final Date DATE = new Date(2023, 9, 11);
    private static final long USER_ID = 1;
    private static final long CHANNEL_ID = 1;
    private static final long POST_PICTURE_ID = 0;
    private static final int LIKES = 500;
    private User mockUser;
    private Channel mockChannel;
    private Channel mockWorkerChannel;
    private List mockTagList;
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
        when(mockUser.getUserId()).thenReturn(USER_ID);
        mockChannel = mock(Channel.class);
        when(mockChannel.getChannelId()).thenReturn(CHANNEL_ID);
        mockTagList = mock(List.class);
        mockTag = mock(Tag.class);

        mockWorkerChannel = mock(Channel.class);

    }


    @Test
    public void testCreate() {
        // 1. Preconditions
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

        // 2. Exercise
        Post newPost = ps.createPost(TITLE, DESCRIPTION, mockUser.getUserId(), mockChannel.getChannelId(), null, null);

        // 3. Postconditions
        Assert.assertNotNull(newPost);
        Assert.assertEquals(newPost.getPostId(), ID);
        Assert.assertEquals(newPost.getTitle(), TITLE);
        Assert.assertEquals(newPost.getDescription(), DESCRIPTION);
        Assert.assertEquals(newPost.getDate(), DATE);
        Assert.assertEquals(newPost.getUser(), mockUser);
        Assert.assertEquals(newPost.getChannel(), mockChannel);
        Assert.assertEquals(newPost.getPostPictureId(), POST_PICTURE_ID);

    }

    @Test(expected = RuntimeException.class)
    public void testCreateAlreadyExists() {
        // 1. Preconditions
        when(postDao.createPost(eq(TITLE), eq(DESCRIPTION), eq(mockUser.getUserId()), eq(mockChannel.getChannelId()), eq(POST_PICTURE_ID))).thenThrow(RuntimeException.class);

        // 2. Exercise
        Post newPost = ps.createPost(TITLE, DESCRIPTION, mockUser.getUserId(), mockChannel.getChannelId(), mockTagList.toString(), null);

        // 3. Postconditions
    }

//    @Test
//    public void testCreateWorkerPost(){
//        // 1. Preconditions
//        when(postDao.createPost(anyString(), anyString(), anyLong(), eq(BaseChannel.WORKERS.getId()), anyLong())).thenReturn(new Post.Builder()
//                .postId(ID)
//                .title(TITLE)
//                .description(DESCRIPTION)
//                .date(DATE)
//                .user(mockUser)
//                .channel(mockWorkerChannel)
//                .postPictureId(POST_PICTURE_ID)
//                .likes(LIKES)
//                .build()
//        );
//
//        // 2. Exercise
//        Post newWorkerPost = ps.createWorkerPost(TITLE, DESCRIPTION, mockUser.getUserId(), null);
//
//        // 3. Postconditions
//        Assert.assertNotNull(newWorkerPost);
//        Assert.assertEquals(newWorkerPost.getPostId(), ID);
//        Assert.assertEquals(newWorkerPost.getTitle(), TITLE);
//        Assert.assertEquals(newWorkerPost.getDescription(), DESCRIPTION);
//        Assert.assertEquals(newWorkerPost.getDate(), DATE);
//        Assert.assertEquals(newWorkerPost.getUser(), mockUser);
//        Assert.assertEquals(newWorkerPost.getChannel(), mockWorkerChannel);
//        Assert.assertEquals(newWorkerPost.getChannel().getChannelId(), BaseChannel.WORKERS.getId());
//        Assert.assertEquals(newWorkerPost.getPostPictureId(), POST_PICTURE_ID);
//
//    }

}


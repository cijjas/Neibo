package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.BaseChannel;
import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.models.Entities.Tag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceImplTest {

    @Mock
    private PostDao postDao;
    @Mock
    private CategorizationDao categorizationDao;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    public void create_tags() {
        // Pre Conditions
        String title = "New Post";
        String description = "Description of the post";
        long userId = 1L;
        long channelId = BaseChannel.FEED.getId();
        List<Long> tagIds = Arrays.asList(10L, 20L);
        long imageId = 123L;
        long neighborhoodId = 456L;

        Post mockPost = new Post.Builder().build();
        mockPost.setPostId(1L);

        when(postDao.createPost(title, description, userId, channelId, imageId)).thenReturn(mockPost);

        when(categorizationDao.findCategorization(10L, mockPost.getPostId())).thenReturn(Optional.empty());
        when(categorizationDao.findCategorization(20L, mockPost.getPostId())).thenReturn(Optional.empty());

        // Exercise
        Post createdPost = postService.createPost(neighborhoodId, userId, title, description, channelId, tagIds, imageId);

        // Validations & Post Conditions
        verify(postDao, times(1)).createPost(title, description, userId, channelId, imageId);
        verify(categorizationDao, times(1)).findCategorization(10L, mockPost.getPostId());
        verify(categorizationDao, times(1)).findCategorization(20L, mockPost.getPostId());
        verify(categorizationDao, times(1)).createCategorization(10L, mockPost.getPostId());
        verify(categorizationDao, times(1)).createCategorization(20L, mockPost.getPostId());
        verify(emailService, times(0)).sendBatchAnnouncementMail(mockPost, neighborhoodId);

        assertNotNull(createdPost);
    }

    @Test
    public void create_noTags() {
        // Pre Conditions
        String title = "New Post";
        String description = "Description of the post";
        long userId = 1L;
        long channelId = 2L;
        List<Long> tagIds = null;
        long imageId = 123L;
        long neighborhoodId = 456L;

        Post mockPost = new Post.Builder().build();
        mockPost.setPostId(1L);

        when(postDao.createPost(title, description, userId, channelId, imageId)).thenReturn(mockPost);

        // Exercise
        Post createdPost = postService.createPost(neighborhoodId, userId, title, description, channelId, tagIds, imageId);

        // Validations & Post Conditions
        verify(postDao, times(1)).createPost(title, description, userId, channelId, imageId);
        verify(categorizationDao, times(0)).findCategorization(anyLong(), anyLong());
        verify(categorizationDao, times(0)).createCategorization(anyLong(), anyLong());
        verify(emailService, times(0)).sendBatchAnnouncementMail(mockPost, neighborhoodId);

        assertNotNull(createdPost);
    }

    @Test
    public void create_inAnnouncements() {
        // Pre Conditions
        String title = "New Post";
        String description = "Description of the post";
        long userId = 1L;
        long channelId = BaseChannel.ANNOUNCEMENTS.getId();
        List<Long> tagIds = Arrays.asList(10L, 20L);
        long imageId = 123L;
        long neighborhoodId = 456L;

        Post mockPost = new Post.Builder().build();
        mockPost.setPostId(1L);

        when(postDao.createPost(title, description, userId, channelId, imageId)).thenReturn(mockPost);
        when(categorizationDao.findCategorization(10L, mockPost.getPostId())).thenReturn(Optional.empty());
        when(categorizationDao.findCategorization(20L, mockPost.getPostId())).thenReturn(Optional.empty());

        // Exercise
        Post createdPost = postService.createPost(neighborhoodId, userId, title, description, channelId, tagIds, imageId);

        // Validations & Post Conditions
        verify(postDao, times(1)).createPost(title, description, userId, channelId, imageId);
        verify(categorizationDao, times(1)).findCategorization(10L, mockPost.getPostId());
        verify(categorizationDao, times(1)).findCategorization(20L, mockPost.getPostId());
        verify(categorizationDao, times(1)).createCategorization(10L, mockPost.getPostId());
        verify(categorizationDao, times(1)).createCategorization(20L, mockPost.getPostId());
        verify(emailService, times(1)).sendBatchAnnouncementMail(mockPost, neighborhoodId);  // Ensure email is sent

        assertNotNull(createdPost);
    }

    @Test
    public void delete_withTags() {
        // Pre Conditions
        long postId = 1L;
        long neighborhoodId = 2L;

        Tag tag1 = new Tag.Builder().build();
        tag1.setTagId(101L);
        Tag tag2 = new Tag.Builder().build();
        tag2.setTagId(102L);

        Set<Tag> tags = new HashSet<>();
        tags.add(tag1);
        tags.add(tag2);

        Post post = new Post.Builder().build();
        post.setTags(tags);

        when(postDao.findPost(postId)).thenReturn(Optional.of(post));
        when(postDao.deletePost(postId)).thenReturn(true);

        // Exercise
        boolean result = postService.deletePost(postId);

        // Validations & Post Conditions
        assertTrue(result);

        verify(postDao, times(1)).findPost(postId);
        verify(categorizationDao, times(1)).deleteCategorization(101L, postId);
        verify(categorizationDao, times(1)).deleteCategorization(102L, postId);
        verify(postDao, times(1)).deletePost(postId);
    }

    @Test
    public void delete_noTags() {
        // Pre Conditions
        long postId = 1L;
        long neighborhoodId = 2L;

        Post post = new Post.Builder().build();
        post.setTags(Collections.emptySet());

        when(postDao.findPost(postId)).thenReturn(Optional.of(post));
        when(postDao.deletePost(postId)).thenReturn(true);

        // Exercise
        boolean result = postService.deletePost(postId);

        // Validations & Post Conditions
        assertTrue(result);

        verify(postDao, times(1)).findPost(postId);
        verify(categorizationDao, never()).deleteCategorization(anyLong(), anyLong());
        verify(postDao, times(1)).deletePost(postId);
    }
}

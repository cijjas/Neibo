package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.BaseChannel;
import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.models.Entities.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

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
    public void testCreatePostWithEverythingPresent() {
        // Setup test data
        String title = "New Post";
        String description = "Description of the post";
        long userId = 1L;
        long channelId = BaseChannel.FEED.getId(); // Assuming a non-ANNOUNCEMENTS channel
        List<Long> tagIds = Arrays.asList(10L, 20L);
        long imageId = 123L;
        long neighborhoodId = 456L;

        // Mock the behavior of postDao and categorizationDao
        Post mockPost = new Post.Builder().build();
        mockPost.setPostId(1L);  // Assuming postId is set after creation

        when(postDao.createPost(title, description, userId, channelId, imageId)).thenReturn(mockPost);

        // Mock categorizationDao behavior for tag handling
        when(categorizationDao.findCategorization(10L, mockPost.getPostId())).thenReturn(Optional.empty());
        when(categorizationDao.findCategorization(20L, mockPost.getPostId())).thenReturn(Optional.empty());

        // Call the method under test
        Post createdPost = postService.createPost(title, description, userId, channelId, tagIds, imageId, neighborhoodId);

        // Verify the interactions with the mocked dependencies
        verify(postDao, times(1)).createPost(title, description, userId, channelId, imageId);
        verify(categorizationDao, times(1)).findCategorization(10L, mockPost.getPostId());
        verify(categorizationDao, times(1)).findCategorization(20L, mockPost.getPostId());
        verify(categorizationDao, times(1)).createCategorization(10L, mockPost.getPostId());
        verify(categorizationDao, times(1)).createCategorization(20L, mockPost.getPostId());
        verify(emailService, times(0)).sendBatchAnnouncementMail(mockPost, neighborhoodId);  // Ensure email is not sent

        // Assert that the post is created successfully
        assertNotNull(createdPost);
    }

    @Test
    public void testCreatePostWithNoTags() {
        // Setup test data
        String title = "New Post";
        String description = "Description of the post";
        long userId = 1L;
        long channelId = 2L;
        List<Long> tagIds = null; // No tags
        long imageId = 123L;
        long neighborhoodId = 456L;

        // Mock the behavior of postDao
        Post mockPost = new Post.Builder().build();
        mockPost.setPostId(1L);

        when(postDao.createPost(title, description, userId, channelId, imageId)).thenReturn(mockPost);

        // Call the method under test
        Post createdPost = postService.createPost(title, description, userId, channelId, tagIds, imageId, neighborhoodId);

        // Verify the interactions with the mocked dependencies
        verify(postDao, times(1)).createPost(title, description, userId, channelId, imageId);
        verify(categorizationDao, times(0)).findCategorization(anyLong(), anyLong());  // No categorization check should happen
        verify(categorizationDao, times(0)).createCategorization(anyLong(), anyLong());  // No categorization creation
        verify(emailService, times(0)).sendBatchAnnouncementMail(mockPost, neighborhoodId);  // Ensure email is not sent

        // Assert that the post is created successfully
        assertNotNull(createdPost);
    }

    @Test
    public void testCreatePostWithAnnouncementsChannel() {
        // Setup test data
        String title = "New Post";
        String description = "Description of the post";
        long userId = 1L;
        long channelId = BaseChannel.ANNOUNCEMENTS.getId();  // ANNOUNCEMENTS channel ID
        List<Long> tagIds = Arrays.asList(10L, 20L);
        long imageId = 123L;
        long neighborhoodId = 456L;

        // Mock the behavior of postDao and categorizationDao
        Post mockPost = new Post.Builder().build();
        mockPost.setPostId(1L);

        when(postDao.createPost(title, description, userId, channelId, imageId)).thenReturn(mockPost);
        when(categorizationDao.findCategorization(10L, mockPost.getPostId())).thenReturn(Optional.empty());
        when(categorizationDao.findCategorization(20L, mockPost.getPostId())).thenReturn(Optional.empty());

        // Call the method under test
        Post createdPost = postService.createPost(title, description, userId, channelId, tagIds, imageId, neighborhoodId);

        // Verify the interactions with the mocked dependencies
        verify(postDao, times(1)).createPost(title, description, userId, channelId, imageId);
        verify(categorizationDao, times(1)).findCategorization(10L, mockPost.getPostId());
        verify(categorizationDao, times(1)).findCategorization(20L, mockPost.getPostId());
        verify(categorizationDao, times(1)).createCategorization(10L, mockPost.getPostId());
        verify(categorizationDao, times(1)).createCategorization(20L, mockPost.getPostId());
        verify(emailService, times(1)).sendBatchAnnouncementMail(mockPost, neighborhoodId);  // Ensure email is sent

        // Assert that the post is created successfully
        assertNotNull(createdPost);
    }

    @Test
    public void testDeletePostWithTags() {
        long postId = 1L;
        long neighborhoodId = 2L;

        // Create mock tags
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

        boolean result = postService.deletePost(postId, neighborhoodId);

        // Verify result
        assertTrue(result);

        // Verify interactions
        verify(postDao, times(1)).findPost(postId);
        verify(categorizationDao, times(1)).deleteCategorization(101L, postId);
        verify(categorizationDao, times(1)).deleteCategorization(102L, postId);
        verify(postDao, times(1)).deletePost(postId);
    }

    @Test
    public void testDeletePostWithoutTags() {
        long postId = 1L;
        long neighborhoodId = 2L;

        Post post = new Post.Builder().build();
        post.setTags(Collections.emptySet()); // No tags

        when(postDao.findPost(postId)).thenReturn(Optional.of(post));
        when(postDao.deletePost(postId)).thenReturn(true);

        boolean result = postService.deletePost(postId, neighborhoodId);

        // Verify result
        assertTrue(result);

        // Verify interactions
        verify(postDao, times(1)).findPost(postId);
        verify(categorizationDao, never()).deleteCategorization(anyLong(), anyLong());
        verify(postDao, times(1)).deletePost(postId);
    }
}

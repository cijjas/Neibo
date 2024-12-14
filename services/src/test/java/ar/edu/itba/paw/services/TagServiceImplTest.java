package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.interfaces.persistence.TagMappingDao;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.models.Entities.Tag;
import ar.edu.itba.paw.models.Entities.TagMapping;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TagServiceImplTest {
    @Mock
    private TagDao tagDao;
    @Mock
    private PostDao postDao;
    @Mock
    private CategorizationDao categorizationDao;
    @Mock
    private TagMappingDao tagMappingDao;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    public void testCreateTagTagFoundAndTagMappingFound() {
        // Arrange
        long neighborhoodId = 1L;
        String tagName = "Park";
        Tag existingTag = new Tag.Builder().tagId(1L).tag(tagName).build();
        TagMapping existingTagMapping = new TagMapping(new Neighborhood.Builder().neighborhoodId(1L).build(), existingTag);

        when(tagDao.findTag(tagName)).thenReturn(Optional.of(existingTag)); // Tag found
        when(tagMappingDao.findTagMapping(existingTag.getTagId(), neighborhoodId)).thenReturn(Optional.of(existingTagMapping)); // Mapping found

        // Act
        Tag result = tagService.createTag(neighborhoodId, tagName);

        // Assert
        verify(tagDao, times(1)).findTag(tagName); // findTag called once
        verify(tagMappingDao, times(1)).findTagMapping(existingTag.getTagId(), neighborhoodId); // findTagMapping called once

        assertNotNull(result);
        assertEquals(existingTag, result);
    }

    @Test
    public void testCreateTagTagNotFoundAndTagMappingFound() {
        // Arrange
        long neighborhoodId = 1L;
        String tagName = "Park";
        Tag newTag = new Tag.Builder().tagId(2L).tag(tagName).build();
        TagMapping existingTagMapping = new TagMapping(new Neighborhood.Builder().neighborhoodId(1L).build(), new Tag.Builder().tagId(1L).tag(tagName).build());

        when(tagDao.findTag(tagName)).thenReturn(Optional.empty()); // Tag not found
        when(tagDao.createTag(tagName)).thenReturn(newTag); // New tag created
        when(tagMappingDao.findTagMapping(newTag.getTagId(), neighborhoodId)).thenReturn(Optional.of(existingTagMapping)); // Mapping found

        // Act
        Tag result = tagService.createTag(neighborhoodId, tagName);

        // Assert
        verify(tagDao, times(1)).findTag(tagName); // findTag called once
        verify(tagDao, times(1)).createTag(tagName); // createTag called once
        verify(tagMappingDao, times(1)).findTagMapping(newTag.getTagId(), neighborhoodId); // findTagMapping called once

        assertNotNull(result);
        assertEquals(newTag, result);
    }

    @Test
    public void testCreateTagTagFoundAndTagMappingNotFound() {
        // Arrange
        long neighborhoodId = 1L;
        String tagName = "Park";
        Tag existingTag = new Tag.Builder().tagId(1L).tag(tagName).build();
        TagMapping newTagMapping = new TagMapping(new Neighborhood.Builder().neighborhoodId(1L).build(), existingTag);

        when(tagDao.findTag(tagName)).thenReturn(Optional.of(existingTag)); // Tag found
        when(tagMappingDao.findTagMapping(existingTag.getTagId(), neighborhoodId)).thenReturn(Optional.empty()); // Mapping not found
        when(tagMappingDao.createTagMappingDao(existingTag.getTagId(), neighborhoodId)).thenReturn(newTagMapping); // New mapping created

        // Act
        Tag result = tagService.createTag(neighborhoodId, tagName);

        // Assert
        verify(tagDao, times(1)).findTag(tagName); // findTag called once
        verify(tagMappingDao, times(1)).findTagMapping(existingTag.getTagId(), neighborhoodId); // findTagMapping called once
        verify(tagMappingDao, times(1)).createTagMappingDao(existingTag.getTagId(), neighborhoodId); // createTagMappingDao called once

        assertNotNull(result);
        assertEquals(existingTag, result);
    }

    @Test
    public void testCreateTagTagNotFoundAndTagMappingNotFound() {
        // Arrange
        long neighborhoodId = 1L;
        String tagName = "Park";
        Tag newTag = new Tag.Builder().tagId(1L).tag(tagName).build();
        TagMapping newTagMapping = new TagMapping(new Neighborhood.Builder().neighborhoodId(1L).build(), newTag);

        when(tagDao.findTag(tagName)).thenReturn(Optional.empty()); // Tag not found
        when(tagDao.createTag(tagName)).thenReturn(newTag); // New tag created
        when(tagMappingDao.findTagMapping(newTag.getTagId(), neighborhoodId)).thenReturn(Optional.empty()); // Mapping not found
        when(tagMappingDao.createTagMappingDao(newTag.getTagId(), neighborhoodId)).thenReturn(newTagMapping); // New mapping created

        // Act
        Tag result = tagService.createTag(neighborhoodId, tagName);

        // Assert
        verify(tagDao, times(1)).findTag(tagName); // findTag called once
        verify(tagDao, times(1)).createTag(tagName); // createTag called once
        verify(tagMappingDao, times(1)).findTagMapping(newTag.getTagId(), neighborhoodId); // findTagMapping called once
        verify(tagMappingDao, times(1)).createTagMappingDao(newTag.getTagId(), neighborhoodId); // createTagMappingDao called once

        assertNotNull(result);
        assertEquals(newTag, result);
    }

    @Test
    public void testDeleteTagWithMultiplePosts() {
        long neighborhoodId = 1L;
        long tagId = 101L;

        int totalPosts = 250; // 3 pages with 100 posts per page
        int batchSize = 100;

        List<Post> postsBatch1 = Arrays.asList(new Post.Builder().postId(1L).build(), new Post.Builder().postId(2L).build());
        List<Post> postsBatch2 = Arrays.asList(new Post.Builder().postId(3L).build(), new Post.Builder().postId(4L).build());
        List<Post> postsBatch3 = Collections.singletonList(new Post.Builder().postId(5L).build());

        when(postDao.countPosts(null, Collections.singletonList(tagId), neighborhoodId, null, null)).thenReturn(totalPosts);
        when(postDao.getPosts(null, 1, batchSize, Collections.singletonList(tagId), neighborhoodId, null, null))
                .thenReturn(postsBatch1);
        when(postDao.getPosts(null, 2, batchSize, Collections.singletonList(tagId), neighborhoodId, null, null))
                .thenReturn(postsBatch2);
        when(postDao.getPosts(null, 3, batchSize, Collections.singletonList(tagId), neighborhoodId, null, null))
                .thenReturn(postsBatch3);
        when(tagMappingDao.getTagMappings(tagId, null, 1, 1)).thenReturn(Collections.emptyList());
        when(tagDao.deleteTag(tagId)).thenReturn(true);

        boolean result = tagService.deleteTag(neighborhoodId, tagId);

        // Verify result
        assertTrue(result);

        // Verify interactions
        verify(tagMappingDao, times(1)).deleteTagMapping(tagId, neighborhoodId);
        verify(postDao, times(1)).countPosts(null, Collections.singletonList(tagId), neighborhoodId, null, null);
        verify(postDao, times(3)).getPosts(eq(null), anyInt(), eq(batchSize), eq(Collections.singletonList(tagId)), eq(neighborhoodId), eq(null), eq(null));
        verify(categorizationDao, times(5)).deleteCategorization(eq(tagId), anyLong());
        verify(tagMappingDao, times(1)).getTagMappings(tagId, null, 1, 1);
        verify(tagDao, times(1)).deleteTag(tagId);
    }

    @Test
    public void testDeleteTagWithNoPosts() {
        long neighborhoodId = 1L;
        long tagId = 101L;

        when(postDao.countPosts(null, Collections.singletonList(tagId), neighborhoodId, null, null)).thenReturn(0);
        when(tagMappingDao.getTagMappings(tagId, null, 1, 1)).thenReturn(Collections.emptyList());
        when(tagDao.deleteTag(tagId)).thenReturn(true);

        boolean result = tagService.deleteTag(neighborhoodId, tagId);

        // Verify result
        assertTrue(result);

        // Verify interactions
        verify(tagMappingDao, times(1)).deleteTagMapping(tagId, neighborhoodId);
        verify(postDao, times(1)).countPosts(null, Collections.singletonList(tagId), neighborhoodId, null, null);
        verify(postDao, never()).getPosts(anyLong(), anyInt(), anyInt(), anyList(), anyLong(), anyLong(), anyLong());
        verify(categorizationDao, never()).deleteCategorization(anyLong(), anyLong());
        verify(tagMappingDao, times(1)).getTagMappings(tagId, null, 1, 1);
        verify(tagDao, times(1)).deleteTag(tagId);
    }

    @Test
    public void testDeleteTagStillMappedToOtherNeighborhoods() {
        long neighborhoodId = 1L;
        long tagId = 101L;

        when(postDao.countPosts(null, Collections.singletonList(tagId), neighborhoodId, null, null)).thenReturn(0);
        when(tagMappingDao.getTagMappings(tagId, null, 1, 1)).thenReturn(Collections.singletonList(new TagMapping(new Neighborhood.Builder().neighborhoodId(neighborhoodId).build(), new Tag.Builder().tagId(tagId).build())));

        boolean result = tagService.deleteTag(neighborhoodId, tagId);

        // Verify result
        assertTrue(result);

        // Verify interactions
        verify(tagMappingDao, times(1)).deleteTagMapping(tagId, neighborhoodId);
        verify(postDao, times(1)).countPosts(null, Collections.singletonList(tagId), neighborhoodId, null, null);
        verify(postDao, never()).getPosts(anyLong(), anyInt(), anyInt(), anyList(), anyLong(), anyLong(), anyLong());
        verify(categorizationDao, never()).deleteCategorization(anyLong(), anyLong());
        verify(tagMappingDao, times(1)).getTagMappings(tagId, null, 1, 1);
        verify(tagDao, never()).deleteTag(tagId);
    }
}

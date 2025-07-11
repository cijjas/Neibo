package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.interfaces.persistence.TagMappingDao;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.models.Entities.Tag;
import ar.edu.itba.paw.models.Entities.TagMapping;
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
    public void create_tag_tagMapping() {
        // Pre Conditions
        long neighborhoodId = 1L;
        String tagName = "Park";
        Tag existingTag = new Tag.Builder().tagId(1L).tag(tagName).build();
        TagMapping existingTagMapping = new TagMapping(new Neighborhood.Builder().neighborhoodId(1L).build(), existingTag);

        when(tagDao.findTag(tagName)).thenReturn(Optional.of(existingTag));
        when(tagMappingDao.findTagMapping(neighborhoodId, existingTag.getTagId())).thenReturn(Optional.of(existingTagMapping));

        // Exercise
        Tag result = tagService.createTag(neighborhoodId, tagName);

        // Validation & Post Conditions
        verify(tagDao, times(1)).findTag(tagName);
        verify(tagMappingDao, times(1)).findTagMapping(neighborhoodId, existingTag.getTagId());

        assertNotNull(result);
        assertEquals(existingTag, result);
    }

    @Test
    public void create_tag_noTagMapping() {
        // Pre Conditions
        long neighborhoodId = 1L;
        String tagName = "Park";
        Tag existingTag = new Tag.Builder().tagId(1L).tag(tagName).build();
        TagMapping newTagMapping = new TagMapping(new Neighborhood.Builder().neighborhoodId(1L).build(), existingTag);

        when(tagDao.findTag(tagName)).thenReturn(Optional.of(existingTag));
        when(tagMappingDao.findTagMapping(neighborhoodId, existingTag.getTagId())).thenReturn(Optional.empty());
        when(tagMappingDao.createTagMappingDao(neighborhoodId, existingTag.getTagId())).thenReturn(newTagMapping);

        // Exercise
        Tag result = tagService.createTag(neighborhoodId, tagName);

        // Validation & Post Conditions
        verify(tagDao, times(1)).findTag(tagName);
        verify(tagMappingDao, times(1)).findTagMapping(neighborhoodId, existingTag.getTagId());
        verify(tagMappingDao, times(1)).createTagMappingDao(neighborhoodId, existingTag.getTagId());

        assertNotNull(result);
        assertEquals(existingTag, result);
    }

    @Test
    public void create_noTag_noTagMapping() {
        // Pre Conditions
        long neighborhoodId = 1L;
        String tagName = "Park";
        Tag newTag = new Tag.Builder().tagId(1L).tag(tagName).build();
        TagMapping newTagMapping = new TagMapping(new Neighborhood.Builder().neighborhoodId(1L).build(), newTag);

        when(tagDao.findTag(tagName)).thenReturn(Optional.empty());
        when(tagDao.createTag(tagName)).thenReturn(newTag);
        when(tagMappingDao.findTagMapping(neighborhoodId, newTag.getTagId())).thenReturn(Optional.empty());
        when(tagMappingDao.createTagMappingDao(neighborhoodId, newTag.getTagId())).thenReturn(newTagMapping);

        // Exercise
        Tag result = tagService.createTag(neighborhoodId, tagName);

        // Validation & Post Conditions
        verify(tagDao, times(1)).findTag(tagName);
        verify(tagDao, times(1)).createTag(tagName);
        verify(tagMappingDao, times(1)).findTagMapping(neighborhoodId, newTag.getTagId());
        verify(tagMappingDao, times(1)).createTagMappingDao(neighborhoodId, newTag.getTagId());

        assertNotNull(result);
        assertEquals(newTag, result);
    }

    @Test
    public void delete_withPosts() {
        // Pre Conditions
        long neighborhoodId = 1L;
        long tagId = 101L;

        int totalPosts = 250;
        int batchSize = 100;

        List<Post> postsBatch1 = Arrays.asList(new Post.Builder().postId(1L).build(), new Post.Builder().postId(2L).build());
        List<Post> postsBatch2 = Arrays.asList(new Post.Builder().postId(3L).build(), new Post.Builder().postId(4L).build());
        List<Post> postsBatch3 = Collections.singletonList(new Post.Builder().postId(5L).build());

        when(postDao.countPosts(neighborhoodId, null, null, Collections.singletonList(tagId), null)).thenReturn(totalPosts);
        when(postDao.getPosts(neighborhoodId, null, null, Collections.singletonList(tagId), null, 1, batchSize))
                .thenReturn(postsBatch1);
        when(postDao.getPosts(neighborhoodId, null, null, Collections.singletonList(tagId), null, 2, batchSize))
                .thenReturn(postsBatch2);
        when(postDao.getPosts(neighborhoodId, null, null, Collections.singletonList(tagId), null, 3, batchSize))
                .thenReturn(postsBatch3);
        when(tagMappingDao.getTagMappings(null, tagId, 1, 1)).thenReturn(Collections.emptyList());
        when(tagDao.deleteTag(tagId)).thenReturn(true);

        // Exercise
        boolean result = tagService.deleteTag(neighborhoodId, tagId);

        // Validation & Post Conditions
        assertTrue(result);

        verify(tagMappingDao, times(1)).deleteTagMapping(neighborhoodId, tagId);
        verify(postDao, times(1)).countPosts(neighborhoodId, null, null, Collections.singletonList(tagId), null);
        verify(postDao, times(3)).getPosts(eq(neighborhoodId), eq(null), eq(null), eq(Collections.singletonList(tagId)), eq(null), anyInt(), eq(batchSize));
        verify(categorizationDao, times(5)).deleteCategorization(anyLong(), eq(tagId));
        verify(tagMappingDao, times(1)).getTagMappings(null, tagId, 1, 1);
        verify(tagDao, times(1)).deleteTag(tagId);
    }

    @Test
    public void delete_noPosts() {
        // Pre Conditions
        long neighborhoodId = 1L;
        long tagId = 101L;

        when(postDao.countPosts(neighborhoodId, null, null, Collections.singletonList(tagId), null)).thenReturn(0);
        when(tagMappingDao.getTagMappings(null, tagId, 1, 1)).thenReturn(Collections.emptyList());
        when(tagDao.deleteTag(tagId)).thenReturn(true);

        // Exercise
        boolean result = tagService.deleteTag(neighborhoodId, tagId);

        // Validation & Post Conditions
        assertTrue(result);

        // Verify interactions
        verify(tagMappingDao, times(1)).deleteTagMapping(neighborhoodId, tagId);
        verify(postDao, times(1)).countPosts(neighborhoodId, null, null, Collections.singletonList(tagId), null);
        verify(postDao, never()).getPosts(anyLong(), anyLong(), anyLong(), anyList(), anyLong(), anyInt(), anyInt());
        verify(categorizationDao, never()).deleteCategorization(anyLong(), anyLong());
        verify(tagMappingDao, times(1)).getTagMappings(null, tagId, 1, 1);
        verify(tagDao, times(1)).deleteTag(tagId);
    }

    @Test
    public void delete_withNeighborhoods() {
        // Pre Conditions
        long neighborhoodId = 1L;
        long tagId = 101L;

        when(postDao.countPosts(neighborhoodId, null, null, Collections.singletonList(tagId), null)).thenReturn(0);
        when(tagMappingDao.getTagMappings(null, tagId, 1, 1)).thenReturn(Collections.singletonList(new TagMapping(new Neighborhood.Builder().neighborhoodId(neighborhoodId).build(), new Tag.Builder().tagId(tagId).build())));

        // Exercise
        boolean result = tagService.deleteTag(neighborhoodId, tagId);

        // Validation & Post Conditions
        assertTrue(result);

        verify(tagMappingDao, times(1)).deleteTagMapping(neighborhoodId, tagId);
        verify(postDao, times(1)).countPosts(neighborhoodId, null, null, Collections.singletonList(tagId), null);
        verify(postDao, never()).getPosts(anyLong(), anyLong(), anyLong(), anyList(), anyLong(), anyInt(), anyInt());
        verify(categorizationDao, never()).deleteCategorization(anyLong(), anyLong());
        verify(tagMappingDao, times(1)).getTagMappings(null, tagId, 1, 1);
        verify(tagDao, never()).deleteTag(tagId);
    }
}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.interfaces.persistence.TagMappingDao;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.Tag;
import ar.edu.itba.paw.models.Entities.TagMapping;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TagServiceImplTest {
    @Mock
    private TagDao tagDao;
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

}

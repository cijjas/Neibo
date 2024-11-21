package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.interfaces.persistence.TagMappingDao;
import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.models.Entities.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TagServiceImpl implements TagService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagServiceImpl.class);

    private final TagDao tagDao;
    private final CategorizationDao categorizationDao;
    private final TagMappingDao tagMappingDao;

    @Autowired
    public TagServiceImpl(TagDao tagDao, CategorizationDao categorizationDao, TagMappingDao tagMappingDao) {
        this.tagDao = tagDao;
        this.categorizationDao = categorizationDao;
        this.tagMappingDao = tagMappingDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Tag createTag(long neighborhoodId, String name) {
        LOGGER.info("Creating Tag {} in Neighborhood {}", name, neighborhoodId);

        //find tag by name, if it doesn't exist create it
        Tag tag = tagDao.findTag(name).orElseGet(() -> tagDao.createTag(name));

        if(!tagMappingDao.findTagMapping(tag.getTagId(), neighborhoodId).isPresent())
            tagMappingDao.createTagMappingDao(tag.getTagId(), neighborhoodId);

        return tag;
    }

    @Override
    public void categorizePost(long postId, List<Long> tagIds) {
        LOGGER.info("Associating Tags {} with Post {}", tagIds, postId);

        ValidationUtils.checkPostId(postId);

        for (long tag: tagIds)
            categorizationDao.findCategorization(tag, postId).orElseGet(() -> categorizationDao.createCategorization(tag, postId));
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Tag> findTag(long tagId, long neighborhoodId) {
        LOGGER.info("Finding Tag {} from Neighborhood {}", tagId, neighborhoodId);

        ValidationUtils.checkTagId(tagId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return tagDao.findTag(tagId, neighborhoodId);
    }

    @Override
    public List<Tag> getTags(String postURN, long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Tags in Post {} from Neighborhood {}", postURN, neighborhoodId);

        Long postId = ValidationUtils.checkURNAndExtractPostId(postURN);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPageAndSize(page, size);

        return tagDao.getTags(postId, neighborhoodId, page, size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public int calculateTagPages(String postURN, long neighborhoodId, int size) {
        LOGGER.info("Calculating Tag Pages in Post {} from Neighborhood {}", postURN, neighborhoodId);

        Long postId = ValidationUtils.checkURNAndExtractPostId(postURN);

        ValidationUtils.checkPostId(postId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(tagDao.countTags(postId, neighborhoodId), size);
    }

    // ---------------------------------------------------

    @Override
    public boolean deleteTag(long neighborhoodId, long tagId) {
        LOGGER.info("Deleting Tag {}", tagId);

        ValidationUtils.checkTagId(tagId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        tagDao.findTag(tagId, neighborhoodId).orElseThrow(NotFoundException::new);
        tagMappingDao.deleteTagMapping(tagId, neighborhoodId);

        // Will delete all categorizations associated with the tag
        categorizationDao.deleteCategorization(tagId, null);

        //if the channel was only being used by this neighborhood, it gets deleted
        if(tagMappingDao.countTagMappings(tagId, null) == 0) {
            return tagDao.deleteTag(tagId);
        }

        return true;
    }

}
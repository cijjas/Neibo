package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.interfaces.persistence.TagMappingDao;
import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.models.Entities.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TagServiceImpl implements TagService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagServiceImpl.class);

    private final TagDao tagDao;
    private final CategorizationDao categorizationDao;
    private final TagMappingDao tagMappingDao;
    private final PostDao postDao;

    @Autowired
    public TagServiceImpl(TagDao tagDao, CategorizationDao categorizationDao, TagMappingDao tagMappingDao, PostDao postDao) {
        this.tagDao = tagDao;
        this.categorizationDao = categorizationDao;
        this.tagMappingDao = tagMappingDao;
        this.postDao = postDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Tag createTag(long neighborhoodId, String name) {
        LOGGER.info("Creating Tag {} in Neighborhood {}", name, neighborhoodId);

        // Find tag by name, if it doesn't exist create it
        Tag tag = tagDao.findTag(name).orElseGet(() -> tagDao.createTag(name));

        if(!tagMappingDao.findTagMapping(tag.getTagId(), neighborhoodId).isPresent())
            tagMappingDao.createTagMappingDao(tag.getTagId(), neighborhoodId);

        return tag;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Tag> findTag(long tagId, long neighborhoodId) {
        LOGGER.info("Finding Tag {} from Neighborhood {}", tagId, neighborhoodId);

        return tagDao.findTag(tagId, neighborhoodId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getTags(Long postId, long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Tags in Post {} from Neighborhood {}", postId, neighborhoodId);

        return tagDao.getTags(postId, neighborhoodId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateTagPages(Long postId, long neighborhoodId, int size) {
        LOGGER.info("Calculating Tag Pages in Post {} from Neighborhood {}", postId, neighborhoodId);

        return PaginationUtils.calculatePages(tagDao.countTags(postId, neighborhoodId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteTag(long neighborhoodId, long tagId) {
        LOGGER.info("Deleting Tag {}", tagId);

        // Delete Tag-Neighborhood association
        tagMappingDao.deleteTagMapping(tagId, neighborhoodId);

        // Delete Tag-Posts associations
        int batchSize = 100;
        int totalPosts = postDao.countPosts(null, Collections.singletonList(tagId), neighborhoodId, null, null);
        int totalPages = PaginationUtils.calculatePages(totalPosts, batchSize);

        for (int page = 1; page <= totalPages; page++) {
            List<Post> posts = postDao.getPosts(null, page, batchSize, Collections.singletonList(tagId), neighborhoodId, null, null);
            for (Post post : posts) {
                categorizationDao.deleteCategorization(tagId, post.getPostId());
            }
        }

        // If the tag was only being used by this Neighborhood, it can safely be deleted
        if (tagMappingDao.getTagMappings(tagId, null, 1, 1).isEmpty()) {
            return tagDao.deleteTag(tagId);
        }

        return true;
    }
}
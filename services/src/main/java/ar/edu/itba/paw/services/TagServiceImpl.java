package ar.edu.itba.paw.services;

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
    public Tag createTag(long neighborhoodId, String tagName) {
        LOGGER.info("Creating Tag {} in Neighborhood {}", tagName, neighborhoodId);

        Tag tag = tagDao.findTag(tagName).orElseGet(() -> tagDao.createTag(tagName));

        if (!tagMappingDao.findTagMapping(neighborhoodId, tag.getTagId()).isPresent())
            tagMappingDao.createTagMappingDao(neighborhoodId, tag.getTagId());

        return tag;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Tag> findTag(long neighborhoodId, long tagId) {
        LOGGER.info("Finding Tag {} from Neighborhood {}", tagId, neighborhoodId);

        return tagDao.findTag(neighborhoodId, tagId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getTags(long neighborhoodId, Long postId, int page, int size) {
        LOGGER.info("Getting Tags in Post {} from Neighborhood {}", postId, neighborhoodId);

        return tagDao.getTags(neighborhoodId, postId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int countTags(long neighborhoodId, Long postId) {
        LOGGER.info("Counting Tags in Post {} from Neighborhood {}", postId, neighborhoodId);

        return tagDao.countTags(neighborhoodId, postId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteTag(long neighborhoodId, long tagId) {
        LOGGER.info("Deleting Tag {} from Neighborhood {}", tagId, neighborhoodId);

        tagMappingDao.deleteTagMapping(neighborhoodId, tagId);

        int batchSize = 100;
        int totalPosts = postDao.countPosts(neighborhoodId, null, null, Collections.singletonList(tagId), null);
        int totalPages = (int) Math.ceil((double) totalPosts / batchSize);

        for (int page = 1; page <= totalPages; page++) {
            List<Post> posts = postDao.getPosts(neighborhoodId, null, null, Collections.singletonList(tagId), null, page, batchSize);
            for (Post post : posts) {
                categorizationDao.deleteCategorization(post.getPostId(), tagId);
            }
        }

        if (tagMappingDao.getTagMappings(null, tagId, 1, 1).isEmpty()) {
            return tagDao.deleteTag(tagId);
        }

        return true;
    }
}
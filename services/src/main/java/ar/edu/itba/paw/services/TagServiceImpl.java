package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.models.Entities.Tag;
import ar.edu.itba.paw.models.TwoIds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.Validation;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagServiceImpl implements TagService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagServiceImpl.class);
    private final TagDao tagDao;
    private final CategorizationDao categorizationDao;
    private final NeighborhoodDao neighborhoodDao;

    @Autowired
    public TagServiceImpl(TagDao tagDao, CategorizationDao categorizationDao, NeighborhoodDao neighborhoodDao) {
        this.tagDao = tagDao;
        this.categorizationDao = categorizationDao;
        this.neighborhoodDao = neighborhoodDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Tag createTag(String name) {
        LOGGER.info("Creating Tag {}", name);

        return tagDao.createTag(name);
    }

    @Override
    public boolean deleteTag(long tagId) {
        LOGGER.info("Deleting Tag {}", tagId);

        ValidationUtils.checkTagId(tagId);

        return tagDao.deleteTag(tagId);
    }

    @Override
    public void createTagsAndCategorizePost(long postId, List<String> tagURIs) {
        LOGGER.info("Creating Tags in {} and associating it with Post {}", tagURIs, postId);

        ValidationUtils.checkPostId(postId);

        if (tagURIs == null) {
            return;
        }

        //cycle array of URI's extracting the id of each tag
        for (String tagURI : tagURIs) {
            long tagId = ValidationUtils.extractURNId(tagURI);
            ValidationUtils.checkTagId(tagId);
            Tag tag = tagDao.findTag(tagId).orElseThrow(() -> new NotFoundException("Tag Not Found"));
            categorizationDao.createCategorization(tagId, postId);
        }
    }

    public String createURLForTagFilter(String tags, String currentUrl, long neighborhoodId) {
        LOGGER.info("Creating URL for Tag Filter");

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        // Extract the base URL (path) without query parameters
        String baseUrl;
        int queryIndex = currentUrl.indexOf('?');
        if (queryIndex != -1) {
            baseUrl = currentUrl.substring(0, queryIndex);
        } else {
            baseUrl = currentUrl;
        }

        String[] tagArray = tags.split(",");

        StringBuilder queryString = new StringBuilder();

        for (String tag : tagArray) {
            if (!tag.trim().isEmpty()) { // Skip empty tags
                if (queryString.length() > 0) {
                    queryString.append("&"); // Add '&' between tags
                }
                queryString.append("tag=").append(tag.trim()); // Append each tag
            }
        }

        String formattedQueryString = queryString.toString();

        if (!formattedQueryString.isEmpty()) {
            // If there are tags to add, append them using '?' or '&' as separator
            char separator = baseUrl.contains("?") ? '&' : '?';
            return baseUrl + separator + formattedQueryString;
        } else {
            return baseUrl;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Optional<Tag> findTag(long tagId, long neighborhoodId) {
        LOGGER.info("Finding Tag {} from Neighborhood {}", tagId, neighborhoodId);

        ValidationUtils.checkTagId(tagId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return tagDao.findTag(tagId);
    }

    @Override
    public List<Tag> getTags(String postURN, long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Tags in Post {} from Neighborhood {}", postURN, neighborhoodId);

        Long postId = ValidationUtils.checkURNAndExtractPostId(postURN);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPageAndSize(page, size);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return tagDao.getTags(postId, neighborhoodId, page, size);
    }

    @Override
    public int countTags(String postURN, long neighborhoodId) {
        LOGGER.info("Counting Tags in Post {} from Neighborhood {}", postURN, neighborhoodId);

        Long postId = ValidationUtils.checkURNAndExtractPostId(postURN);

        ValidationUtils.checkPostId(postId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return tagDao.countTags(postId, neighborhoodId);
    }

    @Override
    public int calculateTagPages(String postURN, long neighborhoodId, int size) {
        LOGGER.info("Calculating Tag Pages in Post {} from Neighborhood {}", postURN, neighborhoodId);

        Long postId = ValidationUtils.checkURNAndExtractPostId(postURN);

        ValidationUtils.checkPostId(postId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(tagDao.countTags(postId, neighborhoodId), size);
    }
}
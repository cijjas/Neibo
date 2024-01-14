package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.models.Entities.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagServiceImpl implements TagService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagServiceImpl.class);
    private final TagDao tagDao;
    private CategorizationDao categorizationDao;
    private ChannelService channelService;

    @Autowired
    public TagServiceImpl(TagDao tagDao, CategorizationDao categorizationDao, final ChannelService channelService) {
        this.channelService = channelService;
        this.tagDao = tagDao;
        this.categorizationDao = categorizationDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Tag createTag(String name) {
        LOGGER.info("Creating Tag {}", name);
        return tagDao.createTag(name);
    }

    @Override
    public void createTagsAndCategorizePost(long postId, String tagsString) {
        LOGGER.info("Creating Tags in {} and Associating it with the Post {}", tagsString, postId);

        ValidationUtils.checkPostId(postId);

        if (tagsString == null || tagsString.isEmpty()) {
            return;
        }
        String[] tagNames = tagsString.split(",");

        // Get the existing tags from the database
        List<Tag> existingTags = tagDao.getAllTags();

        // Create a mapping of tag names to their corresponding Tag objects
        Map<String, Tag> tagMap = existingTags.stream()
                .collect(Collectors.toMap(Tag::getTag, Function.identity()));

        // Iterate through the tag names and associate the post with the tags
        for (String tagName : tagNames) {
            Tag tag = tagMap.get(tagName);
            if (tag == null) {
                // If the tag doesn't exist, create it and add it to the map
                tag = tagDao.createTag(tagName);
                tagMap.put(tagName, tag);
            }
            // Associate the post with the tag using the CategorizationDao
            categorizationDao.createCategorization(tag.getTagId(), postId);
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
    @Transactional(readOnly = true)
    public List<Tag> getTags(long neighborhoodId) {
        LOGGER.info("Getting All Tags from Neighborhood {}", neighborhoodId);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return tagDao.getTags(neighborhoodId);
    }

    @Override
    public List<Tag> getTagsByCriteria(Long postId, Long neighborhoodId, int page, int size) {

        ValidationUtils.checkNegativeTagId(postId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPageAndSize(page, size);

        return tagDao.getTagsByCriteria(postId, neighborhoodId, page, size);
    }

    @Override
    public int getTotalTagPages(long neighborhoodId, int size) {
        LOGGER.info("Getting Total Tag Pages from Neighborhood {}", neighborhoodId);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);


        return (int) Math.ceil(tagDao.getTagsCountByCriteria(0, neighborhoodId) / (double) size);
    }

    @Override
    public int getTotalTagPagesByCriteria(Long postId, Long neighborhoodId, int size) {

        ValidationUtils.checkNegativeTagId(postId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return (int) Math.ceil(tagDao.getTagsCountByCriteria(postId, neighborhoodId) / (double) size);
    }
}
package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.models.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {
    private final TagDao tagDao;
    private final CategorizationDao categorizationDao;
    @Autowired
    public TagServiceImpl(final TagDao tagDao, CategorizationDao categorizationDao) {
        this.tagDao = tagDao;
        this.categorizationDao = categorizationDao;
    }

    @Override
    public List<Tag> getTags() {
        return tagDao.getTags();
    }

    @Override
    public Optional<List<Tag>> findTagsByPostId(long id) {
        return tagDao.findTagsByPostId(id);
    }

    @Override
    public Tag createTag(String name) {
        return tagDao.createTag(name);
    }

    @Override
    public void createTagsAndCategorizePost(long postId, String tagsString) {
        if (tagsString == null || tagsString.isEmpty()) {
            return;
        }
        String[] tagNames = tagsString.split(",");

        // Get the existing tags from the database
        List<Tag> existingTags = tagDao.getTags();

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
            categorizationDao.createCategory(tag.getTagId(), postId);
        }
    }


}
package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.TagDao;
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
    public void createTagsAndCategorizePost(long postId, List<String> tagURIs) {
        LOGGER.info("Creating Tags in {} and associating it with Post {}", tagURIs, postId);

        ValidationUtils.checkPostId(postId);

        //cycle array of URI's extracting the id of each tag
        for (String tagURI : tagURIs) {
            Long tagId = ValidationUtils.checkURNAndExtractTagId(tagURI);
            tagDao.findTag(tagId).orElseThrow(() -> new NotFoundException("Tag Not Found"));
            categorizationDao.createCategorization(tagId, postId);
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

    // ---------------------------------------------------

    @Override
    public int calculateTagPages(String postURN, long neighborhoodId, int size) {
        LOGGER.info("Calculating Tag Pages in Post {} from Neighborhood {}", postURN, neighborhoodId);

        Long postId = ValidationUtils.checkURNAndExtractPostId(postURN);

        ValidationUtils.checkPostId(postId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(tagDao.countTags(postId, neighborhoodId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteTag(long tagId) {
        LOGGER.info("Deleting Tag {}", tagId);

        ValidationUtils.checkTagId(tagId);

        return tagDao.deleteTag(tagId);
    }
}
package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Channel;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PostServiceImpl implements PostService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostDao postDao;
    private final NeighborhoodDao neighborhoodDao;
    private final CategorizationDao categorizationDao;
    private final TagService tagService;
    private final ImageService imageService;
    private final ChannelService channelService;
    private final EmailService emailService;

    @Autowired
    public PostServiceImpl(final PostDao postDao, CategorizationDao categorizationDao, TagService tagService, ImageService imageService, NeighborhoodDao neighborhoodDao, ChannelService channelService, EmailService emailService) {
        this.categorizationDao = categorizationDao;
        this.imageService = imageService;
        this.tagService = tagService;
        this.postDao = postDao;
        this.neighborhoodDao = neighborhoodDao;
        this.channelService = channelService;
        this.emailService = emailService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Post createPost(String title, String description, String userURN, String channelURN, List<String> tagURNs, String imageURN, long neighborhoodId) {
        LOGGER.info("Creating Post with Title {} by User {}", title, userURN);

        Long channelId = ValidationUtils.checkURNAndExtractChannelId(channelURN);

        channelService.findChannel(channelId, neighborhoodId).orElseThrow(NotFoundException::new);

        Image i = null;
        if (imageURN != null) {
            long imageId = ValidationUtils.extractURNId(imageURN);
            ValidationUtils.checkImageId(imageId);
            i = imageService.findImage(imageId).orElseThrow(() -> new NotFoundException("Image not found"));
        }

        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN); // cannot be null due to form validation

        Post p = postDao.createPost(title, description, userId, channelId, i == null ? 0 : i.getImageId());
        if (tagURNs != null && !tagURNs.isEmpty())
            tagService.categorizePost(p.getPostId(), tagURNs, neighborhoodId);

        if(channelId == Channel.ANNOUNCEMENTS.getId())
            emailService.sendBatchAnnouncementMail(p, neighborhoodId);

        return p;
    }

    // -----------------------------------------------------------------------------------------------------------------


    @Override
    @Transactional(readOnly = true)
    public Optional<Post> findPost(final long postId) {
        LOGGER.info("Finding Post {}", postId);

        ValidationUtils.checkPostId(postId);

        return postDao.findPost(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Post> findPost(final long postId, long neighborhoodId) {
        LOGGER.info("Finding Post {} from Neighborhood {}", postId, neighborhoodId);

        ValidationUtils.checkPostId(postId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return postDao.findPost(postId, neighborhoodId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPosts(String channelURN, int page, int size, List<String> tagURNs, long neighborhoodId, String postStatusURN, String userURN) {
        LOGGER.info("Getting Posts with status {} made on Channel {} with Tags {} by User {} from Neighborhood {} ", postStatusURN, channelURN, tagURNs, userURN, neighborhoodId);

        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN);
        Long channelId = ValidationUtils.checkURNAndExtractChannelId(channelURN);
        Long postStatusId = ValidationUtils.checkURNAndExtractPostStatusId(postStatusURN);
        List<Long> tagIds = new ArrayList<>();
        if (tagURNs != null && !tagURNs.isEmpty())
            for (String tagURN : tagURNs)
                tagIds.add(ValidationUtils.checkURNAndExtractTagId(tagURN));

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPageAndSize(page, size);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return postDao.getPosts(channelId, page, size, tagIds, neighborhoodId, postStatusId, userId);
    }

    // ---------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public int calculatePostPages(String channelURN, int size, List<String> tagURNs, long neighborhoodId, String postStatusURN, String userURN) {
        LOGGER.info("Calculating Post pages with status {} made on Channel {} with Tags {} by User {} from Neighborhood {} ", postStatusURN, channelURN, tagURNs, userURN, neighborhoodId);

        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN);
        Long channelId = ValidationUtils.checkURNAndExtractChannelId(channelURN);
        Long postStatusId = ValidationUtils.checkURNAndExtractPostStatusId(postStatusURN);
        List<Long> tagIds = new ArrayList<>();
        if (tagURNs != null && !tagURNs.isEmpty())
            for (String tagURN : tagURNs)
                tagIds.add(ValidationUtils.checkURNAndExtractTagId(tagURN));

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(postDao.countPosts(channelId, tagIds, neighborhoodId, postStatusId, userId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deletePost(long postId, long neighborhoodId) {
        LOGGER.info("Deleting Post {}", postId);

        ValidationUtils.checkPostId(postId);

        findPost(postId).orElseThrow(NotFoundException::new);

        // Delete categorizations associated with the Post
        categorizationDao.deleteCategorization(null, postId);

        return postDao.deletePost(postId);
    }
}

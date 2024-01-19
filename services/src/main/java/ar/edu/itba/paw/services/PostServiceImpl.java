package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PostServiceImpl implements PostService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostServiceImpl.class);
    private final PostDao postDao;
    private final NeighborhoodDao neighborhoodDao;
    private final UserService userService;
    private final EmailService emailService;
    private final TagService tagService;
    private final ImageService imageService;

    @Autowired
    public PostServiceImpl(final PostDao postDao, UserService userService, EmailService emailService,
                           TagService tagService, ImageService imageService, NeighborhoodDao neighborhoodDao) {
        this.imageService = imageService;
        this.tagService = tagService;
        this.postDao = postDao;
        this.userService = userService;
        this.emailService = emailService;
        this.neighborhoodDao = neighborhoodDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Post createPost(String title, String description, long userId, long channelId, String tags, MultipartFile imageFile) {
        LOGGER.info("Creating Post with Title {} by User {}", title, userId);

        Image i = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            i = imageService.storeImage(imageFile);
        }
        Post p = postDao.createPost(title, description, userId, channelId, i == null ? 0 : i.getImageId());
        tagService.createTagsAndCategorizePost(p.getPostId(), tags);
        return p;
    }

    @Override
    public Post createAdminPost(final long neighborhoodId, final String title, final String description, final long userId, final int channelId, final String tags, final MultipartFile imageFile) {
        LOGGER.info("Creating Admin Post with Title {} by User {}", title, userId);

        Post post = createPost(title, description, userId, channelId, tags, imageFile);
        assert post != null;
        emailService.sendAnnouncementMail(post, userService.getNeighbors(neighborhoodId));
        return post;
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
    public List<Post> getPosts(String channel, int page, int size, List<String> tags, long neighborhoodId, String postStatus, Long userId) {
        LOGGER.info("Getting Posts with status {} made on Channel {} with Tags {} by User {} from Neighborhood {} ", postStatus, channel, tags, userId, neighborhoodId);

        ValidationUtils.checkOptionalChannelString(channel);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPostStatusString(postStatus);
        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkPageAndSize(page, size);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return postDao.getPosts(channel, page, size, tags, neighborhoodId, postStatus, userId);
    }

    // ---------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public int countPosts(String channel, List<String> tags, long neighborhoodId, String postStatus, Long userId) {
        LOGGER.info("Counting Posts with status {} made on Channel {} with Tags {} by User {} from Neighborhood {} ", postStatus, channel, tags, userId, neighborhoodId);

        ValidationUtils.checkOptionalChannelString(channel);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPostStatusString(postStatus);
        ValidationUtils.checkUserId(userId);

        return postDao.countPosts(channel, tags, neighborhoodId, postStatus, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculatePostPages(String channel, int size, List<String> tags, long neighborhoodId, String postStatus, Long userId) {
        LOGGER.info("Calculating Post pages with status {} made on Channel {} with Tags {} by User {} from Neighborhood {} ", postStatus, channel, tags, userId, neighborhoodId);

        ValidationUtils.checkOptionalChannelString(channel);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPostStatusString(postStatus);
        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(postDao.countPosts(channel, tags, neighborhoodId, postStatus, userId), size);
    }
}

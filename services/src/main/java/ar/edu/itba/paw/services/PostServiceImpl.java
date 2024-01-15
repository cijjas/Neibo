package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
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
    private final ChannelDao channelDao;
    private final UserService userService;
    private final EmailService emailService;
    private final TagService tagService;
    private final ImageService imageService;

    @Autowired
    public PostServiceImpl(final PostDao postDao, final ChannelDao channelDao, UserService userService, EmailService emailService, TagService tagService, ImageService imageService) {
        this.imageService = imageService;
        this.tagService = tagService;
        this.postDao = postDao;
        this.channelDao = channelDao;
        this.userService = userService;
        this.emailService = emailService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Post createPost(String title, String description, long neighborId, long channelId, String tags, MultipartFile imageFile) {
        LOGGER.info("Creating Post with Title {} by User {}", title, neighborId);

        Image i = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            i = imageService.storeImage(imageFile);
        }
        Post p = postDao.createPost(title, description, neighborId, channelId, i == null ? 0 : i.getImageId());
        tagService.createTagsAndCategorizePost(p.getPostId(), tags);
        return p;
    }

    @Override
    public Post createAdminPost(final long neighborhoodId, final String title, final String description, final long neighborId, final int channelId, final String tags, final MultipartFile imageFile) {

        Post post = createPost(title, description, neighborId, channelId, tags, imageFile);
        assert post != null;
        emailService.sendAnnouncementMail(post, userService.getNeighbors(neighborhoodId));
        return post;
    }

    // -----------------------------------------------------------------------------------------------------------------


    @Override
    @Transactional(readOnly = true)
    public Optional<Post> findPost(final long postId) {
        LOGGER.info("Finding Post with ID {}", postId);

        ValidationUtils.checkPostId(postId);

        return postDao.findPost(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPosts(String channel, int page, int size, List<String> tags, long neighborhoodId, PostStatus postStatus, Long userId) {
        LOGGER.info("Getting Posts from Neighborhood {}, on Channel {}, with Tags {}, by User {} and Post Status {} ", neighborhoodId, channel, tags, userId, postStatus);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return postDao.getPosts(channel, page, size, tags, neighborhoodId, postStatus, userId);
    }

    // ---------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public int countPosts(String channel, List<String> tags, long neighborhoodId, PostStatus postStatus, Long userId) {
        LOGGER.info("Getting Posts from Neighborhood {}, on Channel {}, with Tags {} and Post Status {}", neighborhoodId, channel, tags, postStatus);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkUserId(userId);

        return postDao.countPosts(channel, tags, neighborhoodId, postStatus, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculatePostPages(String channel, int size, List<String> tags, long neighborhoodId, PostStatus postStatus, Long userId) {
        LOGGER.info("Getting Total Post Pages with size {} for Posts from Neighborhood {}, on Channel {}, with Tags {} and Post Status {}", size, neighborhoodId, channel, tags, postStatus);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkUserId(userId);

        return PaginationUtils.calculatePages(postDao.countPosts(channel, tags, neighborhoodId, postStatus, userId), size);
    }
}

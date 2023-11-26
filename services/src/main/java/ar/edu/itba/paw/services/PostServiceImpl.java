package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.MainEntities.Image;
import ar.edu.itba.paw.models.MainEntities.Post;
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
    public List<Post> getWorkerPostsByCriteria(String channel, int page, int size, List<String> tags, long neighborhoodId, PostStatus postStatus, long userId) {
        LOGGER.info("Getting Workers' Posts from Neighborhood {}, on Channel {}, with Tags {} and Post Status {}", neighborhoodId, channel, tags, postStatus);
        return postDao.getPostsByCriteria(channel, page, size, tags, neighborhoodId, postStatus, userId);
    }

    @Override
    public int getWorkerPostsCountByCriteria(String channel, List<String> tags, long neighborhoodId, PostStatus postStatus, long userId) {
        LOGGER.info("Getting Workers' Posts Count from Neighborhood {}, on Channel {}, with Tags {} and Post Status {}", neighborhoodId, channel, tags, postStatus);
        // parse de statusString into the postStatusEnum
        return postDao.getPostsCountByCriteria(channel, tags, neighborhoodId, postStatus, userId);
    }

    @Override
    public int getWorkerTotalPages(String channel, int size, List<String> tags, long neighborhoodId, PostStatus postStatus, long userId) {
        LOGGER.info("Getting Workers' Total Post Pages with size {} for Posts from Neighborhood {}, on Channel {}, with Tags {} and Post Status {}", size, neighborhoodId, channel, tags, postStatus);
        // parse de statusString into the postStatusEnum
        return (int) Math.ceil((double) getWorkerPostsCountByCriteria(channel, tags, neighborhoodId, postStatus, userId) / size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPostsByCriteria(String channel, int page, int size, List<String> tags, long neighborhoodId, PostStatus postStatus) {
        LOGGER.info("Getting Posts from Neighborhood {}, on Channel {}, with Tags {} and Post Status {}", neighborhoodId, channel, tags, postStatus);
        return postDao.getPostsByCriteria(channel, page, size, tags, neighborhoodId, postStatus, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public int getPostsCountByCriteria(String channel, List<String> tags, long neighborhoodId, PostStatus postStatus, long userId) {
        LOGGER.info("Getting Posts from Neighborhood {}, on Channel {}, with Tags {} and Post Status {}", neighborhoodId, channel, tags, postStatus);
        // parse de statusString into the postStatusEnum
        return postDao.getPostsCountByCriteria(channel, tags, neighborhoodId, postStatus, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalPages(String channel, int size, List<String> tags, long neighborhoodId, PostStatus postStatus, long userId) {
        LOGGER.info("Getting Total Post Pages with size {} for Posts from Neighborhood {}, on Channel {}, with Tags {} and Post Status {}", size, neighborhoodId, channel, tags, postStatus);
        // parse de statusString into the postStatusEnum
        return (int) Math.ceil((double) getPostsCountByCriteria(channel, tags, neighborhoodId, postStatus, userId) / size);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Post> findPostById(final long id) {
        return postDao.findPostById(id);
    }
}

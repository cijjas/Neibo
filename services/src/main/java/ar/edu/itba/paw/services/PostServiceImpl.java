package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.BaseChannel;
import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.models.Entities.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PostServiceImpl implements PostService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostDao postDao;
    private final CategorizationDao categorizationDao;
    private final TagService tagService;
    private final EmailService emailService;

    @Autowired
    public PostServiceImpl(final PostDao postDao, CategorizationDao categorizationDao, TagService tagService, EmailService emailService) {
        this.categorizationDao = categorizationDao;
        this.tagService = tagService;
        this.postDao = postDao;
        this.emailService = emailService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Post createPost(String title, String description, long userId, long channelId, List<Long> tagIds, Long imageId, long neighborhoodId) {
        LOGGER.info("Creating Post with Title {} by User {}", title, userId);

        Post p = postDao.createPost(title, description, userId, channelId, imageId == null ? 0 : imageId);
        if (tagIds != null && !tagIds.isEmpty())
            tagService.categorizePost(p.getPostId(), tagIds);

        if(channelId == BaseChannel.ANNOUNCEMENTS.getId())
            emailService.sendBatchAnnouncementMail(p, neighborhoodId);

        return p;
    }

    // -----------------------------------------------------------------------------------------------------------------


    @Override
    @Transactional(readOnly = true)
    public Optional<Post> findPost(final long postId) {
        LOGGER.info("Finding Post {}", postId);

        return postDao.findPost(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Post> findPost(final long postId, long neighborhoodId) {
        LOGGER.info("Finding Post {} from Neighborhood {}", postId, neighborhoodId);

        return postDao.findPost(postId, neighborhoodId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPosts(Long channelId, int page, int size, List<Long> tagIds, long neighborhoodId, Long postStatusId, Long userId) {
        LOGGER.info("Getting Posts with status {} made on Channel {} with Tags {} by User {} from Neighborhood {} ", postStatusId, channelId, tagIds, userId, neighborhoodId);

        return postDao.getPosts(channelId, page, size, tagIds, neighborhoodId, postStatusId, userId);
    }

    // ---------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public int calculatePostPages(Long channelId, int size, List<Long> tagIds, long neighborhoodId, Long postStatusId, Long userId) {
        LOGGER.info("Calculating Post pages with status {} made on Channel {} with Tags {} by User {} from Neighborhood {} ", postStatusId, channelId, tagIds, userId, neighborhoodId);

        return PaginationUtils.calculatePages(postDao.countPosts(channelId, tagIds, neighborhoodId, postStatusId, userId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deletePost(long postId, long neighborhoodId) {
        LOGGER.info("Deleting Post {}", postId);

        // Delete categorizations associated with the Post
        categorizationDao.deleteCategorization(null, postId);

        return postDao.deletePost(postId);
    }
}

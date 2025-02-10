package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.BaseChannel;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.models.Entities.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class PostServiceImpl implements PostService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostDao postDao;
    private final CategorizationDao categorizationDao;
    private final EmailService emailService;

    @Autowired
    public PostServiceImpl(PostDao postDao, CategorizationDao categorizationDao, EmailService emailService) {
        this.categorizationDao = categorizationDao;
        this.postDao = postDao;
        this.emailService = emailService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Post createPost(long neighborhoodId, long userId, String title, String description, long channelId, List<Long> tagIds, Long imageId) {
        LOGGER.info("Creating Post {} described as {} in Channel {} with Tags {} by User {} in Neighborhood {}", title, description, channelId, tagIds, userId, neighborhoodId);

        Post p = postDao.createPost(userId, title, description, channelId, imageId == null ? 0 : imageId);
        if (tagIds != null && !tagIds.isEmpty())
            for (long tag : tagIds)
                categorizationDao.findCategorization(p.getPostId(), tag).orElseGet(() -> categorizationDao.createCategorization(p.getPostId(), tag));

        if (channelId == BaseChannel.ANNOUNCEMENTS.getId())
            emailService.sendBatchAnnouncementMail(p, neighborhoodId);

        return p;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Post> findPost(long neighborhoodId, long postId) {
        LOGGER.info("Finding Post {} from Neighborhood {}", postId, neighborhoodId);

        return postDao.findPost(neighborhoodId, postId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPosts(long neighborhoodId, Long userId, Long channelId, List<Long> tagIds, Long postStatusId, int page, int size) {
        LOGGER.info("Getting Posts with status {} made on Channel {} with Tags {} by User {} from Neighborhood {} ", postStatusId, channelId, tagIds, userId, neighborhoodId);

        return postDao.getPosts(neighborhoodId, userId, channelId, tagIds, postStatusId, page, size);
    }

    @Override
    public int countPosts(long neighborhoodId, Long userId, Long channelId, List<Long> tagIds, Long postStatusId) {
        LOGGER.info("Counting Posts with status {} made on Channel {} with Tags {} by User {} from Neighborhood {} ", postStatusId, channelId, tagIds, userId, neighborhoodId);

        return postDao.countPosts(neighborhoodId, userId, channelId, tagIds, postStatusId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deletePost(long neighborhoodId, long postId) {
        LOGGER.info("Deleting Post {} from Neighborhood {}", postId, neighborhoodId);

        Set<Tag> tags = postDao.findPost(neighborhoodId, postId).orElseThrow(NotFoundException::new).getTags();
        if (tags != null)
            for (Tag tag : tags)
                categorizationDao.deleteCategorization(postId, tag.getTagId());

        return postDao.deletePost(neighborhoodId, postId);
    }
}

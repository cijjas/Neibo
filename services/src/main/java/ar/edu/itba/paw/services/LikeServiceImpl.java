package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.LikeDao;
import ar.edu.itba.paw.interfaces.services.LikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikeServiceImpl implements LikeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LikeServiceImpl.class);
    private final LikeDao likeDao;

    @Autowired
    public LikeServiceImpl(final LikeDao likeDao) {
        this.likeDao = likeDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void addLikeToPost(long postId, long userId) {
        LOGGER.info("Liking Post {} due to User {}", postId, userId);
        likeDao.createLike(postId, userId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public boolean isPostLiked(long postId, long userId) {
        LOGGER.info("Checking if User {} has liked Post {}", userId, postId);
        return likeDao.isPostLiked(postId, userId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void removeLikeFromPost(long postId, long userId) {
        LOGGER.info("Removing Like from Post {} given by User {}", postId, userId);
        likeDao.deleteLike(postId, userId);
    }
}
package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.LikeDao;
import ar.edu.itba.paw.interfaces.services.LikeService;
import ar.edu.itba.paw.models.Entities.Like;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LikeServiceImpl implements LikeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LikeServiceImpl.class);

    private final LikeDao likeDao;

    @Autowired
    public LikeServiceImpl(LikeDao likeDao) {
        this.likeDao = likeDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Like createLike(long userId, long postId) {
        LOGGER.info("Creating Like for Post {} by User {}", postId, userId);

        return likeDao.createLike(userId, postId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Like> getLikes(long neighborhoodId, Long userId, Long postId, int page, int size) {
        LOGGER.info("Getting Likes for Post {} by User {} in Neighborhood {}", postId, userId, neighborhoodId);

        return likeDao.getLikes(neighborhoodId, userId, postId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int countLikes(long neighborhoodId, Long userId, Long postId) {
        LOGGER.info("Counting Likes for Post {} by User {}", userId, postId);

        return likeDao.countLikes(neighborhoodId, userId, postId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteLike(Long userId, Long postId) {
        LOGGER.info("Removing Like from Post {} given by User {}", postId, userId);

        return likeDao.deleteLike(userId, postId);
    }
}
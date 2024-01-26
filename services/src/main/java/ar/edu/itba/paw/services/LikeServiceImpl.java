package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.LikeDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.services.LikeService;
import ar.edu.itba.paw.models.Entities.Like;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LikeServiceImpl implements LikeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LikeServiceImpl.class);
    private final LikeDao likeDao;
    private final NeighborhoodDao neighborhoodDao;

    @Autowired
    public LikeServiceImpl(final LikeDao likeDao, final NeighborhoodDao neighborhoodDao) {
        this.likeDao = likeDao;
        this.neighborhoodDao = neighborhoodDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Like createLike(long postId, long userId) {
        LOGGER.info("Creating Like for Post {} by User {}", postId, userId);

        return likeDao.createLike(postId, userId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public List<Like> getLikes(long neighborhoodId, Long postId, Long userId, int page, int size){
        LOGGER.info("Getting Likes for Post {} by User {} from Neighborhood {}", postId, userId, neighborhoodId);

        ValidationUtils.checkLikeIds(postId, userId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPageAndSize(page, size);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return likeDao.getLikes(postId, userId, neighborhoodId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPostLiked(long postId, long userId) {
        LOGGER.info("Checking a Like from User {} to Post {} exists", userId, postId);

        ValidationUtils.checkLikeIds(postId, userId);

        return likeDao.isPostLiked(postId, userId);
    }

    // -----------------------------------------------------------------------------------------------------------------


    @Override
    public int countLikes(long neighborhoodId, Long postId, Long userId) {
        LOGGER.info("Counting Likes for Post {} by User {} from Neighborhood {}", userId, postId, neighborhoodId);

        ValidationUtils.checkLikeIds(postId, userId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return likeDao.countLikes(postId, userId, neighborhoodId);
    }

    @Override
    public int calculateLikePages(long neighborhoodId, Long postId, Long userId, int size) {
        LOGGER.info("Calculating Like Pages for Post {} by User {} from Neighborhood {}", userId, postId, neighborhoodId);

        ValidationUtils.checkLikeIds(postId, userId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(likeDao.countLikes(postId, userId, neighborhoodId), size);
    }


    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteLike(long postId, long userId) {
        LOGGER.info("Removing Like from Post {} given by User {}", postId, userId);

        ValidationUtils.checkLikeIds(postId, userId);

        return likeDao.deleteLike(postId, userId);
    }
}
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
import java.util.Optional;

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
    public Like createLike(long postId, long userId) {
        LOGGER.info("Liking Post {} due to User {}", postId, userId);

        return likeDao.createLike(postId, userId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Optional<Like> findLike(long likeId) {
        LOGGER.info("Finding Like with Id {}", likeId);

        ValidationUtils.checkLikeId(likeId);

        return likeDao.findLike(likeId);
    }

    @Override
    public List<Like> getLikes(long neighborhoodId, long postId, long userId, int page, int size){
        ValidationUtils.checkNegativeLikeIds(postId, userId);
        ValidationUtils.checkPageAndSize(page, size);

        return likeDao.getLikes(postId, userId, neighborhoodId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPostLiked(long postId, long userId) {
        LOGGER.info("Checking if User {} has liked Post {}", userId, postId);

        ValidationUtils.checkLikeIds(postId, userId);

        return likeDao.isPostLiked(postId, userId);
    }

    // -----------------------------------------------------------------------------------------------------------------


    @Override
    public int countLikes(long neighborhoodId, long postId, long userId) {
        return likeDao.countLikes(postId, userId, neighborhoodId);
    }

    @Override
    public int calculateLikePages(long neighborhoodId, long postId, long userId, int size) {

        ValidationUtils.checkNegativeLikeIds(postId, userId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(likeDao.countLikes(postId, userId, neighborhoodId), size);
    }


    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void deleteLike(long postId, long userId) {
        LOGGER.info("Removing Like from Post {} given by User {}", postId, userId);

        ValidationUtils.checkLikeIds(postId, userId);

        likeDao.deleteLike(postId, userId);
    }
}
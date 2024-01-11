package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.LikeDao;
import ar.edu.itba.paw.interfaces.services.LikeService;
import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.models.Entities.User;
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
    public Like addLikeToPost(long postId, long userId) {
        LOGGER.info("Liking Post {} due to User {}", postId, userId);
        return likeDao.createLike(postId, userId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public boolean isPostLiked(long postId, long userId) {
        LOGGER.info("Checking if User {} has liked Post {}", userId, postId);
        return likeDao.isPostLiked(postId, userId);
    }

    @Override
    public Optional<Like> findLikeById(long likeId) {
        LOGGER.info("Finding Like with Id {}", likeId);
        return likeDao.findLikeById(likeId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void removeLikeFromPost(long postId, long userId) {
        LOGGER.info("Removing Like from Post {} given by User {}", postId, userId);
        likeDao.deleteLike(postId, userId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public List<Like> getLikesByCriteria(long neighborhoodId, long postId, long userId, int page, int size){
        if (userId == 0 && postId == 0) {
            return likeDao.getLikesByNeighborhood(neighborhoodId, page, size);
        }
        else if (userId > 0) {
            return likeDao.getLikesByUser(userId, page, size);
        } else if (postId > 0) {
            return likeDao.getLikesByPost(postId, page, size);
        } else {
            throw new NotFoundException("Invalid combination of parameters.");
        }
    }

    @Override
    public int getTotalLikePagesByCriteria(long neighborhoodId, long postId, long userId, int size) {
        if (userId == 0 && postId == 0) {
            return (int) Math.ceil((double) likeDao.getLikesByNeighborhoodCount(neighborhoodId) / size);
        }
        else if (userId > 0) {
            return (int) Math.ceil((double) likeDao.getLikesByUserCount(userId) / size);
        } else if (postId > 0) {
            return (int) Math.ceil((double) likeDao.getLikesByPostCount(postId) / size);
        } else {
            throw new NotFoundException("Invalid combination of parameters.");
        }
    }

}
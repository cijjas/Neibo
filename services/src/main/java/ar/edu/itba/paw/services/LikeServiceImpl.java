package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.LikeDao;
import ar.edu.itba.paw.interfaces.services.LikeService;
import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.models.TwoIds;
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
    public LikeServiceImpl(final LikeDao likeDao) {
        this.likeDao = likeDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Like createLike(String postURN, String userURN) {
        LOGGER.info("Creating Like for Post {} by User {}", postURN, userURN);

        TwoIds twoIds = ValidationUtils.extractTwoURNIds(postURN);
        long neighborhoodId = twoIds.getFirstId();
        long postId = twoIds.getSecondId();
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPostId(postId);

        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN); // cant be null, the form verifies that

        return likeDao.createLike(postId, userId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public List<Like> getLikes(String postURN, String userURN, int page, int size) {
        LOGGER.info("Getting Likes for Post {} by User {}", postURN, userURN);

        Long postId = ValidationUtils.checkURNAndExtractPostId(postURN);
        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN);

        ValidationUtils.checkPageAndSize(page, size);

        return likeDao.getLikes(postId, userId, page, size);
    }

    // ---------------------------------------------------

    @Override
    public int countLikes(String postURN, String userURN) {
        LOGGER.info("Counting Likes for Post {} by User {}", userURN, postURN);


        Long postId = ValidationUtils.checkURNAndExtractPostId(postURN);
        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN);

        return likeDao.countLikes(postId, userId);
    }

    @Override
    public int calculateLikePages(String postURN, String userURN, int size) {
        LOGGER.info("Calculating Like Pages for Post {} by User {}", userURN, postURN);

        Long postId = ValidationUtils.checkURNAndExtractPostId(postURN);
        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN);

        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(likeDao.countLikes(postId, userId), size);
    }


    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteLike(String postURN, String userURN) {
        LOGGER.info("Removing Like from Post {} given by User {}", postURN, userURN);

        if (postURN == null || userURN == null)
            throw new IllegalArgumentException("Both the Post and the User have to be specified when deleting");

        TwoIds postTwoIds = ValidationUtils.extractTwoURNIds(postURN);
        ValidationUtils.checkNeighborhoodId(postTwoIds.getFirstId());
        ValidationUtils.checkPostId(postTwoIds.getSecondId());
        long postId = postTwoIds.getSecondId();

        TwoIds userTwoIds = ValidationUtils.extractTwoURNIds(userURN);
        ValidationUtils.checkNeighborhoodId(userTwoIds.getFirstId());
        ValidationUtils.checkUserId(userTwoIds.getSecondId());
        long userId = userTwoIds.getSecondId();

        return likeDao.deleteLike(postId, userId);
    }
}
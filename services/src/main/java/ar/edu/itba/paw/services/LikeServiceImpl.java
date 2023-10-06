package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.LikeDao;
import ar.edu.itba.paw.interfaces.persistence.SubscriptionDao;
import ar.edu.itba.paw.interfaces.services.LikeService;
import ar.edu.itba.paw.interfaces.services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {
    private final LikeDao likeDao;

    @Autowired
    public LikeServiceImpl(final LikeDao likeDao) {
        this.likeDao = likeDao;
    }


    @Override
    public void addLikeToPost(long postId, long userId) {
        likeDao.createLike(postId, userId);
    }

    @Override
    public void removeLikeFromPost(long postId, long userId) {
        likeDao.deleteLike(postId, userId);
    }
}
package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.NeighborDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

    private final PostDao postDao;

    @Autowired
    public PostServiceImpl(final PostDao postDao) {
        this.postDao = postDao;
    }
    @Override
    public Post createPost(String title, String description, long neighborId) {
        return postDao.create(title, description, neighborId);
    }
}

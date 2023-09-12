package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.NeighborDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    private final PostDao postDao;

    @Autowired
    public PostServiceImpl(final PostDao postDao) {
        this.postDao = postDao;
    }

    @Override
    public Post createPost(String title, String description, long neighborId, long channelId, byte[] imageFile) {
        return postDao.create(title, description, neighborId, channelId, imageFile);
    }

    @Override
    public List<Post> getAllPosts() {
        return postDao.getAllPosts();
    }

    @Override
    public List<Post> getAllPostsByDate(final String order) {
        return postDao.getAllPostsByDate(order);
    }

    @Override
    public List<Post> getAllPostsByTag(String tag) {
        return postDao.getAllPostsByTag(tag);
    }

    @Override
    public List<Post> getAllPostsByChannel(String channel) {
        return postDao.getAllPostsByChannel(channel);
    }

    @Override
    public Optional<Post> findPostById(final long id) {
        return postDao.findPostById(id);
    }


}

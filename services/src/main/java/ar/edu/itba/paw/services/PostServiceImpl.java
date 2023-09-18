package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {
    private final PostDao postDao;
    private final ChannelDao channelDao;

    @Autowired
    public PostServiceImpl(final PostDao postDao, final ChannelDao channelDao) {
        this.postDao = postDao;
        this.channelDao = channelDao;
    }

    @Override
    public Post createPost(String title, String description, long neighborId, long channelId, byte[] imageFile) {
        return postDao.createPost(title, description, neighborId, channelId, imageFile);
    }

    @Override
    public List<Post> getPosts(int offset, int limit) {
        return postDao.getPosts(offset, limit);
    }

    @Override
    public List<Post> getPostsByDate(final String order, int offset, int limit) {
        return postDao.getPostsByDate(order, offset, limit);
    }

    @Override
    public List<Post> getPostsByTag(String tag, int offset, int limit) {
        return postDao.getPostsByTag(tag, offset, limit);
    }

    @Override
    public List<Post> getPostsByChannel(String channel, int offset, int limit) {
        return postDao.getPostsByChannel(channel, offset, limit);
    }

    @Override
    public List<Post> getPostsByChannelAndDate(final String channel, final String order, int offset, int limit){
        return postDao.getPostsByChannelAndDate(channel, order, offset, limit);
    }

    @Override
    public Optional<Post> findPostById(final long id) {
        return postDao.findPostById(id);
    }

    @Override
    public int getTotalPostsCount(){
        return postDao.getTotalPostsCount();
    }

    @Override
    public int getTotalPostsCountInChannel(String channel){
        return postDao.getTotalPostsCountInChannel(channel);
    }

    @Override
    public int getTotalPostsCountWithTag(String tag) { return postDao.getTotalPostsCountWithTag(tag); }

    @Override
    public int getTotalPostsCountInChannelWithTag(String channel, String tag ){
        return postDao.getTotalPostsCountInChannelWithTag(channel, tag);
    }

    @Override
    public List<Post> getPostsByChannelAndDateAndTag(final String channel, final String order, final String tag, int offset, int limit){
        return postDao.getPostsByChannelAndDateAndTag(channel, order, tag, offset, limit);
    }

    @Override
    public Post createAdminPost(final String title, final String description, final long neighborId, final int channelId, final byte[] imageFile){
        return postDao.createPost(title, description, neighborId, channelId, imageFile);
    }
}

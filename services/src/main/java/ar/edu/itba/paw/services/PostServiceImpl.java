package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.NeighborService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {
    private final PostDao postDao;
    private final ChannelDao channelDao;
    private final NeighborService ns;
    private final EmailService emailService;

    @Autowired
    public PostServiceImpl(final PostDao postDao, final ChannelDao channelDao, NeighborService ns, EmailService emailService) {
        this.postDao = postDao;
        this.channelDao = channelDao;
        this.ns = ns;
        this.emailService = emailService;
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
        Post post = postDao.createPost(title, description, neighborId, channelId, imageFile);
        assert post != null;
        try {
            for(Neighbor n : ns.getNeighbors()) {
                System.out.println(n);
                Map<String, Object> vars = new HashMap<>();
                vars.put("name", n.getName());
                vars.put("postTitle", post.getTitle());
                vars.put("postPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/posts/" + post.getPostId());
                emailService.sendMessageUsingThymeleafTemplate(n.getMail(), "New Announcement", "announcement-template_en.html", vars);
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return post;
    }
}

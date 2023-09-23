package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.User;
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
    private final UserService ns;
    private final EmailService emailService;

    @Autowired
    public PostServiceImpl(final PostDao postDao, final ChannelDao channelDao, UserService ns, EmailService emailService) {
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
        return postDao.getPostsByCriteria(null, null, null, offset, limit);
    }

    @Override
    public List<Post> getPostsByDate(final String order, int offset, int limit) {
        return postDao.getPostsByCriteria(null, null, order, offset, limit);
    }

    @Override
    public List<Post> getPostsByTag(String tag, int offset, int limit) {
        return postDao.getPostsByCriteria(null, tag, null, offset, limit);
    }

    @Override
    public List<Post> getPostsByChannel(String channel, int offset, int limit) {
        return postDao.getPostsByCriteria(channel, null, null, offset, limit);
    }

    @Override
    public List<Post> getPostsByChannelAndDate(final String channel, final String order, int offset, int limit){
        return postDao.getPostsByCriteria(channel, null, order, offset, limit);
    }

    @Override
    public List<Post> getPostsByChannelAndDateAndTag(final String channel, final String tag, final String order, int offset, int limit){
        return postDao.getPostsByCriteria(channel, tag, order, offset, limit);
    }

    @Override
    public Optional<Post> findPostById(final long id) {
        return postDao.findPostById(id);
    }

    @Override
    public int getTotalPostsCount(){
        return postDao.getTotalPostsCountByCriteria(null, null);
    }

    @Override
    public int getTotalPostsCountInChannel(String channel){
        return postDao.getTotalPostsCountByCriteria(channel, null);
    }

    @Override
    public int getTotalPostsCountWithTag(String tag) {
        return postDao.getTotalPostsCountByCriteria(null, tag);
    }

    @Override
    public int getTotalPostsCountInChannelWithTag(String channel, String tag ){
        return postDao.getTotalPostsCountByCriteria(channel, tag);
    }

    @Override
    public Post createAdminPost(final String title, final String description, final long neighborId, final int channelId, final byte[] imageFile){
        Post post = postDao.createPost(title, description, neighborId, channelId, imageFile);
        assert post != null;
        try {
            for(User n : ns.getNeighbors()) {
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

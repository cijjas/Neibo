package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.Tag;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Post;
import enums.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final TagService ts;
    private final ImageService is;

    @Autowired
    public PostServiceImpl(final PostDao postDao, final ChannelDao channelDao, UserService ns, EmailService emailService, TagService ts, ImageService is) {
        this.is = is;
        this.ts = ts;
        this.postDao = postDao;
        this.channelDao = channelDao;
        this.ns = ns;
        this.emailService = emailService;
    }

    @Override
    public Post createPost(String title, String description, long neighborId, long channelId, String tags, MultipartFile imageFile) {
        Image i = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            i = is.storeImage(imageFile);
        }
        Post p = postDao.createPost(title, description, neighborId, channelId, i == null ? 0 : i.getImageId());
        ts.createTagsAndCategorizePost(p.getPostId(), tags);
        return p;
    }

    @Override
    public List<Post> getPostsByCriteria(String channel, int page, int size, SortOrder date, List<String> tags) {
        return postDao.getPostsByCriteria(channel, page, size, date, tags);
    }

    @Override
    public int getPostsCountByCriteria(String channel, List<String> tags) {
        return postDao.getPostsCountByCriteria(channel, tags);
    }

    @Override
    public int getTotalPages(String channel, int size, List<String> tags) {
        return (int) Math.ceil((double) getPostsCountByCriteria(channel, tags) / size);
    }

    @Override
    public Optional<Post> findPostById(final long id) {
        return postDao.findPostById(id);
    }

    // -----------------------------------------------------------------------

/*
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
    }*/

    // -----------------------------------------------------------------------

    @Override
    public Post createAdminPost(final long neighborhoodId, final String title, final String description, final long neighborId, final int channelId, final String tags, final MultipartFile imageFile){
        Post post = createPost(title, description, neighborId, channelId, tags,  imageFile);
        assert post != null;
        try {
            for(User n : ns.getNeighbors(neighborhoodId)) {
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

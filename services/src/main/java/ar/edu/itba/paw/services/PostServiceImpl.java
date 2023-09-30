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
    private final UserService userService;
    private final EmailService emailService;
    private final TagService tagService;
    private final ImageService imageService;

    @Autowired
    public PostServiceImpl(final PostDao postDao, final ChannelDao channelDao, UserService userService, EmailService emailService, TagService tagService, ImageService imageService) {
        this.imageService = imageService;
        this.tagService = tagService;
        this.postDao = postDao;
        this.channelDao = channelDao;
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public Post createPost(String title, String description, long neighborId, long channelId, String tags, MultipartFile imageFile) {
        Image i = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            i = imageService.storeImage(imageFile);
        }
        Post p = postDao.createPost(title, description, neighborId, channelId, i == null ? 0 : i.getImageId());
        tagService.createTagsAndCategorizePost(p.getPostId(), tags);
        return p;
    }

    @Override
    public List<Post> getPostsByCriteria(String channel, int page, int size, SortOrder date, List<String> tags, long neighborhoodId) {
        return postDao.getPostsByCriteria(channel, page, size, date, tags, neighborhoodId);
    }

    @Override
    public int getPostsCountByCriteria(String channel, List<String> tags, long neighborhoodId) {
        return postDao.getPostsCountByCriteria(channel, tags, neighborhoodId);
    }

    @Override
    public int getTotalPages(String channel, int size, List<String> tags, long neighborhoodId) {
        return (int) Math.ceil((double) getPostsCountByCriteria(channel, tags, neighborhoodId) / size);
    }

    @Override
    public Optional<Post> findPostById(final long id) {
        return postDao.findPostById(id);
    }

    @Override
    public Post createAdminPost(final long neighborhoodId, final String title, final String description, final long neighborId, final int channelId, final String tags, final MultipartFile imageFile){
        Post post = createPost(title, description, neighborId, channelId, tags,  imageFile);
        assert post != null;
        try {
            for(User n : userService.getNeighbors(neighborhoodId)) {
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

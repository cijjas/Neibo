package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.Entities.Channel;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.MainEntitiesDaos.DaoUtils.*;

@Repository
public class PostDaoImpl implements PostDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    private final String FROM_POSTS_JOIN_USERS_CHANNELS_TAGS_COMMENTS_LIKES =
            "SELECT DISTINCT p.*, channel, u.* " +
                    "FROM posts p  " +
                    "INNER JOIN users u ON p.userid = u.userid  " +
                    "INNER JOIN channels c ON p.channelid = c.channelid  " +
                    "LEFT JOIN posts_tags pt ON p.postid = pt.postid  " +
                    "LEFT JOIN tags t ON pt.tagid = t.tagid " +
                    "LEFT JOIN comments cm ON p.postid = cm.postid " +
                    "LEFT JOIN posts_users_likes pul on p.postid = pul.postId ";

    private final String COUNT_POSTS_JOIN_USERS_CHANNELS_TAGS_COMMENTS_LIKES =
            "SELECT COUNT(DISTINCT p.*) " +
                    "FROM posts p  " +
                    "INNER JOIN users u ON p.userid = u.userid  " +
                    "INNER JOIN channels c ON p.channelid = c.channelid  " +
                    "LEFT JOIN posts_tags pt ON p.postid = pt.postid  " +
                    "LEFT JOIN tags t ON pt.tagid = t.tagid " +
                    "LEFT JOIN comments cm ON p.postid = cm.postid " +
                    "LEFT JOIN posts_users_likes pul on p.postid = pul.postId ";

    // ------------------------------------------------ POSTS INSERT ---------------------------------------------------

    @Override
    public Post createPost(String title, String description, long userId, long channelId, long imageId) {
        LOGGER.debug("Inserting Post {}", title);

        Post post = new Post.Builder()
                .title(title)
                .description(description)
                .user(em.find(User.class, userId))
                .channel(em.find(Channel.class, channelId))
                .postPicture(em.find(Image.class, imageId))
                .build();
        em.persist(post);
        return post;
    }

    // ------------------------------------------------ POSTS SELECT ---------------------------------------------------

    @Override
    public Optional<Post> findPost(long postId) {
        LOGGER.debug("Selecting Post with id {}", postId);

        return Optional.ofNullable(em.find(Post.class, postId));
    }

    @Override
    public Optional<Post> findPost(long postId, long neighborhoodId) {
        LOGGER.debug("Selecting Post with postId {}, neighborhoodId {}", postId, neighborhoodId);

        TypedQuery<Post> query = em.createQuery(
                "SELECT p FROM Post p WHERE p.postId = :postId AND p.user.neighborhood.neighborhoodId = :neighborhoodId",
                Post.class
        );

        query.setParameter("postId", postId);
        query.setParameter("neighborhoodId", neighborhoodId);

        List<Post> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Post> getPosts(Long channelId, int page, int size, List<Long> tagIds, long neighborhoodId, Long postStatusId, Long userId) {
        LOGGER.debug("Selecting Post from neighborhood {}, channel {}, user {}, tags {} and status {}", neighborhoodId, channelId, userId, tagIds, postStatusId);

        StringBuilder query = new StringBuilder(FROM_POSTS_JOIN_USERS_CHANNELS_TAGS_COMMENTS_LIKES);
        List<Object> queryParams = new ArrayList<>();
        appendCommonConditions(query, queryParams, channelId, userId, neighborhoodId, tagIds, postStatusId);
        appendDateClause(query);
        if (page != 0)
            appendPaginationClause(query, queryParams, page, size);
        Query sqlQuery = em.createNativeQuery(query.toString(), Post.class);
        for (int i = 0; i < queryParams.size(); i++)
            sqlQuery.setParameter(i + 1, queryParams.get(i));
        return sqlQuery.getResultList();
    }

    @Override
    public int countPosts(Long channelId, List<Long> tagIds, long neighborhoodId, Long postStatusId, Long userId) {
        LOGGER.debug("Selecting Post Count from neighborhood {}, channel {}, user {}, tags {} and status {}", neighborhoodId, channelId, userId, tagIds, postStatusId);

        StringBuilder query = new StringBuilder(COUNT_POSTS_JOIN_USERS_CHANNELS_TAGS_COMMENTS_LIKES);
        List<Object> queryParams = new ArrayList<>();

        appendCommonConditions(query, queryParams, channelId, userId, neighborhoodId, tagIds, postStatusId);

        Query sqlQuery = em.createNativeQuery(query.toString());

        for (int i = 0; i < queryParams.size(); i++) {
            sqlQuery.setParameter(i + 1, queryParams.get(i));
        }

        Object result = sqlQuery.getSingleResult();
        return Integer.parseInt(result.toString());
    }

    // ------------------------------------------------ POSTS DELETE ---------------------------------------------------

    @Override
    public boolean deletePost(long postId) {
        LOGGER.debug("Deleting Post with id {}", postId);
        Post post = em.find(Post.class, postId);
        if (post == null) {
            return false;
        }
        em.remove(post);
        return true;
    }
}

package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.MainEntities.*;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static ar.edu.itba.paw.persistence.MainEntitiesDaos.DaoUtils.*;

@Repository
public class PostDaoImpl implements PostDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

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
    public Optional<Post> findPostById(long postId) {
        LOGGER.debug("Selecting Post with id {}", postId);
        return Optional.ofNullable(em.find(Post.class, postId));
    }

    // --------------------------------------------------- COMPLEX -----------------------------------------------------

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

    @Override
    public List<Post> getPostsByCriteria(String channel, int page, int size, List<String> tags, long neighborhoodId, PostStatus postStatus, long userId) {
        LOGGER.debug("Selecting Post from neighborhood {}, channel {}, user {}, tags {} and status {}", neighborhoodId, channel, userId, tags, postStatus);
        StringBuilder query = new StringBuilder(FROM_POSTS_JOIN_USERS_CHANNELS_TAGS_COMMENTS_LIKES);
        List<Object> queryParams = new ArrayList<>();
        appendCommonConditions(query, queryParams, channel, userId, neighborhoodId, tags, postStatus);
        appendDateClause(query);
        if (page != 0)
            appendPaginationClause(query, queryParams, page, size);
        // Create a native SQL query
        Query sqlQuery = em.createNativeQuery(query.toString(), Post.class);
        // Set query parameters
        for (int i = 0; i < queryParams.size(); i++)
            sqlQuery.setParameter(i + 1, queryParams.get(i));
        // Return the result directly as a list of Post entities
        return sqlQuery.getResultList();
    }

    @Override
    public int getPostsCountByCriteria(String channel, List<String> tags, long neighborhoodId, PostStatus postStatus, long userId) {
        LOGGER.debug("Selecting Post Count from neighborhood {}, channel {}, user {}, tags {} and status {}", neighborhoodId, channel, userId, tags, postStatus);

        StringBuilder query = new StringBuilder(COUNT_POSTS_JOIN_USERS_CHANNELS_TAGS_COMMENTS_LIKES);
        List<Object> queryParams = new ArrayList<>();

        appendCommonConditions(query, queryParams, channel, userId, neighborhoodId, tags, postStatus);

        // Create a native SQL query for counting
        Query sqlQuery = em.createNativeQuery(query.toString());

        // Set query parameters
        for (int i = 0; i < queryParams.size(); i++) {
            sqlQuery.setParameter(i + 1, queryParams.get(i));
        }

        // Execute the query and retrieve the count
        Object result = sqlQuery.getSingleResult();
        return Integer.parseInt(result.toString());
    }
}

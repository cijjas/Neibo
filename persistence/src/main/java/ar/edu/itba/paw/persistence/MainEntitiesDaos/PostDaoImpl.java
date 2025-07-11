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
            "SELECT COUNT(DISTINCT p.postid) " +
                    "FROM posts p  " +
                    "INNER JOIN users u ON p.userid = u.userid  " +
                    "INNER JOIN channels c ON p.channelid = c.channelid  " +
                    "LEFT JOIN posts_tags pt ON p.postid = pt.postid  " +
                    "LEFT JOIN tags t ON pt.tagid = t.tagid " +
                    "LEFT JOIN comments cm ON p.postid = cm.postid " +
                    "LEFT JOIN posts_users_likes pul on p.postid = pul.postId ";
    @PersistenceContext
    private EntityManager em;

    // ------------------------------------------------ POSTS INSERT ---------------------------------------------------

    @Override
    public Post createPost(long userId, String title, String description, long channelId, long imageId) {
        LOGGER.debug("Inserting Post {} with User Id {}", title, userId);

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
    public Optional<Post> findPost(long neighborhoodId, long postId) {
        LOGGER.debug("Selecting Post with Neighborhood Id {} and Post Id {}", neighborhoodId, postId);

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
    public List<Post> getPosts(long neighborhoodId, Long userId, Long channelId, List<Long> tagIds, Long postStatusId, int page, int size) {
        LOGGER.debug("Selecting Post with Neighborhood Id {}, User Id {}, Channel Id {}, Tag Ids {} and Post Status Id {}", neighborhoodId, userId, channelId, tagIds, postStatusId);

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
    public int countPosts(long neighborhoodId, Long userId, Long channelId, List<Long> tagIds, Long postStatusId) {
        LOGGER.debug("Counting Post with Neighborhood Id {}, User Id {}, Channel Id {}, Tag Ids {} and Post Status Id {}", neighborhoodId, userId, channelId, tagIds, postStatusId);

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
    public boolean deletePost(long neighborhoodId, long postId) {
        LOGGER.debug("Deleting Post with Neighborhood Id {} and Post Id {}", neighborhoodId, postId);

        int rowsAffected = em.createNativeQuery(
                        "DELETE FROM posts " +
                                "WHERE postid = :postId " +
                                "AND userid IN ( " +
                                "    SELECT userid " +
                                "    FROM users " +
                                "    WHERE neighborhoodid = :neighborhoodId " +
                                ")"
                )
                .setParameter("postId", postId)
                .setParameter("neighborhoodId", neighborhoodId)
                .executeUpdate();

        return rowsAffected > 0;
    }
}

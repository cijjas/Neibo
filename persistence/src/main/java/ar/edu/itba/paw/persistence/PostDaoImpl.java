package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.Tag;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static ar.edu.itba.paw.persistence.DaoUtils.*;

@Repository
public class PostDaoImpl implements PostDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String POSTS_JOIN_USERS_JOIN_CHANNELS =
            "SELECT DISTINCT p.*, channel, u.* " +
                    "FROM posts p  " +
                    "INNER JOIN users u ON p.userid = u.userid  " +
                    "INNER JOIN channels c ON p.channelid = c.channelid  " +
                    "LEFT JOIN posts_tags pt ON p.postid = pt.postid  " +
                    "LEFT JOIN tags t ON pt.tagid = t.tagid ";
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
    private ChannelDao channelDao;
    private TagDao tagDao;
    private UserDao userDao;
    private LikeDao likeDao;
    private final RowMapper<Post> ROW_MAPPER = (rs, rowNum) -> {
        List<Tag> tags = tagDao.findTagsByPostId(rs.getLong("postid"));

        return new Post.Builder()
                .postId(rs.getLong("postid"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .date(rs.getTimestamp("postdate"))
                .postPictureId(rs.getLong("postpictureid"))
                .tags(tags)
                .user(
                        new User.Builder()
                                .userId(rs.getLong("userid"))
                                .mail(rs.getString("mail"))
                                .name(rs.getString("name"))
                                .surname(rs.getString("surname"))
                                .password(rs.getString("password"))
                                .neighborhoodId(rs.getLong("neighborhoodid"))
                                .creationDate(rs.getDate("creationdate"))
                                .darkMode(rs.getBoolean("darkmode"))
                                .profilePictureId(rs.getLong("profilepictureid"))
                                .language(rs.getString("language") != null ? Language.valueOf(rs.getString("language")) : null)
                                .role(rs.getString("role") != null ? UserRole.valueOf(rs.getString("role")) : null)
                                .build()
                )
                .channel(
                        new Channel.Builder()
                                .channelId(rs.getLong("channelid"))
                                .channel(rs.getString("channel"))
                                .build()
                )
                .likes(likeDao.getLikes(rs.getLong("postid")))
                .build();
    };

    // ------------------------------------------------ POSTS INSERT ---------------------------------------------------

    @Autowired
    public PostDaoImpl(final DataSource ds,
                       ChannelDao channelDao,
                       UserDao userDao,
                       TagDao tagDao,
                       LikeDao likeDao
    ) {
        this.userDao = userDao;
        this.channelDao = channelDao;
        this.tagDao = tagDao;
        this.likeDao = likeDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("postid")
                .withTableName("posts");
    }

    // ------------------------------------------------ POSTS SELECT ---------------------------------------------------

    @Override
    public Post createPost(String title, String description, long userid, long channelId, long imageId) {
        LOGGER.debug("Inserting Post {}", title);
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("description", description);
        data.put("postdate", Timestamp.valueOf(LocalDateTime.now()));
        data.put("userid", userid);
        data.put("postPictureId", imageId == 0 ? null : imageId);
        data.put("channelid", channelId);

        try {
            final Number key = jdbcInsert.executeAndReturnKey(data);
            return new Post.Builder()
                    .postId(key.longValue())
                    .title(title)
                    .postPictureId(imageId)
                    .description(description)
                    .build();
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Post", ex);
            throw new InsertionException("An error occurred whilst creating the Post");
        }
    }

    @Override
    public Optional<Post> findPostById(long id) {
        LOGGER.debug("Selecting Post with id {}", id);
        final List<Post> postList = jdbcTemplate.query(POSTS_JOIN_USERS_JOIN_CHANNELS + " where p.postid=?;", ROW_MAPPER, id);
        return postList.isEmpty() ? Optional.empty() : Optional.of(postList.get(0));
    }


    // --------------------------------------------------- COMPLEX -----------------------------------------------------

    @Override
    public List<Post> getPostsByCriteria(String channel, int page, int size, List<String> tags, long neighborhoodId, PostStatus postStatus, long userId) {
        LOGGER.debug("Selecting Post from neighborhood {}, channel {}, user {}, tags {} and status {}", neighborhoodId, channel, userId, tags, postStatus);
        // BASE QUERY
        StringBuilder query = new StringBuilder(FROM_POSTS_JOIN_USERS_CHANNELS_TAGS_COMMENTS_LIKES);
        // PARAMS
        List<Object> queryParams = new ArrayList<>();

        appendCommonConditions(query, queryParams, channel, userId, neighborhoodId, tags, postStatus);

        appendDateClause(query);

        if (page != 0) {
            appendPaginationClause(query, queryParams, page, size);
        }

        // Log results
        LOGGER.debug("{}", query);
        LOGGER.debug("{}", queryParams);

        // LAUNCH IT!
        return jdbcTemplate.query(query.toString(), ROW_MAPPER, queryParams.toArray());
    }

    @Override
    public int getPostsCountByCriteria(String channel, List<String> tags, long neighborhoodId, PostStatus postStatus, long userId) {
        LOGGER.debug("Selecting Post Count from neighborhood {}, channel {}, user {}, tags {} and status {}", neighborhoodId, channel, userId, tags, postStatus);
        // Create the base SQL query string for counting
        StringBuilder query = new StringBuilder(COUNT_POSTS_JOIN_USERS_CHANNELS_TAGS_COMMENTS_LIKES);

        // Create a list to hold query parameters
        List<Object> queryParams = new ArrayList<>();

        appendCommonConditions(query, queryParams, channel, userId, neighborhoodId, tags, postStatus);

        // Log results
        LOGGER.debug("{}", query);
        LOGGER.debug("{}", queryParams);

        // Execute the query and retrieve the count
        return jdbcTemplate.queryForObject(query.toString(), Integer.class, queryParams.toArray());
    }
}

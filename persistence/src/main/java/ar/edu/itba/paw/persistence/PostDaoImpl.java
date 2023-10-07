package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Tag;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Post;
import enums.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class PostDaoImpl implements PostDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private ChannelDao channelDao;
    private TagDao tagDao;
    private UserDao userDao;
    private LikeDao likeDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(PostDaoImpl.class);

    private final String POSTS_JOIN_USERS_JOIN_CHANNELS =
            "SELECT DISTINCT p.* " +
            "FROM posts p  " +
                    "JOIN users u ON p.userid = u.userid  " +
                    "JOIN channels c ON p.channelid = c.channelid  " +
                    "LEFT JOIN posts_tags pt ON p.postid = pt.postid  " +
                    "LEFT JOIN tags t ON pt.tagid = t.tagid ";

    private final String POSTS_JOIN_USERS_CHANNELS_TAGS_COMMENTS_LIKES =
        "SELECT DISTINCT p.* " +
        "FROM posts p  " +
                "JOIN users u ON p.userid = u.userid  " +
                "JOIN channels c ON p.channelid = c.channelid  " +
                "LEFT JOIN posts_tags pt ON p.postid = pt.postid  " +
                "LEFT JOIN tags t ON pt.tagid = t.tagid " +
                "LEFT JOIN comments cm ON p.postid = cm.postid " +
                "LEFT JOIN posts_users_likes pul on p.postid = pul.postId ";
    private final String COUNT_POSTS_JOIN_USERS_CHANNELS_TAGS_COMMENTS_LIKES =
        "SELECT COUNT(DISTINCT p.*) " +
        "FROM posts p  " +
                "JOIN users u ON p.userid = u.userid  " +
                "JOIN channels c ON p.channelid = c.channelid  " +
                "LEFT JOIN posts_tags pt ON p.postid = pt.postid  " +
                "LEFT JOIN tags t ON pt.tagid = t.tagid " +
                "LEFT JOIN comments cm ON p.postid = cm.postid " +
                "LEFT JOIN posts_users_likes pul on p.postid = pul.postId ";


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

    // ------------------------------------------------ POSTS INSERT ---------------------------------------------------

    @Override
    public Post createPost(String title, String description, long userid, long channelId, long imageId) {
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

    // ------------------------------------------------ POSTS SELECT ---------------------------------------------------

    private final RowMapper<Post> ROW_MAPPER = (rs, rowNum) -> {
        User user = userDao.findUserById(rs.getLong("userid")).orElse(null);
        Channel channel = channelDao.findChannelById(rs.getLong("channelid")).orElse(null);
        List<Tag> tags = tagDao.findTagsByPostId(rs.getLong("postid")).orElse(null);

        return new Post.Builder()
                .postId(rs.getLong("postid"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .date(rs.getTimestamp("postdate"))
                .postPictureId(rs.getLong("postpictureid"))
                .tags(tags)
                .user(user)
                .channel(channel)
                .likes(likeDao.getLikes(rs.getLong("postid")))
                .build();
    };

    @Override
    public Optional<Post> findPostById(long id) {
        final List<Post> postList = jdbcTemplate.query(POSTS_JOIN_USERS_JOIN_CHANNELS + " where p.postid=?;", ROW_MAPPER, id);
        return postList.isEmpty() ? Optional.empty() : Optional.of(postList.get(0));
    }



    // --------------------------------------------------- COMPLEX -----------------------------------------------------

    @Override
    public List<Post> getPostsByCriteria(String channel, int page, int size, SortOrder date, List<String> tags, long neighborhoodId, boolean hot) {
        // BASE QUERY
        StringBuilder query = new StringBuilder(POSTS_JOIN_USERS_CHANNELS_TAGS_COMMENTS_LIKES);
        // PARAMS
        List<Object> queryParams = new ArrayList<>();

        // Append the WHERE so we can freely stack conditions
        query.append(" WHERE 1 = 1");

        // Append channel condition, "Administracion", "Feed", etc
        if (channel != null && !channel.isEmpty()) {
            query.append(" AND channel LIKE ?");
            queryParams.add(channel);
        }

        // Append neighborhoodId condition, 1, 2 or 3 representing Olivos Golf Club, Pacheco Golf or Martindale for example
        query.append(" AND u.neighborhoodid = ?");
        queryParams.add(neighborhoodId);

        // Append multiple tags conditions
        if (tags != null && !tags.isEmpty()) {
            query.append(" AND EXISTS (");
            query.append("SELECT 1 FROM posts_tags pt JOIN tags t ON pt.tagid = t.tagid");
            query.append(" WHERE pt.postid = p.postid AND t.tag IN (");
            for (int i = 0; i < tags.size(); i++) {
                query.append("?");
                queryParams.add(tags.get(i)); // Use the tag name as a string
                if (i < tags.size() - 1) {
                    query.append(", ");
                }
            }
            query.append(")");
            query.append(" HAVING COUNT(DISTINCT pt.tagid) = ?)"); // Ensure all specified tags exist
            queryParams.add(tags.size());
        }

        // Hot Filter, what is considered hot is statically defined as having more than 2 likes and more than 2 comments in the last 72 hours
        // this values could be obtained with an average or a percentile from a standard distribution
        if ( hot ){
            // Append the hot post conditions for likes
            query.append(" AND (SELECT COUNT(userid) FROM posts_users_likes as pul WHERE pul.postid = p.postid AND pul.likedate >= NOW() - INTERVAL '72 HOURS') >= 1");

            // Append the hot post conditions for comments
            query.append(" AND (SELECT COUNT(commentid) FROM comments as cm WHERE cm.postid = p.postid AND cm.commentdate >= NOW() - INTERVAL '72 HOURS') >= 1");
        }


        // Append the ORDER BY clause
        query.append(" ORDER BY postdate ").append(date);

        if (page != 0) {
            // Calculate the offset based on the page and size
            int offset = (page - 1) * size;
            // Append the LIMIT and OFFSET clauses for pagination
            query.append(" LIMIT ? OFFSET ?");
            queryParams.add(size);
            queryParams.add(offset);
        }

        // Log results
        LOGGER.info(String.valueOf(query));
        LOGGER.info(String.valueOf(queryParams));

        // LAUNCH IT!
        return jdbcTemplate.query(query.toString(), ROW_MAPPER, queryParams.toArray());
    }

    @Override
    public int getPostsCountByCriteria(String channel, List<String> tags, long neighborhoodId, boolean hot) {
        // Create the base SQL query string for counting
        StringBuilder query = new StringBuilder(COUNT_POSTS_JOIN_USERS_CHANNELS_TAGS_COMMENTS_LIKES);

        // Create a list to hold query parameters
        List<Object> queryParams = new ArrayList<>();

        // Append conditions based on the provided parameters
        query.append(" WHERE 1 = 1");

        if (channel != null && !channel.isEmpty()) {
            query.append(" AND c.channel LIKE ?");
            queryParams.add("%" + channel + "%");
        }

        // Append the neighborhoodId condition
        query.append(" AND u.neighborhoodid = ?");
        queryParams.add(neighborhoodId);

        // Append multiple tags conditions
        if (tags != null && !tags.isEmpty()) {
            query.append(" AND EXISTS (");
            query.append("SELECT 1 FROM posts_tags pt JOIN tags t ON pt.tagid = t.tagid");
            query.append(" WHERE pt.postid = p.postid AND t.tag IN (");
            for (int i = 0; i < tags.size(); i++) {
                query.append("?");
                queryParams.add(tags.get(i)); // Use the tag name as a string
                if (i < tags.size() - 1) {
                    query.append(", ");
                }
            }
            query.append(")");
            query.append(" HAVING COUNT(DISTINCT pt.tagid) = ?)"); // Ensure all specified tags exist
            queryParams.add(tags.size());
        }

        // This could become something more complex, that uses an enum for hot type, hot comment-wise, like-wise, or both-wise
        if ( hot ){
            // Append the hot post conditions for likes
            query.append(" AND (SELECT COUNT(userid) FROM posts_users_likes as pul WHERE pul.postid = p.postid AND pul.likedate >= NOW() - INTERVAL '72 HOURS') >= 1");

            // Append the hot post conditions for comments
            query.append(" AND (SELECT COUNT(commentid) FROM comments as cm WHERE cm.postid = p.postid AND cm.commentdate >= NOW() - INTERVAL '72 HOURS') >= 1");
        }

        // Log results
        LOGGER.info(String.valueOf(query));
        LOGGER.info(String.valueOf(queryParams));

        // Execute the query and retrieve the count
        return jdbcTemplate.queryForObject(query.toString(), Integer.class, queryParams.toArray());
    }


    // ------------------------------------------------- EARLY VERSIONS ---------------------------------------------------

    @Override
    public List<Post> getPostsByCriteria(String channel, int page, int size, SortOrder date, List<String> tags, long neighborhoodId) {
        // Create the base SQL query string
        StringBuilder query = new StringBuilder(POSTS_JOIN_USERS_JOIN_CHANNELS);

        // Create a list to hold query parameters
        List<Object> queryParams = new ArrayList<>();

        // Append the WHERE clause to start building the query
        query.append(" WHERE 1 = 1");

        // Append conditions based on the provided parameters
        if (channel != null && !channel.isEmpty()) {
            query.append(" AND channel LIKE ?");
            queryParams.add(channel);
        }

        // Append the neighborhoodId condition
        query.append(" AND u.neighborhoodid = ?");
        queryParams.add(neighborhoodId);

        if (tags != null && !tags.isEmpty()) {
            query.append(" AND EXISTS (");
            query.append("SELECT 1 FROM posts_tags pt JOIN tags t ON pt.tagid = t.tagid");
            query.append(" WHERE pt.postid = p.postid AND t.tag IN (");
            for (int i = 0; i < tags.size(); i++) {
                query.append("?");
                queryParams.add(tags.get(i)); // Use the tag name as a string
                if (i < tags.size() - 1) {
                    query.append(", ");
                }
            }
            query.append(")");
            query.append(" HAVING COUNT(DISTINCT pt.tagid) = ?)"); // Ensure all specified tags exist
            queryParams.add(tags.size());
        }

        // Append the ORDER BY clause
        query.append(" ORDER BY postdate ").append(date);

        if (page != 0) {
            // Calculate the offset based on the page and size
            int offset = (page - 1) * size;
            // Append the LIMIT and OFFSET clauses for pagination
            query.append(" LIMIT ? OFFSET ?");
            queryParams.add(size);
            queryParams.add(offset);
        }

        return jdbcTemplate.query(query.toString(), ROW_MAPPER, queryParams.toArray());
    }

    @Override
    public int getPostsCountByCriteria(String channel, List<String> tags, long neighborhoodId) {
        // Create the base SQL query string for counting
        StringBuilder query = new StringBuilder(COUNT_POSTS_JOIN_USERS_CHANNELS_TAGS_COMMENTS_LIKES);

        // Create a list to hold query parameters
        List<Object> queryParams = new ArrayList<>();

        // Append conditions based on the provided parameters
        query.append(" WHERE 1 = 1");

        if (channel != null && !channel.isEmpty()) {
            query.append(" AND c.channel LIKE ?");
            queryParams.add("%" + channel + "%");
        }

        // Append the neighborhoodId condition
        query.append(" AND u.neighborhoodid = ?");
        queryParams.add(neighborhoodId);


        if (tags != null && !tags.isEmpty()) {
            query.append(" AND EXISTS (");
            query.append("SELECT 1 FROM posts_tags pt JOIN tags t ON pt.tagid = t.tagid");
            query.append(" WHERE pt.postid = p.postid AND t.tag IN (");
            for (int i = 0; i < tags.size(); i++) {
                query.append("?");
                queryParams.add(tags.get(i)); // Use the tag name as a string
                if (i < tags.size() - 1) {
                    query.append(", ");
                }
            }
            query.append(")");
            query.append(" HAVING COUNT(DISTINCT pt.tagid) = ?)"); // Ensure all specified tags exist
            queryParams.add(tags.size());
        }

        // Execute the query and retrieve the result
        return jdbcTemplate.queryForObject(query.toString(), Integer.class, queryParams.toArray());
    }


}

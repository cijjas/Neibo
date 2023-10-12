package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Tag;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Post;
import enums.PostStatus;
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

    private final String FROM_POSTS_JOIN_USERS_CHANNELS_TAGS_COMMENTS_LIKES =
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
        User user = userDao.findUserById(rs.getLong("userid")).orElseThrow(() -> new NotFoundException("User not Found"));
        Channel channel = channelDao.findChannelById(rs.getLong("channelid")).orElseThrow(()-> new NotFoundException("Channel not Found"));
        List<Tag> tags = tagDao.findTagsByPostId(rs.getLong("postid"));

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
    public List<Post> getPostsByCriteria(String channel, int page, int size, List<String> tags, long neighborhoodId, PostStatus postStatus, long userId) {
        // BASE QUERY
        StringBuilder query = new StringBuilder(FROM_POSTS_JOIN_USERS_CHANNELS_TAGS_COMMENTS_LIKES);
        // PARAMS
        List<Object> queryParams = new ArrayList<>();

        appendCommonConditions(query, queryParams, channel, userId, neighborhoodId, tags);

        if (page != 0)
            appendPaginationClause(query, queryParams, page, size);

        // Log results
        LOGGER.info("{}", query);
        LOGGER.info("{}", queryParams);

        // LAUNCH IT!
        return jdbcTemplate.query(query.toString(), ROW_MAPPER, queryParams.toArray());
    }

    @Override
    public int getPostsCountByCriteria(String channel, List<String> tags, long neighborhoodId, PostStatus postStatus, long userId) {
        // Create the base SQL query string for counting
        StringBuilder query = new StringBuilder(COUNT_POSTS_JOIN_USERS_CHANNELS_TAGS_COMMENTS_LIKES);

        // Create a list to hold query parameters
        List<Object> queryParams = new ArrayList<>();

        appendCommonConditions(query, queryParams, channel, userId, neighborhoodId, tags);

        // Log results
        LOGGER.info("{}", query);
        LOGGER.info("{}", queryParams);

        // Execute the query and retrieve the count
        return jdbcTemplate.queryForObject(query.toString(), Integer.class, queryParams.toArray());
    }

    // -----------------------------------------------------------------------------------------------------------------
    // ------------------------------------------------- HELPER METHODS ------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------


    private void appendCommonConditions(StringBuilder query, List<Object> queryParams, String channel, long userId, long neighborhoodId, List<String> tags) {
        appendInitialWhereClause(query);
        appendNeighborhoodIdCondition(query, queryParams, neighborhoodId);

        if (channel != null && !channel.isEmpty())
            appendChannelCondition(query, queryParams, channel);

        if (userId != 0)
            appendUserIdCondition(query, queryParams, userId);

        if (tags != null && !tags.isEmpty())
            appendTagsCondition(query, queryParams, tags);
    }

    private void appendInitialWhereClause(StringBuilder query) {
        query.append(" WHERE 1 = 1");
    }

    private void appendChannelCondition(StringBuilder query, List<Object> queryParams, String channel) {
        query.append(" AND channel LIKE ?");
        queryParams.add(channel);
    }

    private void appendUserIdCondition(StringBuilder query, List<Object> queryParams, long userId) {
        query.append(" AND u.userid = ?");
        queryParams.add(userId);
    }

    private void appendNeighborhoodIdCondition(StringBuilder query, List<Object> queryParams, long neighborhoodId) {
        query.append(" AND u.neighborhoodid = ?");
        queryParams.add(neighborhoodId);
    }

    private void appendTagsCondition(StringBuilder query, List<Object> queryParams, List<String> tags) {
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

    private void appendPaginationClause(StringBuilder query, List<Object> queryParams, int page, int size) {
        // Calculate the offset based on the page and size
        int offset = (page - 1) * size;
        query.append(" LIMIT ? OFFSET ?");
        queryParams.add(size);
        queryParams.add(offset);
    }

}

package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Post;
import enums.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class PostDaoImpl implements PostDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private ChannelDao channelDao;
    private UserDao userDao;

    private final String POSTS_JOIN_USERS_AND_CHANNELS =
        "SELECT DISTINCT p.* " +
        "FROM posts p  JOIN users u ON p.userid = u.userid  JOIN channels c ON p.channelid = c.channelid  LEFT JOIN posts_tags pt ON p.postid = pt.postid  LEFT JOIN tags t ON pt.tagid = t.tagid ";
    private final String COUNT_POSTS_JOIN_TAGS_AND_CHANNELS =
        "SELECT COUNT(*) \n" +
        "FROM posts p JOIN posts_tags pt ON p.postid = pt.postid JOIN tags t ON pt.tagid = t.tagid  JOIN channels c on c.channelid = p.channelid ";


    @Autowired
    public PostDaoImpl(final DataSource ds,
                       ChannelDao channelDao,
                       UserDao userDao
                       ) {
        this.userDao = userDao;
        this.channelDao = channelDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("postid")
                .withTableName("posts");
    }

    @Override
    public Post createPost(String title, String description, long userid, long channelId, byte[] imageFile) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("description", description);
        data.put("postdate", Timestamp.valueOf(LocalDateTime.now()));
        data.put("userid", userid);
        data.put("channelid", channelId);
        data.put("postimage", imageFile);

        final Number key = jdbcInsert.executeAndReturnKey(data);
        return new Post.Builder()
                .postId(key.longValue())
                .title(title)
                .description(description)
                .build();
    }

    private final RowMapper<Post> ROW_MAPPER = (rs, rowNum) -> {
        User user = userDao.findUserById(rs.getLong("userid")).orElse(null);
        Channel channel = channelDao.findChannelById(rs.getLong("channelid")).orElse(null);

        return new Post.Builder()
                .postId(rs.getLong("postid"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .date(rs.getTimestamp("postdate"))
                .postPictureId(rs.getLong("postpictureid"))
                .user(user)
                .channel(channel)
                .build();
    };

    @Override
    public Optional<Post> findPostById(long id) {
        final List<Post> postList = jdbcTemplate.query(POSTS_JOIN_USERS_AND_CHANNELS + " where p.postid=?;", ROW_MAPPER, id);
        return postList.isEmpty() ? Optional.empty() : Optional.of(postList.get(0));
    }

    @Override
    public List<Post> getPostsByCriteria(String channel, int page, int size, SortOrder date, List<String> tags) {
        if ( page < 1 )
            return null;

        // Calculate the offset based on the page and size
        int offset = (page - 1) * size;

        // Create the base SQL query string
        StringBuilder query = new StringBuilder(POSTS_JOIN_USERS_AND_CHANNELS);

        // Create a list to hold query parameters
        List<Object> queryParams = new ArrayList<>();

        // Append the WHERE clause to start building the query
        query.append(" WHERE 1 = 1");

        // Append conditions based on the provided parameters
        if (channel != null && !channel.isEmpty()) {
            query.append(" AND channel LIKE ?");
            queryParams.add(channel);
        }

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


        // Append the LIMIT and OFFSET clauses for pagination
        query.append(" LIMIT ? OFFSET ?");
        queryParams.add(size);
        queryParams.add(offset);


        return jdbcTemplate.query(query.toString(), ROW_MAPPER, queryParams.toArray());
    }


    @Override
    public int getPostsCountByCriteria(String channel, List<String> tags) {
        // Create the base SQL query string for counting
        StringBuilder query = new StringBuilder(COUNT_POSTS_JOIN_TAGS_AND_CHANNELS);

        // Create a list to hold query parameters
        List<Object> queryParams = new ArrayList<>();

        // Append conditions based on the provided parameters
        query.append(" WHERE 1 = 1");

        if (channel != null && !channel.isEmpty()) {
            query.append(" AND c.channel LIKE ?");
            queryParams.add("%" + channel + "%");
        }

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

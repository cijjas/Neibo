package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Post;
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
        "FROM posts JOIN posts_tags ON posts.postid = posts_tags.postid JOIN tags ON posts_tags.tagid = tags.tagid  JOIN public.channels c on c.channelid = posts.channelid ";


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
        System.out.println("CREATING NEW POST");
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
                .imageFile(imageFile)
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
                .imageFile(rs.getBytes("postimage"))
                .user(user)
                .channel(channel)
                .build();
    };

    public List<Post> getPostsByCriteria(
            String channel,  // Channel filter
            String tag,      // Tag filter
            String order,    // Sorting order (e.g., "asc" or "desc")
            int offset,      // Offset for pagination
            int limit        // Limit for pagination
    ) {
        StringBuilder query = new StringBuilder(POSTS_JOIN_USERS_AND_CHANNELS);
        List<Object> queryParams = new ArrayList<>();

        query.append(" WHERE 1 = 1");  // Initial condition to start the WHERE clause

        if (channel != null && !channel.isEmpty()) {
            query.append(" AND c.channel LIKE ?");
            queryParams.add(channel);
        }

        if (tag != null && !tag.isEmpty()) {
            query.append(" AND t.tag LIKE ?");
            queryParams.add(tag);
        }

        if ("asc".equalsIgnoreCase(order) || "desc".equalsIgnoreCase(order)) {
            query.append(" ORDER BY p.postdate ").append(order);
        }

        query.append(" LIMIT ? OFFSET ?");
        queryParams.add(limit);
        queryParams.add(offset);

        return jdbcTemplate.query(query.toString(), ROW_MAPPER, queryParams.toArray());
    }

    public int getTotalPostsCountByCriteria(String channel, String tag) {
        StringBuilder query = new StringBuilder(COUNT_POSTS_JOIN_TAGS_AND_CHANNELS);
        List<Object> queryParams = new ArrayList<>();

        query.append(" WHERE 1 = 1");  // Initial condition to start the WHERE clause

        if (channel != null && !channel.isEmpty()) {
            query.append(" AND channel LIKE ?");
            queryParams.add(channel);
        }

        if (tag != null && !tag.isEmpty()) {
            query.append(" AND tag LIKE ?");
            queryParams.add(tag);
        }

        return jdbcTemplate.queryForObject(query.toString(), Integer.class, queryParams.toArray());
    }

    @Override
    public Optional<Post> findPostById(long id) {
        final List<Post> postList = jdbcTemplate.query(POSTS_JOIN_USERS_AND_CHANNELS + " where postid=?;", ROW_MAPPER, id);
        return postList.isEmpty() ? Optional.empty() : Optional.of(postList.get(0));
    }

}

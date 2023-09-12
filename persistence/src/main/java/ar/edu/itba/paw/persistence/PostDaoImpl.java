package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class PostDaoImpl implements PostDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final String POSTS_JOIN_NEIGHBORS_AND_CHANNELS =
            "select p.postid as postid, title, description, postdate, n.neighborid, mail, name, surname, c.channelid, channel, postimage\n" +
            "from posts p join neighbors n on p.neighborid = n.neighborid join channels c on p.channelid = c.channelid ";
    private final String POSTS_JOIN_NEIGHBORS_AND_CHANNELS_AND_TAGS =
            "select p.postid as postid, title, description, postdate, n.neighborid, mail, name, surname, n.neighborhoodid, neighborhoodname, c.channelid, channel, postimage\n" +
            "from posts p join neighbors n on p.neighborid = n.neighborid join neighborhoods nh on n.neighborhoodid = nh.neighborhoodid join channels c on c.channelid = p.channelid  join posts_tags on p.postid = posts_tags.postid join tags on posts_tags.tagid = tags.tagid " ;

    @Autowired
    public PostDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("postid")
                .withTableName("posts");
    }

    @Override
    public Post createPost(String title, String description, long neighborId, long channelId, byte[] imageFile) {
        System.out.println("CREATING NEW POST");
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("description", description);
        data.put("postdate", Timestamp.valueOf(LocalDateTime.now()));
        data.put("neighborid", neighborId);
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

    private static final RowMapper<Post> ROW_MAPPER = (rs, rowNum) ->
            new Post.Builder()
                    .postId(rs.getLong("postid"))
                    .title(rs.getString("title"))
                    .description(rs.getString("description"))
                    .date(rs.getTimestamp("postdate"))
                    .imageFile(rs.getBytes("postimage"))
                    .neighbor(
                            new Neighbor.Builder()
                                    .neighborId(rs.getLong("neighborid"))
                                    .mail(rs.getString("mail"))
                                    .name(rs.getString("name"))
                                    .surname(rs.getString("surname"))
                                    .build()
                    )
                    .channel(
                            new Channel.Builder()
                                    .channelId(rs.getLong("channelid"))
                                    .channel(rs.getString("channel"))
                                    .build()
                    )
                    .build();

    @Override
    public List<Post> getPosts() {
        return jdbcTemplate.query(POSTS_JOIN_NEIGHBORS_AND_CHANNELS, ROW_MAPPER);
    }

    @Override
    public List<Post> getPostsByDate(String order) {
        // It is not a dangerous SQL Injection as the user has no access to this variable and is only managed internally
        // Still I leave this conditional just in case someone makes a change in the future
        if (!("asc".equalsIgnoreCase(order) || "desc".equalsIgnoreCase(order))) {
            throw new IllegalArgumentException("Invalid 'order' parameter.");
        }
        return jdbcTemplate.query(POSTS_JOIN_NEIGHBORS_AND_CHANNELS + " order by postdate " + order, ROW_MAPPER);
    }

    @Override
    public List<Post> getPostsByTag(String tag) {
        return jdbcTemplate.query(POSTS_JOIN_NEIGHBORS_AND_CHANNELS_AND_TAGS + " where tag like ?", ROW_MAPPER, tag);
    }

    @Override
    public List<Post> getPostsByChannel(String channel) {
        return jdbcTemplate.query(POSTS_JOIN_NEIGHBORS_AND_CHANNELS_AND_TAGS + " where channel like ?", ROW_MAPPER, channel);
    }

    @Override
    public Optional<Post> findPostById(long id) {
        final List<Post> postList = jdbcTemplate.query(POSTS_JOIN_NEIGHBORS_AND_CHANNELS + " where postid=?;", ROW_MAPPER, id);
        return postList.isEmpty() ? Optional.empty() : Optional.of(postList.get(0));
    }
}

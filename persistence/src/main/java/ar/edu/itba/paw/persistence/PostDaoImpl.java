package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PostDaoImpl implements PostDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final String POSTS_JOIN_NEIGHBORS_AND_CHANNELS =
            "select p.postid as postid, title, description, postdate, n.neighborid, mail, name, surname, c.channelid, channel, postimage\n" +
            "from posts p join neighbors n on p.neighborid = n.neighborid join channels c on p.channelid = c.channelid ";
    private final String POSTS_JOIN_NEIGHBORS_AND_NEIGHBORHOODS_AND_CHANNELS_AND_TAGS =
            "select p.postid as postid, title, description, postdate, n.neighborid, mail, name, surname, n.neighborhoodid, neighborhoodname, c.channelid, channel, postimage\n" +
            "from posts p join neighbors n on p.neighborid = n.neighborid join neighborhoods nh on n.neighborhoodid = nh.neighborhoodid join channels c on c.channelid = p.channelid  join posts_tags on p.postid = posts_tags.postid join tags on posts_tags.tagid = tags.tagid " ;
    private final String POSTS_JOIN_NEIGHBORS_AND_NEIGHBORHOODS_AND_CHANNELS =
            "select p.postid as postid, title, description, postdate, n.neighborid, mail, name, surname, n.neighborhoodid, neighborhoodname, c.channelid, channel, postimage\n" +
                    "from posts p join neighbors n on p.neighborid = n.neighborid join neighborhoods nh on n.neighborhoodid = nh.neighborhoodid join channels c on c.channelid = p.channelid ";
    private final String COUNT_POSTS =
            "SELECT COUNT(*)\n " +
            "FROM posts";
    private final String COUNT_POSTS_JOIN_CHANNELS =
            "SELECT COUNT(*)\n " +
            "FROM posts JOIN public.channels c on c.channelid = posts.channelid";
    private final String COUNT_POSTS_JOIN_TAGS =
            "SELECT COUNT(*)\n " +
            "FROM posts JOIN posts_tags ON posts.postid = posts_tags.postid JOIN tags ON posts_tags.tagid = tags.tagid";
    private final String COUNT_POSTS_JOIN_TAGS_AND_CHANNELS =
            "SELECT COUNT(*)\n" +
            "FROM posts JOIN posts_tags ON posts.postid = posts_tags.postid JOIN tags ON posts_tags.tagid = tags.tagid  JOIN public.channels c on c.channelid = posts.channelid ";


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
    public List<Post> getPosts(int offset, int limit) {
        String query = POSTS_JOIN_NEIGHBORS_AND_CHANNELS + " LIMIT ? OFFSET ?";
        return jdbcTemplate.query(query, ROW_MAPPER, limit, offset);
    }

    @Override
    public List<Post> getPostsByDate(String order, int offset, int limit) {
        // Check the validity of the 'order' parameter
        if (!("asc".equalsIgnoreCase(order) || "desc".equalsIgnoreCase(order))) {
            throw new IllegalArgumentException("Invalid 'order' parameter.");
        }

        String query = POSTS_JOIN_NEIGHBORS_AND_CHANNELS + " ORDER BY postdate " + order + " LIMIT ? OFFSET ?";
        return jdbcTemplate.query(query, ROW_MAPPER, limit, offset);
    }

    @Override
    public List<Post> getPostsByTag(String tag, int offset, int limit) {
        String query = POSTS_JOIN_NEIGHBORS_AND_NEIGHBORHOODS_AND_CHANNELS_AND_TAGS + " WHERE tag LIKE ? LIMIT ? OFFSET ?";
        return jdbcTemplate.query(query, ROW_MAPPER, tag, limit, offset);
    }

    @Override
    public List<Post> getPostsByChannel(String channel, int offset, int limit) {
        String query = POSTS_JOIN_NEIGHBORS_AND_NEIGHBORHOODS_AND_CHANNELS + " WHERE channel LIKE ? LIMIT ? OFFSET ?";
        return jdbcTemplate.query(query, ROW_MAPPER, channel, limit, offset);
    }

    @Override
    public List<Post> getPostsByChannelAndDate(final String channel, final String order, int offset, int limit){
        String query = POSTS_JOIN_NEIGHBORS_AND_CHANNELS + "WHERE channel LIKE ? ORDER BY postdate " + order + " LIMIT ? OFFSET ?";
        return jdbcTemplate.query(query, ROW_MAPPER, channel, limit, offset);
    }

    @Override
    public List<Post> getPostsByChannelAndDateAndTag(final String channel, final String order, final String tag, int offset, int limit) {
        String query = POSTS_JOIN_NEIGHBORS_AND_NEIGHBORHOODS_AND_CHANNELS_AND_TAGS + "WHERE channel LIKE ? AND tag like ? ORDER BY postdate " + order + " LIMIT ? OFFSET ?";
        return jdbcTemplate.query(query, ROW_MAPPER, channel, tag, limit, offset);
    }

    @Override
    public int getTotalPostsCount() {
        return jdbcTemplate.queryForObject(COUNT_POSTS, Integer.class);
    }

    @Override
    public int getTotalPostsCountInChannel(String channel){
        String query = COUNT_POSTS_JOIN_CHANNELS + " WHERE channel LIKE ?";
        return jdbcTemplate.queryForObject(query, Integer.class, channel);
    }

    @Override
    public int getTotalPostsCountWithTag(String tag) {
        String query = COUNT_POSTS_JOIN_TAGS + " WHERE tag LIKE ?";
        return jdbcTemplate.queryForObject(query, Integer.class, tag);
    }

    @Override
    public int getTotalPostsCountInChannelWithTag(String channel, String tag ){
        String query = COUNT_POSTS_JOIN_TAGS_AND_CHANNELS + " WHERE channel LIKE ? AND tag like ?";
        return jdbcTemplate.queryForObject(query, Integer.class, channel, tag);
    }

    @Override
    public Optional<Post> findPostById(long id) {
        final List<Post> postList = jdbcTemplate.query(POSTS_JOIN_NEIGHBORS_AND_CHANNELS + " where postid=?;", ROW_MAPPER, id);
        return postList.isEmpty() ? Optional.empty() : Optional.of(postList.get(0));
    }

}

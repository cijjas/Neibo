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
import java.time.LocalDate;
import java.util.*;

@Repository
public class PostDaoImpl implements PostDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert; // En vez de hacer queries de tipo INSERT, usamos este objeto.
    private final String baseQuery =
            "select p.postid as postid, title, description, postdate, n.neighborid, mail, name, surname, c.channelid, channel " +
            "from posts p join neighbors n on p.neighborid = n.neighborid join channels c on p.channelid = c.channelid ";

    // cambiar query, cambiar row mapper

    @Autowired // Motor de inyección de dependencias; nos da el DataSource definido en el @Bean de WebConfig.
    public PostDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("postid")
                .withTableName("posts");
        // con .usingColumns(); podemos especificar las columnas a usar y otras cosas
    }

    @Override
    public Post create(String title, String description, long neighborId, long channelId) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("description", description);
        data.put("postdate", LocalDate.now());
        data.put("neighborid", neighborId);
        data.put("channelid", channelId);

        final Number key = jdbcInsert.executeAndReturnKey(data);
        return new Post.Builder()
                .postId(key.longValue())
                .title(title)
                .description(description)
                .build();
    }

    private static final RowMapper<Post> ROW_MAPPER = (rs, rowNum) ->
            new Post.Builder()
                    .postId(rs.getLong("postid"))
                    .title(rs.getString("title"))
                    .description(rs.getString("description"))
                    .date(rs.getDate("postdate"))
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
    public List<Post> getAllPosts() {
        return jdbcTemplate.query(baseQuery, ROW_MAPPER);
    }

    @Override
    public List<Post> getAllPostsByDate(String order) {
        // It is not a dangerous SQL Injection as the user has no access to this variable and is only managed internally
        // Still I tried using the following line but does not work although im quite sure it should, maybe string issues?
        // return jdbcTemplate.query(baseQuery + " order by postdate ?", ROW_MAPPER, order);
        return jdbcTemplate.query(baseQuery + " order by postdate " + order, ROW_MAPPER);
    }

    @Override
    public List<Post> getAllPostsByTag(String tag) {
        // This WILL be vulnerable to SQL Injections if we ever allow the user to create their own tags, for as long as they
        // are created by us there should not be danger
        /*
        return jdbcTemplate.query("select p.postid as postid, title, description, postdate, n.neighborid, mail, name, surname, n.neighborhoodid,neighborhoodname\n" +
                "from posts p join neighbors n on p.neighborid = n.neighborid join neighborhoods nh on n.neighborhoodid = nh.neighborhoodid join posts_tags on p.postid = posts_tags.postid join tags on posts_tags.tagid = tags.tagid\n" +
                "where tag like '?'", ROW_MAPPER, tag);
         */
        return jdbcTemplate.query(
            "select p.postid as postid, title, description, postdate, n.neighborid, mail, name, surname, n.neighborhoodid, neighborhoodname, c.channelid, channel\n" +
                "from posts p join neighbors n on p.neighborid = n.neighborid join neighborhoods nh on n.neighborhoodid = nh.neighborhoodid join channels c on c.channelid = p.channelid  join posts_tags on p.postid = posts_tags.postid join tags on posts_tags.tagid = tags.tagid" +
                " where tag like '" + tag + "';", ROW_MAPPER);

    }

    @Override
    public List<Post> getAllPostsByChannel(String channel) {
        // Again, this WILL be vulnerable to SQL Injections if we allow users to create channels/communities
        return jdbcTemplate.query(
                "select p.postid as postid, title, description, postdate, n.neighborid, mail, name, surname, n.neighborhoodid, neighborhoodname, c.channelid, channel\n" +
                        "from posts p join neighbors n on p.neighborid = n.neighborid join neighborhoods nh on n.neighborhoodid = nh.neighborhoodid join channels c on c.channelid = p.channelid  join posts_tags on p.postid = posts_tags.postid join tags on posts_tags.tagid = tags.tagid" +
                        " where channel like '" + channel + "';", ROW_MAPPER);
    }

    @Override
    public Optional<Post> findPostById(long id) {
        final List<Post> postList = jdbcTemplate.query(baseQuery + " where postid=?", ROW_MAPPER, id);
        return postList.isEmpty() ? Optional.empty() : Optional.of(postList.get(0));
    }

}

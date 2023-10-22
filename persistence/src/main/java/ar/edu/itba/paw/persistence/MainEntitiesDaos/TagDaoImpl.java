package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.models.MainEntities.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TagDaoImpl implements TagDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagDaoImpl.class);
    private static final RowMapper<Tag> ROW_MAPPER =
            (rs, rowNum) -> new Tag.Builder()
                    .tagId(rs.getLong("tagid"))
                    .tag(rs.getString("tag"))
                    .build();
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String TAGS = "SELECT * FROM tags ";
    private final String TAGS_JOIN_POSTS =
            "SELECT tags.tagid, tag\n" +
                    "FROM posts_tags " +
                    "INNER JOIN tags ON posts_tags.tagid = tags.tagid ";
    private final String TAGS_JOIN_POSTS_JOIN_USERS_JOIN_NEIGHBORHOODS =
            "SELECT DISTINCT tags.tagid, tag\n" +
                    "FROM posts_tags " +
                    "INNER JOIN tags ON posts_tags.tagid = tags.tagid " +
                    "INNER JOIN posts p ON posts_tags.postid = p.postid " +
                    "INNER JOIN users u ON u.userid = p.userid " +
                    "INNER JOIN neighborhoods nh ON u.neighborhoodid = nh.neighborhoodid ";

    // ---------------------------------------------- TAGS INSERT ------------------------------------------------------

    @Autowired
    public TagDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("tagid")
                .withTableName("tags");
    }


    // ---------------------------------------------- TAGS SELECT ------------------------------------------------------

    @Override
    public Tag createTag(String name) {
        LOGGER.debug("Inserting Tag {}", name);
        Map<String, Object> data = new HashMap<>();
        data.put("tag", name);

        try {
            final Number key = jdbcInsert.executeAndReturnKey(data);
            return new Tag.Builder()
                    .tagId(key.longValue())
                    .tag(name)
                    .build();
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Tag", ex);
            throw new InsertionException("An error occurred whilst creating the Tag");
        }

    }

    // Method cant properly differentiate between not finding the post and the post having no comments, but as the function
    // is called through the detail of a post it cant be an invalid postId
    @Override
    public List<Tag> findTagsByPostId(long id) {
        LOGGER.debug("Selecting Tags for Post with postId {}", id);
        return jdbcTemplate.query(TAGS_JOIN_POSTS + " WHERE postid=?;", ROW_MAPPER, id);
    }

    @Override
    public List<Tag> getTags(long neighborhoodId) {
        LOGGER.debug("Selecting Tag List from Neighborhood {}", neighborhoodId);
        return jdbcTemplate.query(TAGS_JOIN_POSTS_JOIN_USERS_JOIN_NEIGHBORHOODS + " WHERE nh.neighborhoodid=?", ROW_MAPPER, neighborhoodId);
    }

    @Override
    public List<Tag> getAllTags() {
        LOGGER.debug("Selecting Complete Tag List");
        return jdbcTemplate.query(TAGS, ROW_MAPPER);
    }
}

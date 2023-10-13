package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.models.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TagDaoImpl implements TagDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final String TAGS = "select * from tags ";
    private final String TAGS_JOIN_POSTS =
            "select tags.tagid, tag\n" +
            "from posts_tags join tags on posts_tags.tagid = tags.tagid ";
    private final String TAGS_JOIN_POSTS_JOIN_USERS_JOIN_NEIGHBORHOODS =
            "select distinct tags.tagid, tag\n" +
            "from posts_tags join tags on posts_tags.tagid = tags.tagid join posts p on posts_tags.postid = p.postid join users u on u.userid = p.userid join neighborhoods nh on u.neighborhoodid = nh.neighborhoodid ";

    private static final Logger LOGGER = LoggerFactory.getLogger(TagDaoImpl.class);

    @Autowired
    public TagDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("tagid")
                .withTableName("tags");
    }

    // ---------------------------------------------- TAGS INSERT ------------------------------------------------------

    @Override
    public Tag createTag(String name) {
        LOGGER.info("Inserting Tag {}", name);
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


    // ---------------------------------------------- TAGS SELECT ------------------------------------------------------

    private static final RowMapper<Tag> ROW_MAPPER =
            (rs, rowNum) -> new Tag.Builder()
                    .tagId(rs.getLong("tagid"))
                    .tag(rs.getString("tag"))
                    .build();

    // Method cant properly differentiate between not finding the post and the post having no comments, but as the function
    // is called through the detail of a post it cant be an invalid postId
    @Override
    public List<Tag> findTagsByPostId(long id) {
        LOGGER.info("Selecting Tags for Post with postId {}", id);
        return jdbcTemplate.query(TAGS_JOIN_POSTS + " WHERE postid=?;", ROW_MAPPER, id);
    }

    @Override
    public List<Tag> getTags(long neighborhoodId) {
        LOGGER.info("Selecting Tag List from Neighborhood {}", neighborhoodId);
        return jdbcTemplate.query(TAGS_JOIN_POSTS_JOIN_USERS_JOIN_NEIGHBORHOODS + " WHERE nh.neighborhoodid=?", ROW_MAPPER, neighborhoodId);
    }

    @Override
    public List<Tag> getAllTags() {
        LOGGER.info("Selecting Complete Tag List");
        return jdbcTemplate.query(TAGS, ROW_MAPPER);
    }
}

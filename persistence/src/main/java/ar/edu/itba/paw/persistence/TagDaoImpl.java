package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.models.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

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

    @Autowired
    public TagDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("tagid")
                .withTableName("tags");
    }

    private static final RowMapper<Tag> ROW_MAPPER =
            (rs, rowNum) -> new Tag.Builder()
                    .tagId(rs.getLong("tagid"))
                    .tag(rs.getString("tag"))
                    .build();

    @Override
    public Optional<List<Tag>> findTagsByPostId(long id) {
        final List<Tag> tags = jdbcTemplate.query(TAGS_JOIN_POSTS + " WHERE postid=?;", ROW_MAPPER, id);
        return tags.isEmpty() ? Optional.empty() : Optional.of(tags);
    }

    @Override
    public List<Tag> getTags() {
        return jdbcTemplate.query(TAGS, ROW_MAPPER);
    }

    @Override
    public Tag createTag(String name) {
        Map<String, Object> data = new HashMap<>();
        data.put("tag", name);

        final Number key = jdbcInsert.executeAndReturnKey(data);
        return new Tag.Builder()
                .tagId(key.longValue())
                .tag(name)
                .build();
    }
}

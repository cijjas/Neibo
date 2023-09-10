package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class TagDaoImpl implements TagDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert; // En vez de hacer queries de tipo INSERT, usamos este objeto.

    @Autowired // Motor de inyección de dependencias; nos da el DataSource definido en el @Bean de WebConfig.
    public TagDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("tagid")
                .withTableName("tags");
        // con .usingColumns(); podemos especificar las columnas a usar y otras cosas
    }

    private static final RowMapper<Tag> ROW_MAPPER =
            (rs, rowNum) -> new Tag.Builder()
                    .tagId(rs.getLong("tagid"))
                    .tag(rs.getString("tag"))
                    .build();


    // getTags

    @Override
    public Optional<List<Tag>> findTags(long id) {
        final List<Tag> tags = jdbcTemplate.query("select tags.tagid, tag from\n" +
                "             posts_tags join tags on posts_tags.tagid = tags.tagid WHERE postid=?;", ROW_MAPPER, id);
        return tags.isEmpty() ? Optional.empty() : Optional.of(tags);
    }

    @Override
    public List<Tag> getAllTags() {
        return jdbcTemplate.query("select * from tags;", ROW_MAPPER);
    }
}

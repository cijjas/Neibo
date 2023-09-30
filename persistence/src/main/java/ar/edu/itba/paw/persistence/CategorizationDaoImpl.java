package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CategorizationDaoImpl implements CategorizationDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public CategorizationDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("posts_tags");
    }

    // -------------------------------------------- POSTS_TAGS INSERT --------------------------------------------------

    @Override
    public void createCategory(final long tagId, final long postId) {
        Map<String, Object> data = new HashMap<>();
        data.put("tagid", tagId);
        data.put("postid", postId);
        jdbcInsert.execute(data);
    }
}

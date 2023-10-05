package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CategorizationDaoImpl implements CategorizationDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final Logger LOGGER = LoggerFactory.getLogger(CategorizationDaoImpl.class);

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
        try {
            jdbcInsert.execute(data);
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Category", ex);
            throw new InsertionException("An error occurred whilst creating the category");
        }
    }
}

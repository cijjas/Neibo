package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ResourceDao;
import ar.edu.itba.paw.models.Resource;
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
public class ResourceDaoImpl implements ResourceDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final String RESOURCES =
            "select rs.* " +
            "from resources rs";

    @Autowired
    public ResourceDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("resourceid")
                .withTableName("resources");
    }

    // --------------------------------------------- RESOURCE INSERT ----------------------------------------------------

    @Override
    public Resource createResource(long neighborhoodId, String title, String description, long imageId) {
        Map<String, Object> data = new HashMap<>();
        data.put("neighborhoodid", neighborhoodId);
        data.put("title", title);
        data.put("description", description);
        data.put("imageId", imageId);

        final Number key = jdbcInsert.executeAndReturnKey(data);
        return new Resource.Builder()
                .title(title)
                .description(description)
                .imageId(imageId)
                .neighborhoodId(neighborhoodId)
                .build();
    }

    // --------------------------------------------- RESOURCE SELECT ----------------------------------------------------

    private static final RowMapper<Resource> ROW_MAPPER = (rs, rowNum) ->
            new Resource.Builder()
                    .title(rs.getString("title"))
                    .description(rs.getString("description"))
                    .imageId(rs.getLong("imageId"))
                    .neighborhoodId(rs.getLong("neighborhoodid"))
                    .build();
    @Override
    public List<Resource> getResources(final long neighborhoodId) {
        return jdbcTemplate.query(RESOURCES + " WHERE rs.neighborhoodid = ?", ROW_MAPPER, neighborhoodId);
    }
}

package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.ResourceDao;
import ar.edu.itba.paw.models.Resource;
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
public class ResourceDaoImpl implements ResourceDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final String RESOURCES =
            "select rs.* " +
            "from resources rs";

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceDaoImpl.class);

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
        data.put("resourcetitle", title);
        data.put("resourcedescription", description);
        data.put("resourceimageid", imageId == 0 ? null : imageId);


        try {
            final Number key = jdbcInsert.executeAndReturnKey(data);
            return new Resource.Builder()
                    .resourceId(key.longValue())
                    .title(title)
                    .description(description)
                    .imageId(imageId)
                    .neighborhoodId(neighborhoodId)
                    .build();
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Resource", ex);
            throw new InsertionException("An error occurred whilst creating the Resource");
        }

    }

    // --------------------------------------------- RESOURCE SELECT ----------------------------------------------------

    private static final RowMapper<Resource> ROW_MAPPER = (rs, rowNum) ->
            new Resource.Builder()
                    .resourceId(rs.getLong("resourceid"))
                    .title(rs.getString("resourcetitle"))
                    .description(rs.getString("resourcedescription"))
                    .imageId(rs.getLong("resourceimageId"))
                    .neighborhoodId(rs.getLong("neighborhoodid"))
                    .build();
    @Override
    public List<Resource> getResources(final long neighborhoodId) {
        return jdbcTemplate.query(RESOURCES + " WHERE rs.neighborhoodid = ?", ROW_MAPPER, neighborhoodId);
    }

    @Override
    public boolean deleteResource(final long resourceId) {
        return jdbcTemplate.update("DELETE FROM resources WHERE resourceid = ?", resourceId) > 0;
    }
}

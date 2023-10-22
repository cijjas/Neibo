package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.ResourceDao;
import ar.edu.itba.paw.models.MainEntities.Resource;
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
public class ResourceDaoImpl implements ResourceDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceDaoImpl.class);
    private static final RowMapper<Resource> ROW_MAPPER = (rs, rowNum) ->
            new Resource.Builder()
                    .resourceId(rs.getLong("resourceid"))
                    .title(rs.getString("resourcetitle"))
                    .description(rs.getString("resourcedescription"))
                    .imageId(rs.getLong("resourceimageId"))
                    .neighborhoodId(rs.getLong("neighborhoodid"))
                    .build();
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String RESOURCES = "SELECT rs.* FROM resources rs";

    // --------------------------------------------- RESOURCES INSERT --------------------------------------------------

    @Autowired
    public ResourceDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("resourceid")
                .withTableName("resources");
    }

    // --------------------------------------------- RESOURCES SELECT --------------------------------------------------

    @Override
    public Resource createResource(long neighborhoodId, String title, String description, long imageId) {
        LOGGER.debug("Inserting Resource {}", title);
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

    @Override
    public List<Resource> getResources(final long neighborhoodId) {
        LOGGER.debug("Selecting Resources from Neighborhood {}", neighborhoodId);
        return jdbcTemplate.query(RESOURCES + " WHERE rs.neighborhoodid = ?", ROW_MAPPER, neighborhoodId);
    }

    // --------------------------------------------- RESOURCE DELETE ----------------------------------------------------

    @Override
    public boolean deleteResource(final long resourceId) {
        LOGGER.debug("Deleting Resource with resourceId {}", resourceId);
        return jdbcTemplate.update("DELETE FROM resources WHERE resourceid = ?", resourceId) > 0;
    }
}

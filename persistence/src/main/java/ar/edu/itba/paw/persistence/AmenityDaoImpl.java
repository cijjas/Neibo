package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.models.Shift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class AmenityDaoImpl implements AmenityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private ShiftDao shiftDao;
    private final RowMapper<Amenity> ROW_MAPPER = (rs, rowNum) -> {
        List<Shift> availableShifts = shiftDao.getAmenityShifts(rs.getLong("amenityid"));
        return new Amenity.Builder()
                .amenityId(rs.getLong("amenityid"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .neighborhoodId(rs.getLong("neighborhoodid"))
                .availableShifts(availableShifts)
                .build();
    };
    private String AMENITIES = "SELECT * FROM amenities";
    private String COUNT_AMENITIES = "SELECT COUNT(*) FROM amenities";

    // ---------------------------------------------- AMENITY INSERT ---------------------------------------------------

    @Autowired
    public AmenityDaoImpl(final DataSource ds, final ShiftDao shiftDao) {
        this.shiftDao = shiftDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("amenityid")
                .withTableName(Table.amenities.name());
    }

    // ---------------------------------------------- AMENITY SELECT ---------------------------------------------------

    @Override
    public Amenity createAmenity(String name, String description, Long neighborhoodId) {
        LOGGER.debug("Inserting Amenity {}", name);
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("description", description);
        data.put("neighborhoodid", neighborhoodId);

        final Number amenityId = jdbcInsert.executeAndReturnKey(data);
        return new Amenity.Builder()
                .amenityId(amenityId.longValue())
                .name(name)
                .description(description)
                .build();
    }

    @Override
    public Optional<Amenity> findAmenityById(Long amenityId) {
        LOGGER.debug("Selecting Amenity with id {}", amenityId);
        List<Amenity> amenities = jdbcTemplate.query(AMENITIES + " WHERE amenityid = ?", ROW_MAPPER, amenityId);
        return amenities.isEmpty() ? Optional.empty() : Optional.of(amenities.get(0));
    }

    @Override
    public List<Amenity> getAmenities(Long neighborhoodId, int page, int size) {
        LOGGER.debug("Selecting Amenities from Neighborhood {}", neighborhoodId);
        return jdbcTemplate.query(AMENITIES + " WHERE neighborhoodId = ? ORDER BY name  LIMIT ? OFFSET ?", ROW_MAPPER, neighborhoodId, size, (page - 1) * size);
    }

    @Override
    public int getAmenitiesCount(Long neighborhoodId) {
        LOGGER.debug("Selecting Amenities from Neighborhood {}", neighborhoodId);
        return jdbcTemplate.queryForObject(COUNT_AMENITIES + " WHERE neighborhoodId = ?", Integer.class, neighborhoodId);
    }

    // ---------------------------------------------- AMENITY DELETE ---------------------------------------------------

    @Override
    public boolean deleteAmenity(Long amenityId) {
        LOGGER.debug("Deleting Amenity with id {}", amenityId);
        return jdbcTemplate.update("DELETE FROM amenities WHERE amenityid = ?", amenityId) > 0;
    }
}

package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.models.DayTime;
import ar.edu.itba.paw.models.Shift;
import enums.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AmenityDaoImpl implements AmenityDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private ShiftDao shiftDao;

    private String AMENITIES = "SELECT * FROM amenities";

    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityDaoImpl.class);

    @Autowired
    public AmenityDaoImpl(final DataSource ds, final ShiftDao shiftDao) {
        this.shiftDao = shiftDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("amenityid")
                .withTableName(Table.amenities.name());
    }

    // ---------------------------------------------- AMENITY INSERT ---------------------------------------------------

    @Override
    public Amenity createAmenity(String name, String description, long neighborhoodId) {
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

    // ---------------------------------------------- AMENITY SELECT ---------------------------------------------------

    private final RowMapper<Amenity> ROW_MAPPER = (rs, rowNum) -> {
        List<Shift> availableShifts = shiftDao.getAmenityShifts(rs.getLong("amenityid"));
        System.out.println("NAKAKANAKA");
        System.out.println(availableShifts);
        return new Amenity.Builder()
                .amenityId(rs.getLong("amenityid"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .neighborhoodId(rs.getLong("neighborhoodid"))
                .availableShifts(availableShifts)
                .build();
    };

    @Override
    public Optional<Amenity> findAmenityById(long amenityId) {
        LOGGER.debug("Selecting Amenity with id {}", amenityId);
        List<Amenity> amenities = jdbcTemplate.query(AMENITIES + " WHERE amenityid = ?", ROW_MAPPER, amenityId);
        return amenities.isEmpty() ? Optional.empty() : Optional.of(amenities.get(0));
    }

    @Override
    public List<Amenity> getAmenities(long neighborhoodId) {
        LOGGER.debug("Selecting Amenities from Neighborhood {}", neighborhoodId);
        return jdbcTemplate.query(AMENITIES + " WHERE neighborhoodId = ?", ROW_MAPPER, neighborhoodId);
    }

    // ---------------------------------------------- AMENITY DELETE ---------------------------------------------------

    @Override
    public boolean deleteAmenity(long amenityId) {
        LOGGER.debug("Deleting Amenity with id {}", amenityId);
        return jdbcTemplate.update("DELETE FROM amenities WHERE amenityid = ?", amenityId) > 0;
    }
}

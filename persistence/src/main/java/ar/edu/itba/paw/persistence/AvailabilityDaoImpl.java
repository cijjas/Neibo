package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
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
import java.util.OptionalLong;

@Repository
public class AvailabilityDaoImpl implements AvailabilityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityDaoImpl.class);
    private static final RowMapper<Long> ROW_MAPPER = (rs, rowNum) -> {
        return rs.getLong("amenityavailabilityid");
    };
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private AmenityDao amenityDao;
    private ShiftDao shiftDao;

    // ---------------------------------- AMENITIES_SHIFTS_AVAILABILITY INSERT -----------------------------------------

    @Autowired
    public AvailabilityDaoImpl(final DataSource ds, final AmenityDao amenityDao, final ShiftDao shiftDao) {
        this.amenityDao = amenityDao;
        this.shiftDao = shiftDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName(Table.amenities_shifts_availability.name())
                .usingGeneratedKeyColumns("amenityavailabilityid");
    }

    // ---------------------------------- AMENITIES_SHIFTS_AVAILABILITY SELECT -----------------------------------------

    @Override
    public Number createAvailability(long amenityId, long shiftId) {
        LOGGER.debug("Inserting Availability");
        Map<String, Object> data = new HashMap<>();
        data.put("amenityid", amenityId);
        data.put("shiftid", shiftId);
        try {
            return jdbcInsert.executeAndReturnKey(data);
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Availability", ex);
            throw new InsertionException("An error occurred whilst creating the Availability for the Amenity");
        }
    }

    @Override
    public OptionalLong findAvailabilityId(long amenityId, long shiftId) {
        LOGGER.debug("Selecting Availability with amenityId {} and shiftId {}", amenityId, shiftId);
        final List<Long> list = jdbcTemplate.query("SELECT amenityAvailabilityId FROM amenities_shifts_availability WHERE amenityid = ? AND shiftid = ?", ROW_MAPPER, amenityId, shiftId);
        return list.isEmpty() ? OptionalLong.empty() : OptionalLong.of(list.get(0));
    }


    // ---------------------------------- AMENITIES_SHIFTS_AVAILABILITY DELETE -----------------------------------------

    @Override
    public boolean deleteAvailability(long amenityId, long shiftId) {
        LOGGER.debug("Deleting Availability with amenityId {} and shiftId {}", amenityId, shiftId);
        return jdbcTemplate.update("DELETE FROM amenities_shifts_availability WHERE amenityid = ? and shiftid = ?", amenityId, shiftId) > 0;
    }

}
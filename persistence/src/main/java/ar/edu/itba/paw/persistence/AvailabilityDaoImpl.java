package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.models.*;
import enums.DayOfTheWeek;
import enums.StandardTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AvailabilityDaoImpl implements AvailabilityDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private AmenityDao amenityDao;
    private ShiftDao shiftDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityDaoImpl.class);

    @Autowired
    public AvailabilityDaoImpl(final DataSource ds, final AmenityDao amenityDao, final ShiftDao shiftDao) {
        this.amenityDao = amenityDao;
        this.shiftDao = shiftDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("amenities_shifts_availability")
                .usingGeneratedKeyColumns("amenityavailabilityid");
    }

    // ---------------------------------- AMENITIES_SHIFTS_AVAILABILITY INSERT -----------------------------------------

    @Override
    public void createAvailability(long amenityId, long shiftId) {
        Map<String, Object> data = new HashMap<>();
        data.put("amenityid", amenityId);
        data.put("shiftid", shiftId);
        try {
            jdbcInsert.execute(data);
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Availability", ex);
            throw new InsertionException("An error occurred whilst creating the Availability for the Amenity");
        }
    }

    @Override
    public Optional<Long> getAvailabilityId(long amenityId, long shiftId) {
        String query = "SELECT amenityAvailabilityId FROM amenities_shifts_availability WHERE amenityid = ? AND shiftid = ?";

        try {
            Long amenityAvailabilityId = jdbcTemplate.queryForObject(
                    query,
                    new Object[]{amenityId, shiftId},
                    Long.class
            );
            return Optional.ofNullable(amenityAvailabilityId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ---------------------------------- AMENITIES_SHIFTS_AVAILABILITY SELECT -----------------------------------------

}
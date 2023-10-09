package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.models.DayTime;
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
    private SimpleJdbcInsert jdbcInsertHours;
    private SimpleJdbcInsert jdbcInsertAmenityHours;

    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityDaoImpl.class);

/*    @Autowired
    public AmenityDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("amenityid")
                .withTableName("amenities");
        this.jdbcInsertHours = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("hoursid")
                .withTableName("hours");
        this.jdbcInsertAmenityHours = new SimpleJdbcInsert(ds)
                .withTableName("amenities_hours");
    }*/

    // ---------------------------------------------- AMENITY INSERT ---------------------------------------------------

    @Override
    public Amenity createAmenity(String name, String description, Map<String, DayTime> dayHourData, long neighborhoodId) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("description", description);
        data.put("neighborhoodid", neighborhoodId);

        final Number amenityId = jdbcInsert.executeAndReturnKey(data);

        for (Map.Entry<String, DayTime> entry : dayHourData.entrySet()) {
            String dayOfWeek = entry.getKey();
            DayTime openingClosingTimes = entry.getValue();

            Time openTime = openingClosingTimes.getOpenTime();
            Time closeTime = openingClosingTimes.getCloseTime();

            // Check if the same hours entry already exists in the 'hours' table
            List<Map<String, Object>> existingHours = jdbcTemplate.queryForList(
                    "SELECT hoursid FROM hours WHERE weekday = ? AND opentime = ? AND closetime = ?",
                    dayOfWeek, openTime, closeTime);

            long hoursId;

            if (existingHours.isEmpty()) {
                // If it doesn't exist, create a new entry in the 'hours' table
                Map<String, Object> hoursData = new HashMap<>();
                hoursData.put("weekday", dayOfWeek);
                hoursData.put("opentime", openTime);
                hoursData.put("closetime", closeTime);

                hoursId = jdbcInsertHours.executeAndReturnKey(hoursData).longValue();
            } else {
                // If it exists, use the existing hoursId
                Map<String, Object> existingHour = existingHours.get(0);
                hoursId = ((Number) existingHour.get("hoursid")).longValue();
            }

            // Insert into the 'amenities_hours' junction table
            Map<String, Object> amenityHoursData = new HashMap<>();
            amenityHoursData.put("amenityid", amenityId.longValue());
            amenityHoursData.put("hoursid", hoursId);
            try {
                jdbcInsertAmenityHours.execute(amenityHoursData);
            } catch (DataAccessException ex) {
                LOGGER.error("Error inserting the Amenity", ex);
                throw new InsertionException("An error occurred whilst creating the amenity");
            }
        }

        return new Amenity.Builder()
                .amenityId(amenityId.longValue())
                .name(name)
                .description(description)
                .build();
    }


    // ---------------------------------------------- AMENITY SELECT ---------------------------------------------------

    private final RowMapper<Amenity> ROW_MAPPER = (rs, rowNum) -> {
        return new Amenity.Builder()
                .amenityId(rs.getLong("amenityid"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .neighborhoodId(rs.getLong("neighborhoodid"))
                .build();
    };

    @Override
    public Optional<Amenity> findAmenityById(long amenityId) {
        List<Amenity> amenities = jdbcTemplate.query("SELECT * FROM amenities WHERE amenityid = ?", ROW_MAPPER, amenityId);
        return amenities.isEmpty() ? Optional.empty() : Optional.of(amenities.get(0));
    }

    @Override
    public List<Amenity> getAmenities(long neighborhoodId) {
        return jdbcTemplate.query("SELECT * FROM amenities WHERE neighborhoodId = ?", ROW_MAPPER, neighborhoodId);
    }

    @Override
    public Map<String, DayTime> getAmenityHoursByAmenityId(long amenityId) {
        Map<String, DayTime> amenityHoursMap = new HashMap<>();

        List<Map<String, Object>> results = jdbcTemplate.queryForList(
                "SELECT h.weekday, h.opentime, h.closetime " +
                        "FROM amenities_hours ah " +
                        "JOIN hours h ON ah.hoursid = h.hoursid " +
                        "WHERE ah.amenityId = ?",
                amenityId
        );

        for (Map<String, Object> row : results) {
            String dayOfWeek = (String) row.get("weekday");
            Time openTime = (Time) row.get("opentime");
            Time closeTime = (Time) row.get("closetime");

            if (!amenityHoursMap.containsKey(dayOfWeek)) {
                DayTime dayTime = new DayTime();
                dayTime.setOpenTime(openTime);
                dayTime.setCloseTime(closeTime);
                amenityHoursMap.put(dayOfWeek, dayTime);
            }
        }

        return amenityHoursMap;
    }

    @Override
    public DayTime getAmenityHoursByDay(long amenityId, String dayOfWeek) {
        Time openTime = null;
        Time closeTime = null;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(
                "SELECT h.opentime, h.closetime " +
                        "FROM amenities_hours ah " +
                        "JOIN hours h ON ah.hoursid = h.hoursid " +
                        "WHERE ah.amenityId = ? AND h.weekday = ?",
                amenityId,
                dayOfWeek
        );

        if (!results.isEmpty()) {
            Map<String, Object> firstResult = results.get(0);
            openTime = (Time) firstResult.get("opentime");
            closeTime = (Time) firstResult.get("closetime");
        }

        DayTime dayTime = new DayTime();
        dayTime.setOpenTime(openTime);
        dayTime.setCloseTime(closeTime);
        return dayTime;
    }

    // ---------------------------------------------- AMENITY DELETE ---------------------------------------------------

    @Override
    public boolean deleteAmenity(long amenityId) {
        List<Number> hoursIds = jdbcTemplate.queryForList(
                "SELECT ah.hoursid FROM amenities_hours ah " +
                        "WHERE ah.amenityid = ?", Number.class, amenityId);

        boolean deleted = jdbcTemplate.update("DELETE FROM amenities WHERE amenityid = ?", amenityId) > 0;

        // delete hour from hours table if unused by any other amenity
        for (Number hoursId : hoursIds) {
            int count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM amenities_hours WHERE hoursid = ?",
                    Integer.class, hoursId);

            if (count == 0) {
                // If not associated with any other amenity, delete the hours entry
                jdbcTemplate.update("DELETE FROM hours WHERE hoursid = ?", hoursId);
            }
        }
        return deleted;
    }


    // ---------------------------------------------- JOACO ---------------------------------------------------
    // ---------------------------------------------- JOACO ---------------------------------------------------
    // ---------------------------------------------- JOACO ---------------------------------------------------


    @Autowired
    public AmenityDaoImpl(final DataSource ds, final ShiftDao shiftDao) {
        this.shiftDao = shiftDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("amenityid")
                .withTableName("amenities");
    }

    // ---------------------------------------------- AMENITY INSERT ---------------------------------------------------

    @Override
    public Amenity createAmenity(String name, String description, long neighborhoodId) {
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

    private final RowMapper<Amenity> ROW_MAPPER2 = (rs, rowNum) -> {
        return new Amenity.Builder()
                .amenityId(rs.getLong("amenityid"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .neighborhoodId(rs.getLong("neighborhoodid"))
                .build();
    };

    @Override
    public Optional<Amenity> findAmenityById2(long amenityId) {
        List<Amenity> amenities = jdbcTemplate.query("SELECT * FROM amenities WHERE amenityid = ?", ROW_MAPPER2, amenityId);
        return amenities.isEmpty() ? Optional.empty() : Optional.of(amenities.get(0));
    }

    @Override
    public List<Amenity> getAmenities2(long neighborhoodId) {
        return jdbcTemplate.query("SELECT * FROM amenities WHERE neighborhoodId = ?", ROW_MAPPER2, neighborhoodId);
    }



    // ---------------------------------------------- AMENITY DELETE ---------------------------------------------------

    /*@Override
    public boolean deleteAmenity(long amenityId) {
        List<Number> hoursIds = jdbcTemplate.queryForList(
                "SELECT ah.hoursid FROM amenities_hours ah " +
                        "WHERE ah.amenityid = ?", Number.class, amenityId);

        boolean deleted = jdbcTemplate.update("DELETE FROM amenities WHERE amenityid = ?", amenityId) > 0;

        // delete hour from hours table if unused by any other amenity
        for (Number hoursId : hoursIds) {
            int count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM amenities_hours WHERE hoursid = ?",
                    Integer.class, hoursId);

            if (count == 0) {
                // If not associated with any other amenity, delete the hours entry
                jdbcTemplate.update("DELETE FROM hours WHERE hoursid = ?", hoursId);
            }
        }
        return deleted;
    }*/

}

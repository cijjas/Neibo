package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.models.DayTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

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
    private final SimpleJdbcInsert jdbcInsertHours;
    private final SimpleJdbcInsert jdbcInsertAmenityHours;

    @Autowired
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
    }

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
            jdbcInsertAmenityHours.execute(amenityHoursData);
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
        return jdbcTemplate.update("DELETE FROM amenities WHERE amenityid = ?", amenityId) > 0;
    }
}

package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.models.DayTime;
import ar.edu.itba.paw.models.Reservation;
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

    @Autowired
    public AmenityDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("amenityid")
                .withTableName("amenities");
        this.jdbcInsertHours = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("hoursid")
                .withTableName("hours");
    }

    // ---------------------------------------------- AMENITY INSERT ---------------------------------------------------

    @Override
    public Amenity createAmenity(String name, String description, Map<String, DayTime> dayHourData) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("description", description);

        final Number amenityId = jdbcInsert.executeAndReturnKey(data);

        // Insert opening/closing hours for each day of the week
        for (Map.Entry<String, DayTime> entry : dayHourData.entrySet()) {
            String dayOfWeek = entry.getKey();
            DayTime openingClosingTimes = entry.getValue();

            // Extract open and close times from DayTime
            Time openTime = openingClosingTimes.getOpenTime();
            Time closeTime = openingClosingTimes.getCloseTime();

            // Create a new record in the hours table for this day
            Map<String, Object> hoursData = new HashMap<>();
            hoursData.put("weekday", dayOfWeek);
            hoursData.put("opentime", openTime);
            hoursData.put("closetime", closeTime);
            hoursData.put("amenityId", amenityId.intValue()); // Convert amenityId to int

            // Insert into hours table
            jdbcInsertHours.execute(hoursData);
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
                .build();
    };

    //    @Override
    //    public boolean updateAmenity(long amenityId, String name, String description) {
    //        return jdbcTemplate.update("UPDATE amenities SET name = ?, description = ? WHERE amenityid = ?", name, description, amenityId) > 0;
    //    }

    @Override
    public Optional<Amenity> findAmenityById(long amenityId) {
        List<Amenity> amenities = jdbcTemplate.query("SELECT * FROM amenities WHERE amenityid = ?", ROW_MAPPER, amenityId);
        return amenities.isEmpty() ? Optional.empty() : Optional.of(amenities.get(0));
    }

    @Override
    public List<Amenity> getAmenities() {
        return jdbcTemplate.query("SELECT * FROM amenities", ROW_MAPPER);
    }

    @Override
    public Map<String, DayTime> getAmenityHoursByAmenityId(long amenityId) {
        Map<String, DayTime> amenityHoursMap = new HashMap<>();

        List<Map<String, Object>> results = jdbcTemplate.queryForList(
                "SELECT weekday, opentime, closetime " +
                        "FROM hours " +
                        "WHERE amenityId = ?",
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
//                amenityHoursMap.put(dayOfWeek, new DayTime.Builder().openTime(openTime).closeTime(closeTime).build());
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
                "SELECT opentime, closetime FROM hours WHERE amenityId = ? AND weekday = ?",
                amenityId,
                dayOfWeek
        );

        if (!results.isEmpty()) {
            Map<String, Object> firstResult = results.get(0);
            openTime = (Time) firstResult.get("opentime");
            closeTime = (Time) firstResult.get("closetime");
        }

//        return new DayTime.Builder().openTime(openTime).closeTime(closeTime).build();
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

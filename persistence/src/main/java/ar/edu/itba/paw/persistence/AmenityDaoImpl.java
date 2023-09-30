package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.models.Amenity;
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
                .withTableName("amenity");
        this.jdbcInsertHours = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("hoursid")
                .withTableName("hours");
    }

    // ---------------------------------------------- AMENITY INSERT ---------------------------------------------------

    @Override
    public Amenity createAmenity(String name, String description, Map<String, Map<Time, Time>> dayHourData) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("description", description);

        final Number amenityId = jdbcInsert.executeAndReturnKey(data);

        //insert opening/closing hours for each day of the week
        for (Map.Entry<String, Map<Time, Time>> entry : dayHourData.entrySet()) {
            String dayOfWeek = entry.getKey();
            Map<Time, Time> openingClosingTimes = entry.getValue();

            for (Map.Entry<Time, Time> timeEntry : openingClosingTimes.entrySet()) {
                Time openTime = timeEntry.getKey();
                Time closeTime = timeEntry.getValue();

                // Create a new record in the hours table for this day
                Map<String, Object> hoursData = new HashMap<>();
                hoursData.put("weekday", dayOfWeek);
                hoursData.put("opentime", openTime);
                hoursData.put("closetime", closeTime);
                hoursData.put("amenityId", amenityId.intValue()); // Convert amenityId to int

                // Insert into hours table
                jdbcInsertHours.execute(hoursData);
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
                .build();
    };

    @Override
    public Optional<Amenity> findAmenityById(long amenityId) {
        List<Amenity> amenities = jdbcTemplate.query("SELECT * FROM amenity WHERE amenityid = ?", ROW_MAPPER, amenityId);
        return amenities.isEmpty() ? Optional.empty() : Optional.of(amenities.get(0));
    }

    @Override
    public List<Amenity> getAmenities() {
        return jdbcTemplate.query("SELECT * FROM amenity", ROW_MAPPER);
    }

    @Override
    public Map<Time, Time> getAmenityHoursByDay(long amenityId, String dayOfWeek) {
        Map<Time, Time> openingClosingHours = new HashMap<>();

        List<Map<String, Object>> results = jdbcTemplate.queryForList(
                "SELECT opentime, closetime FROM hours WHERE amenityId = ? AND weekday = ?",
                amenityId,
                dayOfWeek
        );

        for (Map<String, Object> row : results) {
            Time openTime = (Time) row.get("opentime");
            Time closeTime = (Time) row.get("closetime");
            openingClosingHours.put(openTime, closeTime);
        }

        return openingClosingHours;
    }

    // ---------------------------------------------- AMENITY DELETE ---------------------------------------------------

    @Override
    public boolean deleteAmenity(long amenityId) {
        return jdbcTemplate.update("DELETE FROM amenity WHERE amenityid = ?", amenityId) > 0;
    }
}

package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.AttendanceDao;
import ar.edu.itba.paw.interfaces.persistence.SubscriptionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class AttendanceDaoImpl implements AttendanceDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public AttendanceDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("events_users");
    }

    // ---------------------------------------------- EVENTS_USERS INSERT ------------------------------------------------

    @Override
    public void createAttendee(long userId, long eventId) {
        Map<String, Object> data = new HashMap<>();
        data.put("userid", userId);
        data.put("eventid", eventId);
        jdbcInsert.execute(data);
    }

    @Override
    public boolean deleteAttendee(long userId, long eventId) {
        final String sql = "delete from events_users where userid = ? and eventid = ?";
        return jdbcTemplate.update(sql, userId, eventId) > 0;
    }
}
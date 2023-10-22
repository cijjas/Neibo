package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.DayDao;
import ar.edu.itba.paw.models.MainEntities.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.Optional;

@Repository
public class DayDaoImpl implements DayDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(DayDaoImpl.class);
    @PersistenceContext
    private EntityManager em;
    private static final RowMapper<Day> ROW_MAPPER =
            (rs, rowNum) -> new Day.Builder()
                    .dayId(rs.getLong("dayid"))
                    .dayName(rs.getString("dayname"))
                    .build();
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String DAYS = "SELECT * FROM days ";

    // ------------------------------------------------ DAYS INSERT ----------------------------------------------------

    @Autowired
    public DayDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("days")
                .usingGeneratedKeyColumns("dayid");
    }

    // ------------------------------------------------ DAYS SELECT ----------------------------------------------------

    @Override
    public Day createDay(String dayName) {
        LOGGER.debug("Inserting Day {}", dayName);
        Day day = new Day.Builder()
                .dayName(dayName)
                .build();
        em.persist(day);
        return day;
    }

    @Override
    public Optional<Day> findDayById(long dayId) {
        LOGGER.debug("Selecting Day with id {}", dayId);
        return Optional.ofNullable(em.find(Day.class, dayId));
    }
}

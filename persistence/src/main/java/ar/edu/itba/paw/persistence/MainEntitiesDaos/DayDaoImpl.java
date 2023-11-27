package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.DayDao;
import ar.edu.itba.paw.models.Entities.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class DayDaoImpl implements DayDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(DayDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // ------------------------------------------------ DAYS INSERT ----------------------------------------------------

    @Override
    public Day createDay(String dayName) {
        LOGGER.debug("Inserting Day {}", dayName);
        Day day = new Day.Builder()
                .dayName(dayName)
                .build();
        em.persist(day);
        return day;
    }

    // ------------------------------------------------ DAYS SELECT ----------------------------------------------------

    @Override
    public Optional<Day> findDayById(long dayId) {
        LOGGER.debug("Selecting Day with id {}", dayId);
        return Optional.ofNullable(em.find(Day.class, dayId));
    }
}

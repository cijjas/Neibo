package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.models.Entities.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class TimeDaoImpl implements TimeDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // ----------------------------------------------- TIMES INSERT ----------------------------------------------------

    @Override
    public Time createTime(java.sql.Time timeInterval) {
        LOGGER.debug("Inserting Time {}", timeInterval.getTime());

        Time time = new Time.Builder()
                .timeInterval(timeInterval)
                .build();
        em.persist(time);
        return time;
    }

    // ----------------------------------------------- TIMES SELECT ----------------------------------------------------

    @Override
    public Optional<Time> findTime(long timeId) {
        LOGGER.debug("Selecting Time with Time Id {}", timeId);

        return Optional.ofNullable(em.find(Time.class, timeId));
    }

    @Override
    public Optional<Time> findTime(java.sql.Time time) {
        LOGGER.debug("Selecting Time with SQL Time {}", time);

        TypedQuery<Time> query = em.createQuery("SELECT t FROM Time t WHERE t.timeInterval = :timeInterval", Time.class);
        query.setParameter("timeInterval", time);
        List<Time> resultList = query.getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
    }
}

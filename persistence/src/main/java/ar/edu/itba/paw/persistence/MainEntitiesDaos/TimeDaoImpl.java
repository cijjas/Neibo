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
import java.util.OptionalLong;

@Repository
public class TimeDaoImpl implements TimeDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // ----------------------------------------------- TIMES INSERT ----------------------------------------------------

    @Override
    public Time createTime(java.sql.Time timeInterval) {
        LOGGER.debug("Inserting Time {}", timeInterval.getTime());
        Time time =  new Time.Builder()
                .timeInterval(timeInterval)
                .build();
        em.persist(time);
        return time;
    }

    // ----------------------------------------------- TIMES SELECT ----------------------------------------------------

    @Override
    public Optional<Time> findTime(long timeId) {
        LOGGER.debug("Selecting Time with timeId {}", timeId);
        return Optional.ofNullable(em.find(Time.class, timeId));
    }

    @Override
    public OptionalLong findId(java.sql.Time time) {
        LOGGER.debug("Selecting Id with time {}", time);
        TypedQuery<Long> query = em.createQuery("SELECT t.id FROM Time t WHERE t.timeInterval = :timeInterval", Long.class);
        query.setParameter("timeInterval", time);
        List<Long> resultList = query.getResultList();
        return resultList.isEmpty() ? OptionalLong.empty() : OptionalLong.of(resultList.get(0));
    }
}

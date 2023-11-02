package ar.edu.itba.paw.persistence.JunctionDaos;

        import ar.edu.itba.paw.enums.Table;
        import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
        import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
        import ar.edu.itba.paw.models.JunctionEntities.ChannelMapping;
        import ar.edu.itba.paw.models.JunctionEntities.Specialization;
        import ar.edu.itba.paw.models.MainEntities.Channel;
        import ar.edu.itba.paw.models.MainEntities.Neighborhood;
        import ar.edu.itba.paw.models.MainEntities.Profession;
        import ar.edu.itba.paw.models.MainEntities.Worker;
        import org.hibernate.jdbc.Work;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.dao.DataAccessException;
        import org.springframework.jdbc.core.JdbcTemplate;
        import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
        import org.springframework.stereotype.Repository;

        import javax.persistence.EntityManager;
        import javax.persistence.PersistenceContext;
        import javax.persistence.TypedQuery;
        import javax.sql.DataSource;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

@Repository
public class ProfessionWorkerDaoImpl implements ProfessionWorkerDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionWorkerDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // --------------------------------------- WORKERS_PROFESSIONS INSERT ----------------------------------------------

    @Override
    public Specialization createSpecialization(long workerId, long professionId) {
        LOGGER.debug("Inserting Worker Profession");
        Specialization specialization = new Specialization(em.find(Worker.class, workerId), em.find(Profession.class, professionId));
        em.persist(specialization);
        return specialization;
    }

    // --------------------------------------- WORKERS_PROFESSIONS SELECT ----------------------------------------------

    @Override
    public List<Profession> getWorkerProfessions(long workerId) {
        LOGGER.debug("Selecting Professions of Worker {}", workerId);
        TypedQuery<Profession> query = em.createQuery("SELECT p FROM Worker w JOIN w.professions p WHERE w.user.id = :workerId", Profession.class);
        query.setParameter("workerId", workerId);
        return query.getResultList();
    }
}

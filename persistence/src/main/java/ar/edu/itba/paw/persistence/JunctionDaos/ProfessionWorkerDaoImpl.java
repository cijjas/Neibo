package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
import ar.edu.itba.paw.models.Entities.Profession;
import ar.edu.itba.paw.models.Entities.Specialization;
import ar.edu.itba.paw.models.Entities.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class ProfessionWorkerDaoImpl implements ProfessionWorkerDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionWorkerDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // --------------------------------------- WORKERS PROFESSIONS INSERT ----------------------------------------------

    @Override
    public Specialization createSpecialization(long workerId, long professionId) {
        LOGGER.debug("Inserting Worker Profession");
        Specialization specialization = new Specialization(em.find(Worker.class, workerId), em.find(Profession.class, professionId));
        em.persist(specialization);
        return specialization;
    }

    // --------------------------------------- WORKERS PROFESSIONS SELECT ----------------------------------------------

    @Override
    public List<Profession> getWorkerProfessions(long workerId) {
        LOGGER.debug("Selecting Professions of Worker {}", workerId);
        TypedQuery<Profession> query = em.createQuery("SELECT p FROM Worker w JOIN w.professions p WHERE w.user.id = :workerId", Profession.class);
        query.setParameter("workerId", workerId);
        return query.getResultList();
    }
}

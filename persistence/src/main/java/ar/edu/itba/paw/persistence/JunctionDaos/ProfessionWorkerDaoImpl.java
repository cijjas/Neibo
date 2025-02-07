package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
import ar.edu.itba.paw.models.Entities.Profession;
import ar.edu.itba.paw.models.Entities.Specialization;
import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.models.compositeKeys.SpecializationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class ProfessionWorkerDaoImpl implements ProfessionWorkerDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionWorkerDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // --------------------------------------- WORKERS PROFESSIONS INSERT ----------------------------------------------

    @Override
    public Specialization createSpecialization(long workerId, long professionId) {
        LOGGER.debug("Inserting Specialization with Worker Id {} and Profession Id {}", workerId, professionId);

        Specialization specialization = new Specialization(em.find(Worker.class, workerId), em.find(Profession.class, professionId));
        em.persist(specialization);
        return specialization;
    }

    // --------------------------------------- WORKERS PROFESSIONS SELECT ----------------------------------------------

    @Override
    public Optional<Specialization> findSpecialization(long workerId, long professionId) {
        LOGGER.debug("Selecting Specialization with Worker Id {} and Profession Id {}", workerId, professionId);

        return Optional.ofNullable(em.find(Specialization.class, new SpecializationKey(workerId, professionId)));
    }
}

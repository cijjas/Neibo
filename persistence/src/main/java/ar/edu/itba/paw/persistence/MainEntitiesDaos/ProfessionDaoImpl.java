package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.ProfessionDao;
import ar.edu.itba.paw.models.Entities.Profession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class ProfessionDaoImpl implements ProfessionDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // ------------------------------------------------ PROFESSION INSERT ----------------------------------------------

    @Override
    public Profession createProfession(String professionName) {
        LOGGER.debug("Inserting Profession {}", professionName);
        final Profession profession = new Profession.Builder()
                .profession(professionName)
                .build();
        em.persist(profession);
        return profession;
    }

    // ------------------------------------------------ PROFESSION SELECT ----------------------------------------------

    @Override
    public List<Profession> getProfessions(Long workerId) {
        LOGGER.debug("Selecting Professions for workerId {}", workerId);

        StringBuilder queryString = new StringBuilder("SELECT p FROM Profession p");

        if (workerId != null) {
            queryString.append(" JOIN p.workers w WHERE w.user.id = :workerId");
        }

        TypedQuery<Profession> query = em.createQuery(queryString.toString(), Profession.class);

        if (workerId != null) {
            query.setParameter("workerId", workerId);
        }

        return query.getResultList();
    }

    @Override
    public Optional<Profession> findProfession(long professionId) {
        LOGGER.debug("Selecting Profession {}", professionId);

        return Optional.ofNullable(em.find(Profession.class, professionId));
    }

    @Override
    public boolean deleteProfession(long professionId) {
        LOGGER.debug("Deleting Profession with id {}", professionId);
        Profession profession = em.find(Profession.class, professionId);
        if (profession!= null) {
            em.remove(profession);
            return true;
        }
        return false;
    }
}
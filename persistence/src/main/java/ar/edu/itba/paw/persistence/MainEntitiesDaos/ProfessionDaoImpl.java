package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.enums.Professions;
import ar.edu.itba.paw.interfaces.persistence.ProfessionDao;
import ar.edu.itba.paw.models.Entities.Profession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class ProfessionDaoImpl implements ProfessionDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // ------------------------------------------------ PROFESSION INSERT ----------------------------------------------

    @Override
    public Profession createProfession(Professions professionType) {
        LOGGER.debug("Inserting Profession {}", professionType);
        final Profession profession = new Profession.Builder()
                .profession(professionType)
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
}

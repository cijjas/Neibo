package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.enums.Professions;
import ar.edu.itba.paw.interfaces.persistence.ProfessionDao;
import ar.edu.itba.paw.models.Entities.Profession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
}

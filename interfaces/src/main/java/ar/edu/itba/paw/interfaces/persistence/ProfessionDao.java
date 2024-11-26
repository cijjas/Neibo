package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Profession;

import java.util.List;
import java.util.Optional;

public interface ProfessionDao {

    // --------------------------------------------- PROFESSIONS INSERT -------------------------------------------------

    Profession createProfession(String professionName);

    // --------------------------------------------- PROFESSIONS SELECT -------------------------------------------------

    Optional<Profession> findProfession(long professionId);

    List<Profession> getProfessions(Long workerId);

    // --------------------------------------------- PROFESSIONS DELETE -------------------------------------------------

    boolean deleteProfession(long professionId);
}

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Profession;

import java.util.List;
import java.util.Optional;

public interface ProfessionDao {

    // --------------------------------------------- PROFESSIONS INSERT -------------------------------------------------

    Profession createProfession(String professionName);

    // --------------------------------------------- PROFESSIONS SELECT -------------------------------------------------

    List<Profession> getProfessions(Long workerId);

    Optional<Profession> findProfession(long professionId);

    // --------------------------------------------- PROFESSIONS DELETE -------------------------------------------------

    boolean deleteProfession(long professionId);
}

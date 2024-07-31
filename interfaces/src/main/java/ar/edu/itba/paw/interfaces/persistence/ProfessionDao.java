package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.enums.Professions;
import ar.edu.itba.paw.models.Entities.Profession;

import java.util.List;

public interface ProfessionDao {

    // --------------------------------------------- PROFESSIONS INSERT -------------------------------------------------

    Profession createProfession(Professions professionName);

    // --------------------------------------------- PROFESSIONS SELECT -------------------------------------------------

    List<Profession> getProfessions(Long workerId);
}

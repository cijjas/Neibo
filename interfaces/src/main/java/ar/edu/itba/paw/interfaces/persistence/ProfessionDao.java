package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.enums.Professions;
import ar.edu.itba.paw.models.Entities.Profession;

import java.util.List;

public interface ProfessionDao {

    // --------------------------------------------- PROFESSIONS INSERT -------------------------------------------------

    Profession createProfession(String professionName);

    // --------------------------------------------- PROFESSIONS SELECT -------------------------------------------------

    List<Profession> getProfessions(Long workerId);
}

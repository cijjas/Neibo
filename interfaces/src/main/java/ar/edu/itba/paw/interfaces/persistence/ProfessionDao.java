package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.enums.Professions;
import ar.edu.itba.paw.models.Entities.Profession;

public interface ProfessionDao {

    // --------------------------------------------- PROFESSIONS INSERT -------------------------------------------------

    Profession createProfession(Professions professionName);
}

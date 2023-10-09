package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Profession;

import java.util.List;

public interface ProfessionDao {
    // ---------------------------------------------- PROFESSIONS SELECT -----------------------------------------------
    List<Profession> getProfessions();
}

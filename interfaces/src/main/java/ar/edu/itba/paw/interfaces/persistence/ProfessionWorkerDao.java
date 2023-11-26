package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.JunctionEntities.Specialization;
import ar.edu.itba.paw.models.MainEntities.Profession;

import java.util.List;

public interface ProfessionWorkerDao {

    // --------------------------------------- WORKERS PROFESSIONS INSERT ----------------------------------------------

    Specialization createSpecialization(long workerId, long professionId);

    // --------------------------------------- WORKERS PROFESSIONS SELECT ----------------------------------------------

    List<Profession> getWorkerProfessions(long workerId);
}

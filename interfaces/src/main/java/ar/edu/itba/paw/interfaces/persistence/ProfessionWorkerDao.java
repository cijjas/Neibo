package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Specialization;

public interface ProfessionWorkerDao {

    // --------------------------------------- WORKERS PROFESSIONS INSERT ----------------------------------------------

    Specialization createSpecialization(long workerId, long professionId);

}

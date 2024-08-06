package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Specialization;

import java.util.Optional;

public interface ProfessionWorkerDao {

    // --------------------------------------- WORKERS PROFESSIONS INSERT ----------------------------------------------

    Specialization createSpecialization(long workerId, long professionId);

    // --------------------------------------- WORKERS PROFESSIONS SELECT ----------------------------------------------

    Optional<Specialization> findSpecialization(long workerId, long professionId);

}

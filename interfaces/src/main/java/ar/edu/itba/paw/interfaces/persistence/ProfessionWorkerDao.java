package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Profession;
import ar.edu.itba.paw.models.Entities.Specialization;

import java.util.List;

public interface ProfessionWorkerDao {

    // --------------------------------------- WORKERS PROFESSIONS INSERT ----------------------------------------------

    Specialization createSpecialization(long workerId, long professionId);

}

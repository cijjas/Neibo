package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;

public interface ProfessionWorkerDao {

    // --------------------------------------- WORKERS_PROFESSIONS INSERT ----------------------------------------------

    void addWorkerProfession(long workerId, long professionId);

    // --------------------------------------- WORKERS_PROFESSIONS SELECT ----------------------------------------------

    List<String> getWorkerProfessions(long workerId);
}

package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;

public interface ProfessionWorkerDao {
    // --------------------------------------- PROFESSIONWORKERS INSERT ------------------------------------------------
    void addWorkerProfession(long workerId, long professionId);

    // --------------------------------------- PROFESSIONWORKERS SELECT ------------------------------------------------
    List<String> getWorkerProfessions(long workerId);
}

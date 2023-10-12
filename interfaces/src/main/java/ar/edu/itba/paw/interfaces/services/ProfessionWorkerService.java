package ar.edu.itba.paw.interfaces.services;

import java.util.List;

public interface ProfessionWorkerService {
    // --------------------------------------- PROFESSIONWORKERS INSERT ------------------------------------------------
    void addWorkerProfession(long workerId, long professionId);

    // --------------------------------------- PROFESSIONWORKERS SELECT ------------------------------------------------
    List<String> getWorkerProfessions(long workerId);
}

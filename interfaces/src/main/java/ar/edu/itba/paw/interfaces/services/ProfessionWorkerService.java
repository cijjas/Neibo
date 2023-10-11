package ar.edu.itba.paw.interfaces.services;

public interface ProfessionWorkerService {
    // --------------------------------------- PROFESSIONWORKERS INSERT ------------------------------------------------
    void addWorkerProfession(long workerId, long professionId);

    // --------------------------------------- PROFESSIONWORKERS SELECT ------------------------------------------------
    String getWorkerProfession(long workerId);
}

package ar.edu.itba.paw.interfaces.persistence;

public interface ProfessionWorkerDao {
    // --------------------------------------- PROFESSIONWORKERS INSERT ------------------------------------------------
    void addWorkerProfession(long workerId, long professionId);

    // --------------------------------------- PROFESSIONWORKERS SELECT ------------------------------------------------
    String getWorkerProfession(long workerId);
}

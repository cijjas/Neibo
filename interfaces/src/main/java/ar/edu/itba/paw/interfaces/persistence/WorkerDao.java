package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.enums.WorkerStatus;
import ar.edu.itba.paw.models.Entities.Worker;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface WorkerDao {

    // ---------------------------------------------- WORKERS INSERT ---------------------------------------------------

    Worker createWorker(long workerId, String phoneNumber, String address, String businessName);

    // ---------------------------------------------- WORKERS SELECT ---------------------------------------------------

    Optional<Worker> findWorker(long workerId);

    List<Worker> getWorkers(int page, int size, List<String> professions, List<Long> neighborhoodIds, String workerRole, String workerStatus);

    int countWorkers(List<String> professions, List<Long> neighborhoodIds, String workerRole, String workerStatus);
}

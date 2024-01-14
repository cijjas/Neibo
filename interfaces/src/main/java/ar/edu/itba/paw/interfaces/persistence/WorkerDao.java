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

    Set<Worker> getWorkers(int page, int size, List<String> professions, long[] neighborhoodIds, WorkerRole workerRole, WorkerStatus workerStatus);

    int countWorkers(List<String> professions, long[] neighborhoodIds, WorkerRole workerRole, WorkerStatus workerStatus);
}

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Worker;

import java.util.List;
import java.util.Optional;

public interface WorkerDao {

    // ---------------------------------------------- WORKERS INSERT ---------------------------------------------------

    Worker createWorker(long workerId, String phoneNumber, String address, String businessName);

    // ---------------------------------------------- WORKERS SELECT ---------------------------------------------------

    Optional<Worker> findWorker(long workerId);

    List<Worker> getWorkers(List<Long> neighborhoodIds, List<Long> professionIds, Long workerRoleId, Long workerStatusId, int page, int size);

    int countWorkers(List<Long> neighborhoodIds, List<Long> professionIds, Long workerRoleId, Long workerStatusId);
}

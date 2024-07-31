package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Worker;

import java.util.List;
import java.util.Optional;

public interface WorkerDao {

    // ---------------------------------------------- WORKERS INSERT ---------------------------------------------------

    Worker createWorker(long workerId, String phoneNumber, String address, String businessName);

    // ---------------------------------------------- WORKERS SELECT ---------------------------------------------------

    Optional<Worker> findWorker(long workerId);

    List<Worker> getWorkers(int page, int size, List<Long> professionIds, List<Long> neighborhoodIds, Long workerRoleId, Long workerStatusId);

    int countWorkers(List<Long> professionIds, List<Long> neighborhoodIds, Long workerRoleId, Long workerStatusId);

    // ---------------------------------------------- WORKERS DELETE ---------------------------------------------------

    boolean deleteWorker(long workerId);
}

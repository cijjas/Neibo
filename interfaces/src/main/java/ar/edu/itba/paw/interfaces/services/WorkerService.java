package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Worker;

import java.util.List;
import java.util.Optional;

public interface WorkerService {

    Worker createWorker(long userId, String phoneNumber, String address, List<Long> professionIds, String businessName);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Worker> findWorker(long workerId);

    List<Worker> getWorkers(int page, int size, List<String> professionURNs, List<String> neighborhoodURNs, String workerRoleURN, String workerStatusURN);

    // ---------------------------------------------------

    int calculateWorkerPages(List<String> professionURNs, List<String> neighborhoodURNs, int size, String workerRoleURN, String workerStatusURN);

    // -----------------------------------------------------------------------------------------------------------------

    Worker updateWorkerPartially(long userId, String phoneNumber, String address, String businessName, Long backgroundPictureId, String bio);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteWorker(long workerId);
}

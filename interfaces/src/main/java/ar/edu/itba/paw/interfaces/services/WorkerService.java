package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Worker;

import java.util.List;
import java.util.Optional;

public interface WorkerService {

    Worker createWorker(long userId, List<Long> professionIds, String businessName, String address, String phoneNumber);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Worker> findWorker(long workerId);

    List<Worker> getWorkers(List<Long> neighborhoodIds, List<Long> professionIds, Long workerRoleId, Long workerStatusId, int page, int size);

    int calculateWorkerPages(List<Long> neighborhoodIds, List<Long> professionIds, Long workerRoleId, Long workerStatusId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Worker updateWorker(long userId, String businessName, String address, String phoneNumber, Long backgroundPictureId, String bio);
}

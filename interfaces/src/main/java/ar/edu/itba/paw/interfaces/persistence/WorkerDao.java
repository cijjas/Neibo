package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.models.MainEntities.Worker;

import java.util.List;
import java.util.Optional;

public interface WorkerDao {

    // ---------------------------------------------- WORKERS INSERT ---------------------------------------------------

    Worker createWorker(long workerId, String phoneNumber, String address, String businessName);

    // ---------------------------------------------- WORKERS SELECT ---------------------------------------------------

    Optional<Worker> findWorkerById(long workerId);

    List<Worker> getWorkersByCriteria(int page, int size, List<String> professions, long[] neighborhoodIds, WorkerRole workerRole);

    int getWorkersCountByCriteria(List<String> professions, long[] neighborhoodIds, WorkerRole workerRole);

    // ---------------------------------------------- WORKERS UPDATE ---------------------------------------------------

    Worker updateWorker(long userId, String phoneNumber, String address, String businessName, long backgroundPictureId, String bio);

}

package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Worker;
import enums.Language;

import java.util.List;
import java.util.Optional;

public interface WorkerService {

    // ---------------------------------------------- WORKERS INSERT -----------------------------------------------------
    Worker createWorker(String mail, String name, String surname, String password, int identification, String phoneNumber, String address, Language language, long professionId, String businessName);
    // ---------------------------------------------- WORKERS SELECT -----------------------------------------------------
    Optional<Worker> findWorkerById(long workerId);

    Optional<Worker> findWorkerByMail(String mail);

    List<Worker> getWorkersByCriteria(int page, int size, List<String> professions, long neighborhoodId);

    int getWorkersCountByCriteria(List<String> professions, long neighborhoodId);

    // ---------------------------------------------- WORKERS UPDATE -----------------------------------------------------
    void updateWorker(long userId, String name, String surname, String password, int identification, String phoneNumber,
                      String address, Language language, boolean darkMode, String businessName, long profilePictureId,
                      long backgroundPictureId, String bio);
}

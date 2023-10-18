package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.models.Worker;
import ar.edu.itba.paw.enums.Language;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface WorkerService {

    // ---------------------------------------------- WORKERS INSERT -----------------------------------------------------
    Worker createWorker(String mail, String name, String surname, String password, int identification, String phoneNumber, String address, Language language, long[] professionIds, String businessName);
    // ---------------------------------------------- WORKERS SELECT -----------------------------------------------------
    Optional<Worker> findWorkerById(long workerId);

    Optional<Worker> findWorkerByMail(String mail);

    List<Worker> getWorkersByCriteria(int page, int size, List<String> professions, long neighborhoodId, long loggedUserId);

    int getWorkersCountByCriteria(List<String> professions, long[] neighborhoodIds);

    // ---------------------------------------------- WORKERS UPDATE -----------------------------------------------------
    void updateWorker(long userId, String phoneNumber, String address, String businessName,
                      MultipartFile backgroundPicture, String bio);

    int getTotalWorkerPages(long neighborhoodId, int size);
}

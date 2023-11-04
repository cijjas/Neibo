package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.models.MainEntities.Worker;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface WorkerService {

    Worker createWorker(String mail, String name, String surname, String password, String identification, String phoneNumber, String address, Language language, long[] professionIds, String businessName);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Worker> findWorkerById(long workerId);

    Optional<Worker> findWorkerByMail(String mail);

    List<Worker> getWorkersByCriteria(int page, int size, List<String> professions, long neighborhoodId, long loggedUserId, WorkerRole workerRole);

    int getWorkersCountByCriteria(List<String> professions, long[] neighborhoodIds, WorkerRole workerRole);

    // -----------------------------------------------------------------------------------------------------------------

    void updateWorker(long userId, String phoneNumber, String address, String businessName,
                      MultipartFile backgroundPicture, String bio);

    int getTotalWorkerPages(long neighborhoodId, int size);

    int getTotalWorkerPagesByCriteria(List<String> professions, long[] neighborhoodIds, int size, WorkerRole workerRole);

}

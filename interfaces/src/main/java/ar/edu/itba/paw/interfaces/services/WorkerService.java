package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.enums.WorkerStatus;
import ar.edu.itba.paw.models.Entities.Worker;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface WorkerService {

    Worker createWorker(String mail, String name, String surname, String password, String identification, String phoneNumber, String address, String languageURN, String[] professionURNs, String businessName);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Worker> findWorker(long workerId);

    Optional<Worker> findWorker(String mail);

    Set<Worker> getWorkers(int page, int size, List<String> professions, List<String> neighborhoodIds, String workerRole, String workerStatus);

    int countWorkers(List<String> professions, List<String> neighborhoodIds, String workerRole, String workerStatus);

    int calculateWorkerPages(List<String> professions, List<String> neighborhoodIds, int size, String workerRole, String workerStatus);

    // -----------------------------------------------------------------------------------------------------------------

    Worker updateWorkerPartially(long userId, String phoneNumber, String address, String businessName, MultipartFile backgroundPicture, String bio);
}

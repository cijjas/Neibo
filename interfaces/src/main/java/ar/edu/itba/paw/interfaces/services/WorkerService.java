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

    Worker createWorker(String userURN, String phoneNumber, String address, String[] professionURNs, String businessName);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Worker> findWorker(long workerId);

    Optional<Worker> findWorker(String mail);

    List<Worker> getWorkers(int page, int size, List<String> professionURNs, List<String> neighborhoodURNs, String workerRoleURN, String workerStatusURN);

    int countWorkers(List<String> professionURNs, List<String> neighborhoodURNs, String workerRoleURN, String workerStatusURN);

    int calculateWorkerPages(List<String> professionURNs, List<String> neighborhoodURNs, int size, String workerRoleURN, String workerStatusURN);

    // -----------------------------------------------------------------------------------------------------------------

    Worker updateWorkerPartially(long userId, String phoneNumber, String address, String businessName, String backgroundPictureURN, String bio);
}

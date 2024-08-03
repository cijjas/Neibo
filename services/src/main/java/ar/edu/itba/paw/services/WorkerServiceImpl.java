package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.WorkerDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.models.Entities.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WorkerServiceImpl implements WorkerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerServiceImpl.class);
    private final WorkerDao workerDao;
    private final ProfessionWorkerDao professionWorkerDao;
    private final UserDao userDao;
    private final ImageService imageService;

    @Autowired
    public WorkerServiceImpl(WorkerDao workerDao, ProfessionWorkerDao professionWorkerDao, UserDao userDao, ImageService imageService) {
        this.workerDao = workerDao;
        this.professionWorkerDao = professionWorkerDao;
        this.userDao = userDao;
        this.imageService = imageService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Worker createWorker(String userURN, String phoneNumber, String address, String[] professionURNs, String businessName) {
        LOGGER.info("Creating Worker associated with User {}", userURN);

        Long userId = ValidationUtils.checkURNAndExtractUserWorkerId(userURN);

        Worker worker = workerDao.createWorker(userId, phoneNumber, address, businessName);
        for (String urn : professionURNs) {
            long professionId = ValidationUtils.extractURNId(urn);
            ValidationUtils.checkProfessionId(professionId);
            professionWorkerDao.createSpecialization(userId, professionId);
        }
        return worker;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Worker> findWorker(long userId) {
        LOGGER.info("Finding Worker {}", userId);

        ValidationUtils.checkUserId(userId);

        Optional<User> optionalUser = userDao.findUser(userId);
        return optionalUser.isPresent() ? workerDao.findWorker(userId) : Optional.empty();
    }

    @Override
    public List<Worker> getWorkers(int page, int size, List<String> professionURNs, List<String> neighborhoodURNs, String workerRoleURN, String workerStatusURN) {
        LOGGER.info("Getting Workers with Status {} Role {} Professions {} from Neighborhoods {}", workerStatusURN, workerRoleURN, professionURNs, neighborhoodURNs);

        Long workerRoleId = ValidationUtils.checkURNAndExtractWorkerRoleId(workerRoleURN);
        Long workerStatusId = ValidationUtils.checkURNAndExtractWorkerStatusId(workerStatusURN);
        List<Long> professionIds = new ArrayList<>();
        if (professionURNs != null)
            for (String professionURN : professionURNs)
                professionIds.add(ValidationUtils.checkURNAndExtractProfessionId(professionURN));
        List<Long> neighborhoodIds = new ArrayList<>();
        if (neighborhoodURNs != null)
            for (String neighborhoodURN : neighborhoodURNs)
                neighborhoodIds.add(ValidationUtils.checkURNAndExtractNeighborhoodId(neighborhoodURN));

        ValidationUtils.checkPageAndSize(page, size);

        return workerDao.getWorkers(page, size, professionIds, neighborhoodIds, workerRoleId, workerStatusId);
    }

    // ---------------------------------------------------

    @Override
    public int calculateWorkerPages(List<String> professionURNs, List<String> neighborhoodURNs, int size, String workerRoleURN, String workerStatusURN) {
        LOGGER.info("Calculating Worker Pages with Status {} Role {} Professions {} from Neighborhoods {}", workerStatusURN, workerRoleURN, professionURNs, neighborhoodURNs);

        Long workerRoleId = ValidationUtils.checkURNAndExtractWorkerRoleId(workerRoleURN);
        Long workerStatusId = ValidationUtils.checkURNAndExtractWorkerStatusId(workerStatusURN);
        List<Long> professionIds = new ArrayList<>();
        if (professionURNs != null)
            for (String professionURN : professionURNs)
                professionIds.add(ValidationUtils.checkURNAndExtractProfessionId(professionURN));
        List<Long> neighborhoodIds = new ArrayList<>();
        if (neighborhoodURNs != null)
            for (String neighborhoodURN : neighborhoodURNs)
                neighborhoodIds.add(ValidationUtils.checkURNAndExtractNeighborhoodId(neighborhoodURN));

        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(workerDao.countWorkers(professionIds, neighborhoodIds, workerRoleId, workerStatusId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Worker updateWorkerPartially(long workerId, String phoneNumber, String address, String businessName, String backgroundPictureURN, String bio) {
        LOGGER.info("Updating Worker {}", workerId);

        Worker worker = workerDao.findWorker(workerId).orElseThrow(() -> new NotFoundException("Worker Not Found"));
        if (phoneNumber != null && !phoneNumber.isEmpty())
            worker.setPhoneNumber(phoneNumber);
        if (address != null && !address.isEmpty())
            worker.setAddress(address);
        if (businessName != null && !businessName.isEmpty())
            worker.setBusinessName(businessName);
        if (bio != null && !bio.isEmpty())
            worker.setBio(bio);
        if (backgroundPictureURN != null) {
            long imageId = ValidationUtils.extractURNId(backgroundPictureURN);
            ValidationUtils.checkImageId(imageId);
            Image i = imageService.findImage(imageId).orElseThrow(() -> new NotFoundException("Image not found"));
            worker.setBackgroundPictureId(i.getImageId());
        }

        return worker;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteWorker(long workerId) {
        LOGGER.info("Deleting Worker {}", workerId);

        ValidationUtils.checkTagId(workerId);

        return workerDao.deleteWorker(workerId);
    }
}

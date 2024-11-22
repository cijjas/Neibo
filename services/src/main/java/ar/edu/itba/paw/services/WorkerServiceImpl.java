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
    public Worker createWorker(long userId, String phoneNumber, String address, List<Long> professionIds, String businessName) {
        LOGGER.info("Creating Worker associated with User {}", userId);

        Worker worker = workerDao.createWorker(userId, phoneNumber, address, businessName);
        for (long id: professionIds) {
            professionWorkerDao.findSpecialization(userId, id).orElseGet(() -> professionWorkerDao.createSpecialization(userId, id));
        }

        return worker;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Worker> findWorker(long userId) {
        LOGGER.info("Finding Worker {}", userId);

        Optional<User> optionalUser = userDao.findUser(userId);
        return optionalUser.isPresent() ? workerDao.findWorker(userId) : Optional.empty();
    }

    @Override
    public List<Worker> getWorkers(int page, int size, List<Long> professionIds, List<Long> neighborhoodIds, Long workerRoleId, Long workerStatusId) {
        LOGGER.info("Getting Workers with Status {} Role {} Professions {} from Neighborhoods {}", workerStatusId, workerRoleId, professionIds, neighborhoodIds);

        return workerDao.getWorkers(page, size, professionIds, neighborhoodIds, workerRoleId, workerStatusId);
    }

    // ---------------------------------------------------

    @Override
    public int calculateWorkerPages(List<Long> professionIds, List<Long> neighborhoodIds, int size, Long workerRoleId, Long workerStatusId) {
        LOGGER.info("Calculating Worker Pages with Status {} Role {} Professions {} from Neighborhoods {}", workerStatusId, workerRoleId, professionIds, neighborhoodIds);

        return PaginationUtils.calculatePages(workerDao.countWorkers(professionIds, neighborhoodIds, workerRoleId, workerStatusId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Worker updateWorkerPartially(long workerId, String phoneNumber, String address, String businessName, Long backgroundPictureId, String bio) {
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
        if (backgroundPictureId != null) {
            Image i = imageService.findImage(backgroundPictureId).orElseThrow(() -> new NotFoundException("Image not found"));
            worker.setBackgroundPictureId(i.getImageId());
        }

        return worker;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteWorker(long workerId) {
        LOGGER.info("Deleting Worker {}", workerId);

        return workerDao.deleteWorker(workerId);
    }
}

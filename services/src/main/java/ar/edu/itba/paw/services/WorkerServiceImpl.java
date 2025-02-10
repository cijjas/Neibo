package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
import ar.edu.itba.paw.interfaces.persistence.WorkerDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.Entities.Image;
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
    private final ImageService imageService;

    @Autowired
    public WorkerServiceImpl(WorkerDao workerDao, ProfessionWorkerDao professionWorkerDao, ImageService imageService) {
        this.workerDao = workerDao;
        this.professionWorkerDao = professionWorkerDao;
        this.imageService = imageService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Worker createWorker(long userId, List<Long> professionIds, String businessName, String address, String phoneNumber) {
        LOGGER.info("Creating Worker associated with User {}, business name {}, address {}, phone number {} and Professions {}", userId, businessName, address, phoneNumber, professionIds);

        Worker worker = workerDao.createWorker(userId, phoneNumber, address, businessName);
        for (long id : professionIds) {
            professionWorkerDao.findSpecialization(userId, id).orElseGet(() -> professionWorkerDao.createSpecialization(userId, id));
        }

        return worker;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Worker> findWorker(long workerId) {
        LOGGER.info("Finding Worker {}", workerId);

        return workerDao.findWorker(workerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Worker> getWorkers(List<Long> neighborhoodIds, List<Long> professionIds, Long workerRoleId, Long workerStatusId, int page, int size) {
        LOGGER.info("Getting Workers with Status {} Role {} Professions {} from Neighborhoods {}", workerStatusId, workerRoleId, professionIds, neighborhoodIds);

        return workerDao.getWorkers(neighborhoodIds, professionIds, workerRoleId, workerStatusId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int countWorkers(List<Long> neighborhoodIds, List<Long> professionIds, Long workerRoleId, Long workerStatusId) {
        LOGGER.info("Calculating Worker Pages with Status {} Role {} Professions {} from Neighborhoods {}", workerStatusId, workerRoleId, professionIds, neighborhoodIds);

        return workerDao.countWorkers(neighborhoodIds, professionIds, workerRoleId, workerStatusId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Worker updateWorker(long workerId, String businessName, String address, String phoneNumber, Long backgroundPictureId, String bio) {
        LOGGER.info("Updating Worker {}", workerId);

        Worker worker = workerDao.findWorker(workerId).orElseThrow(NotFoundException::new);
        if (phoneNumber != null && !phoneNumber.isEmpty())
            worker.setPhoneNumber(phoneNumber);
        if (address != null && !address.isEmpty())
            worker.setAddress(address);
        if (businessName != null && !businessName.isEmpty())
            worker.setBusinessName(businessName);
        if (bio != null && !bio.isEmpty())
            worker.setBio(bio);
        if (backgroundPictureId != null) {
            Image i = imageService.findImage(backgroundPictureId).orElseThrow(NotFoundException::new);
            worker.setBackgroundPictureId(i);
        }

        return worker;
    }
}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.UnexpectedException;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodWorkerDao;
import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.WorkerDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.models.Entities.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class WorkerServiceImpl implements WorkerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerServiceImpl.class);
    private final WorkerDao workerDao;
    private final ProfessionWorkerDao professionWorkerDao;
    private final UserDao userDao;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;
    private final NeighborhoodWorkerDao neighborhoodWorkerDao;

    @Autowired
    public WorkerServiceImpl(WorkerDao workerDao, ProfessionWorkerDao professionWorkerDao, UserDao userDao, ImageService imageService, PasswordEncoder passwordEncoder, NeighborhoodWorkerDao neighborhoodWorkerDao) {
        this.workerDao = workerDao;
        this.professionWorkerDao = professionWorkerDao;
        this.userDao = userDao;
        this.imageService = imageService;
        this.passwordEncoder = passwordEncoder;
        this.neighborhoodWorkerDao = neighborhoodWorkerDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Worker createWorker(String mail, String name, String surname, String password, String identification, String phoneNumber, String address, Language language, Long[] professionIds, String businessName) {
        LOGGER.info("Creating Worker with mail {}", mail);

        int id = 0;
        try {
            id = Integer.parseInt(identification);
        } catch (NumberFormatException e) {
            LOGGER.error("Error whilst formatting Identification");
            throw new UnexpectedException("Unexpected Error while creating Worker");
        }

        User user = userDao.createUser(mail, passwordEncoder.encode(password), name, surname, BaseNeighborhood.WORKERS_NEIGHBORHOOD.getId(), language, false, UserRole.WORKER, id);
        Worker worker = workerDao.createWorker(user.getUserId(), phoneNumber, address, businessName);
        for (long professionId : professionIds) {
            professionWorkerDao.createSpecialization(user.getUserId(), professionId);
        }
        return worker;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Worker> findWorker(long userId) {
        LOGGER.info("Finding Worker with id {}", userId);

        ValidationUtils.checkUserId(userId);

        Optional<User> optionalUser = userDao.findUser(userId);
        return optionalUser.isPresent() ? workerDao.findWorker(userId) : Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Worker> findWorker(String mail) {
        LOGGER.info("Finding Worker with mail {}", mail);
        Optional<User> optionalUser = userDao.findUser(mail);
        return optionalUser.isPresent() ? workerDao.findWorker(optionalUser.get().getUserId()) : Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Worker> getWorkers(int page, int size, List<String> professions, long userId, WorkerRole workerRole, WorkerStatus workerStatus) {
        LOGGER.info("Getting Workers with professions {}", professions);

        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkPageAndSize(page, size);

        //If inquirer is a worker, show return workers from any of the neighborhoods they are in
        User user = userDao.findUser(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        if(user.getNeighborhood().getNeighborhoodId() == 0) { //inquirer is a worker
            Set<Neighborhood> neighborhoods = neighborhoodWorkerDao.getNeighborhoods(userId);
            long[] neighborhoodIds = neighborhoods.stream()
                    .mapToLong(Neighborhood::getNeighborhoodId)
                    .toArray();

            return workerDao.getWorkers(page, size, professions, neighborhoodIds, workerRole, workerStatus);
        } else {
            //user isn't a worker, must only return workers from their neighborhood
            return workerDao.getWorkers(page, size, professions, new long[]{user.getNeighborhood().getNeighborhoodId()}, workerRole, workerStatus);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int countWorkers(List<String> professions, long userId, WorkerRole workerRole, WorkerStatus workerStatus) {
        LOGGER.info("Getting Workers Count for User {} with professions {}", userId, professions);

        ValidationUtils.checkId(userId, "User");

        User user = userDao.findUser(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        if(user.getNeighborhood().getNeighborhoodId() == 0) { //inquirer is a worker
            Set<Neighborhood> neighborhoods = neighborhoodWorkerDao.getNeighborhoods(userId);
            long[] neighborhoodIds = neighborhoods.stream()
                    .mapToLong(Neighborhood::getNeighborhoodId)
                    .toArray();

            return workerDao.countWorkers(professions, neighborhoodIds, workerRole, workerStatus);
        } else {
            //user isn't a worker, must only return workers from their neighborhood
            return workerDao.countWorkers(professions, new long[]{user.getNeighborhood().getNeighborhoodId()}, workerRole, workerStatus);
        }
    }

    @Override
    public int calculateWorkerPages(List<String> professions, long userId, int size, WorkerRole workerRole, WorkerStatus workerStatus) {
        LOGGER.info("Getting Pages of Workers with size {} from User {} with professions {}", size, userId, professions);

        ValidationUtils.checkId(userId, "User");

        return PaginationUtils.calculatePages(countWorkers(professions, userId, workerRole, workerStatus), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Worker updateWorkerPartially(long workerId, String phoneNumber, String address, String businessName, MultipartFile backgroundPicture, String bio){
        LOGGER.info("Updating Worker {}", workerId);

        Worker worker = getWorker(workerId);
        if(phoneNumber != null && !phoneNumber.isEmpty())
            worker.setPhoneNumber(phoneNumber);
        if(address != null && !address.isEmpty())
            worker.setAddress(address);
        if(businessName != null && !businessName.isEmpty())
            worker.setBusinessName(businessName);
        if(bio != null && !bio.isEmpty())
            worker.setBio(bio);
        if(backgroundPicture != null && !backgroundPicture.isEmpty()) {
            Image i = imageService.storeImage(backgroundPicture);
            worker.setBackgroundPictureId(i.getImageId());
        }
        return worker;
    }

    // -----------------------------------------------------------------------------------------------------------------

    private Worker getWorker(long workerId) {
        LOGGER.info("Getting Worker {}", workerId);

        ValidationUtils.checkWorkerId(workerId);

        return workerDao.findWorker(workerId).orElseThrow(() -> new NotFoundException("Worker not found"));
    }
}

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
    public Optional<Worker> findWorkerById(long userId) {
        LOGGER.info("Finding Worker with id {}", userId);
        if (userId <= 0)
            throw new IllegalArgumentException("Worker ID must be a positive integer");
        Optional<User> optionalUser = userDao.findUserById(userId);
        return optionalUser.isPresent() ? workerDao.findWorkerById(userId) : Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Worker> findWorkerByMail(String mail) {
        LOGGER.info("Finding Worker with mail {}", mail);
        Optional<User> optionalUser = userDao.findUserByMail(mail);
        return optionalUser.isPresent() ? workerDao.findWorkerById(optionalUser.get().getUserId()) : Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Worker> getWorkersByCriteria(int page, int size, List<String> professions, long loggedUserId, WorkerRole workerRole, WorkerStatus workerStatus) {
        LOGGER.info("Getting Workers with professions {}", professions);
        //If inquirer is a worker, show return workers from any of the neighborhoods they are in
        User user = userDao.findUserById(loggedUserId).orElseThrow(() -> new NotFoundException("User Not Found"));
        if(user.getNeighborhood().getNeighborhoodId() == 0) { //inquirer is a worker
            Set<Neighborhood> neighborhoods = neighborhoodWorkerDao.getNeighborhoods(loggedUserId);
            long[] neighborhoodIds = neighborhoods.stream()
                    .mapToLong(Neighborhood::getNeighborhoodId)
                    .toArray();

            return workerDao.getWorkersByCriteria(page, size, professions, neighborhoodIds, workerRole, workerStatus);
        } else {
            //user isn't a worker, must only return workers from their neighborhood
            return workerDao.getWorkersByCriteria(page, size, professions, new long[]{user.getNeighborhood().getNeighborhoodId()}, workerRole, workerStatus);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int getWorkersCountByCriteria(List<String> professions, long[] neighborhoodIds, WorkerRole workerRole, WorkerStatus workerStatus) {
        LOGGER.info("Getting Workers Count for Neighborhood {} with professions {}", neighborhoodIds, professions);
        return workerDao.getWorkersCountByCriteria(professions, neighborhoodIds, workerRole, workerStatus);
    }

    @Override
    public int getTotalWorkerPages(long neighborhoodId, int size) {
        LOGGER.info("Getting Pages of Workers with size {} from Neighborhood {}", size, neighborhoodId);
        long[] neighborhoodIds = {neighborhoodId};
        return (int) Math.ceil((double) workerDao.getWorkersCountByCriteria(null, neighborhoodIds, WorkerRole.VERIFIED_WORKER, WorkerStatus.none) / size);
    }

    @Override
    public int getTotalWorkerPagesByCriteria(List<String> professions, long[] neighborhoodIds, int size, WorkerRole workerRole, WorkerStatus workerStatus) {
        LOGGER.info("Getting Pages of Workers with size {} from Neighborhoods {} with professions {}", size, neighborhoodIds, professions);
        return (int) Math.ceil((double) workerDao.getWorkersCountByCriteria(professions, neighborhoodIds, workerRole, workerStatus) / size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void updateWorker(long userId, String phoneNumber, String address, String businessName,
                             MultipartFile backgroundPicture, String bio) {
        LOGGER.info("Updating Worker {}", userId);
        Worker worker = workerDao.findWorkerById(userId).orElseThrow(()-> new NotFoundException("Worker Not Found"));
        worker.setPhoneNumber(phoneNumber);
        worker.setAddress(address);
        worker.setBusinessName(businessName);
        worker.setBio(bio);
        if(!backgroundPicture.isEmpty()) {
            Image i = imageService.storeImage(backgroundPicture);
            worker.setBackgroundPictureId(i.getImageId());
        }
    }

    private Worker getWorker(long workerId) {
        LOGGER.info("Getting Worker {}", workerId);
        return workerDao.findWorkerById(workerId).orElseThrow(() -> new NotFoundException("Worker not found"));
    }

    @Override
    public Worker updateWorkerPartially(long userId, String phoneNumber, String address, String businessName, MultipartFile backgroundPicture, String bio){
        LOGGER.info("Updating Worker {}", userId);
        Worker worker = getWorker(userId);
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
}

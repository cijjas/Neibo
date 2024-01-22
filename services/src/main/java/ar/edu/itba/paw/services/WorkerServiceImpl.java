package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.exceptions.UnexpectedException;
import ar.edu.itba.paw.interfaces.persistence.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class WorkerServiceImpl implements WorkerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerServiceImpl.class);
    private final WorkerDao workerDao;
    private final NeighborhoodDao neighborhoodDao;
    private final ProfessionWorkerDao professionWorkerDao;
    private final UserDao userDao;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;
    private final NeighborhoodWorkerDao neighborhoodWorkerDao;

    @Autowired
    public WorkerServiceImpl(WorkerDao workerDao, ProfessionWorkerDao professionWorkerDao, UserDao userDao, ImageService imageService,
                             PasswordEncoder passwordEncoder, NeighborhoodWorkerDao neighborhoodWorkerDao, NeighborhoodDao neighborhoodDao) {
        this.workerDao = workerDao;
        this.professionWorkerDao = professionWorkerDao;
        this.userDao = userDao;
        this.imageService = imageService;
        this.passwordEncoder = passwordEncoder;
        this.neighborhoodWorkerDao = neighborhoodWorkerDao;
        this.neighborhoodDao = neighborhoodDao;
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
        LOGGER.info("Finding Worker {}", userId);

        ValidationUtils.checkUserId(userId);

        Optional<User> optionalUser = userDao.findUser(userId);
        return optionalUser.isPresent() ? workerDao.findWorker(userId) : Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Worker> findWorker(String mail) {
        LOGGER.info("Finding Worker {}", mail);
        Optional<User> optionalUser = userDao.findUser(mail);
        return optionalUser.isPresent() ? workerDao.findWorker(optionalUser.get().getUserId()) : Optional.empty();
    }

    @Override
    public Set<Worker> getWorkers(int page, int size, List<String> professions, List<String> neighborhoodIds, String workerRole, String workerStatus) {
        LOGGER.info("Getting Workers with Status {} Role {} Professions {} from Neighborhoods {}", workerStatus, workerRole, professions, neighborhoodIds);

        ValidationUtils.checkOptionalWorkerRoleString(workerRole);
        ValidationUtils.checkOptionalWorkerStatusString(workerStatus);
        ValidationUtils.checkProfessionsArray(professions);
        ValidationUtils.checkPageAndSize(page, size);
        List<Long> parsedNeighborhoodIds = parseNeighborhoodIds(neighborhoodIds);

        return workerDao.getWorkers(page, size, professions, parsedNeighborhoodIds, workerRole, workerStatus);
    }

    @Override
    public int countWorkers(List<String> professions, List<String> neighborhoodIds, String workerRole, String workerStatus) {
        LOGGER.info("Counting Workers with Status {} Role {} Professions {} from Neighborhoods {}", workerStatus, workerRole, professions, neighborhoodIds);

        ValidationUtils.checkOptionalWorkerRoleString(workerRole);
        ValidationUtils.checkOptionalWorkerStatusString(workerStatus);
        ValidationUtils.checkProfessionsArray(professions);

        List<Long> parsedNeighborhoodIds = parseNeighborhoodIds(neighborhoodIds);

        return workerDao.countWorkers(professions, parsedNeighborhoodIds, workerRole, workerStatus);
    }

    @Override
    public int calculateWorkerPages(List<String> professions, List<String> neighborhoodIds, int size, String workerRole, String workerStatus) {
        LOGGER.info("Calculating Worker Pages with Status {} Role {} Professions {} from Neighborhoods {}", workerStatus, workerRole, professions, neighborhoodIds);

        ValidationUtils.checkOptionalWorkerRoleString(workerRole);
        ValidationUtils.checkOptionalWorkerStatusString(workerStatus);
        ValidationUtils.checkProfessionsArray(professions);
        ValidationUtils.checkSize(size);

        List<Long> parsedNeighborhoodIds = parseNeighborhoodIds(neighborhoodIds);

        return PaginationUtils.calculatePages(workerDao.countWorkers(professions, parsedNeighborhoodIds, workerRole, workerStatus), size);
    }

    private List<Long> parseNeighborhoodIds(List<String> neighborhoodIds) {
        List<Long> parsedNeighborhoodIds = new ArrayList<>();

        if (neighborhoodIds != null && !neighborhoodIds.isEmpty()) {
            for (String neighborhoodId : neighborhoodIds) {
                long parsedId = Long.parseLong(neighborhoodId);
                if (parsedId <= 0)
                    throw new IllegalArgumentException("Invalid value (" + parsedId + ") for the Neighborhood ID. Please use a positive integer greater than 0.");
                neighborhoodDao.findNeighborhood(parsedId).orElseThrow(NotFoundException::new);
                parsedNeighborhoodIds.add(parsedId);
            }
        }

        return parsedNeighborhoodIds;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Worker updateWorkerPartially(long workerId, String phoneNumber, String address, String businessName, MultipartFile backgroundPicture, String bio){
        LOGGER.info("Updating Worker {}", workerId);

        Worker worker = workerDao.findWorker(workerId).orElseThrow(()-> new NotFoundException("Worker Not Found"));
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

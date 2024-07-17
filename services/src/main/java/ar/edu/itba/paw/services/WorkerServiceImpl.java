package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.exceptions.UnexpectedException;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.models.Entities.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Validation;
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
    private final AffiliationDao affiliationDao;

    @Autowired
    public WorkerServiceImpl(WorkerDao workerDao, ProfessionWorkerDao professionWorkerDao, UserDao userDao, ImageService imageService,
                             PasswordEncoder passwordEncoder, AffiliationDao affiliationDao, NeighborhoodDao neighborhoodDao) {
        this.workerDao = workerDao;
        this.professionWorkerDao = professionWorkerDao;
        this.userDao = userDao;
        this.imageService = imageService;
        this.passwordEncoder = passwordEncoder;
        this.affiliationDao = affiliationDao;
        this.neighborhoodDao = neighborhoodDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Worker createWorker(String mail, String name, String surname, String password, String identification, String phoneNumber, String address, String languageURN, String[] professionURNs, String businessName) {
        LOGGER.info("Creating Worker with mail {}", mail);

        int id = 0;
        try {
            id = Integer.parseInt(identification);
        } catch (NumberFormatException e) {
            LOGGER.error("Error whilst formatting Identification");
            throw new UnexpectedException("Unexpected Error while creating Worker");
        }
        Language language = Language.ENGLISH;
        if(languageURN != null) {
            long languageId = ValidationUtils.extractURNId(languageURN);
            ValidationUtils.checkLanguageId(languageId);
            language = Language.fromId(languageId);
        }

        User user = userDao.createUser(mail, passwordEncoder.encode(password), name, surname, BaseNeighborhood.WORKERS_NEIGHBORHOOD.getId(), language, false, UserRole.WORKER, id);
        Worker worker = workerDao.createWorker(user.getUserId(), phoneNumber, address, businessName);
        for(String urn : professionURNs){
            long professionId = ValidationUtils.extractURNId(urn);
            ValidationUtils.checkProfessionId(professionId);
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

    @Override
    public int countWorkers(List<String> professionURNs, List<String> neighborhoodURNs, String workerRoleURN, String workerStatusURN) {
        LOGGER.info("Counting Workers with Status {} Role {} Professions {} from Neighborhoods {}", workerStatusURN, workerRoleURN, professionURNs, neighborhoodURNs);

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

       return workerDao.countWorkers(professionIds, neighborhoodIds, workerRoleId, workerStatusId);
    }

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
    public Worker updateWorkerPartially(long workerId, String phoneNumber, String address, String businessName, String backgroundPictureURN, String bio){
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
        if (backgroundPictureURN != null) {
            long imageId = ValidationUtils.extractURNId(backgroundPictureURN);
            ValidationUtils.checkImageId(imageId);
            Image i = imageService.findImage(imageId).orElseThrow(() -> new NotFoundException("Image not found"));
            worker.setBackgroundPictureId(i.getImageId());
        }

        return worker;
    }
}

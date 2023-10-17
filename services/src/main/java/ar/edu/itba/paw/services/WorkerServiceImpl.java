package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.NeighborhoodWorkerDao;
import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.WorkerDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Worker;
import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WorkerServiceImpl implements WorkerService {
    private final WorkerDao workerDao;
    private final ProfessionWorkerDao professionWorkerDao;
    private final UserDao userDao;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;
    private final NeighborhoodWorkerDao neighborhoodWorkerDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerServiceImpl.class);

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
    public Worker createWorker(String mail, String name, String surname, String password, int identification, String phoneNumber, String address, Language language, long[] professionIds, String businessName) {
        LOGGER.info("Creating Worker with mail {}", mail);
        User user = userDao.createUser(mail, passwordEncoder.encode(password), name, surname, 0, language, false, UserRole.WORKER, identification);
        Worker worker = workerDao.createWorker(user.getUserId(), phoneNumber, address, businessName);
        for (long professionId : professionIds) {
            professionWorkerDao.addWorkerProfession(user.getUserId(), professionId);
        }
        return worker;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Worker> findWorkerById(long userId) {
        LOGGER.info("Finding Worker with id {}", userId);
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
    public List<Worker> getWorkersByCriteria(int page, int size, List<String> professions, long neighborhoodId, long loggedUserId) {
        LOGGER.info("Getting Workers from Neighborhoods {} with professions {}", neighborhoodId, professions);
        if(neighborhoodId != 0)
            return workerDao.getWorkersByCriteria(page, size, professions, new long[]{neighborhoodId});

        // If the user is a worker, display workers from every neighborhood they are in
        List<Neighborhood> neighborhoods = neighborhoodWorkerDao.getNeighborhoods(loggedUserId);

        // Transform the list of neighborhoods into an array of longs
        long[] neighborhoodIds = neighborhoods.stream()
                .mapToLong(Neighborhood::getNeighborhoodId)
                .toArray();

        return workerDao.getWorkersByCriteria(page, size, professions, neighborhoodIds);
    }

    @Override
    @Transactional(readOnly = true)
    public int getWorkersCountByCriteria(List<String> professions, long[] neighborhoodIds) {
        LOGGER.info("Getting Workers Count for Neighborhood {} with professions {}", neighborhoodIds, professions);
        return workerDao.getWorkersCountByCriteria(professions, neighborhoodIds);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void updateWorker(long userId, String phoneNumber, String address, String businessName,
                             MultipartFile backgroundPicture, String bio) {
        LOGGER.info("Updating Worker {}", userId);
        Image i = imageService.storeImage(backgroundPicture);
        workerDao.updateWorker(userId, phoneNumber, address, businessName, i.getImageId(), bio);
    }
}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.WorkerDao;
import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Worker;
import enums.Language;
import enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

@Service
public class WorkerServiceImpl implements WorkerService {
    private final WorkerDao workerDao;
    private final ProfessionWorkerDao professionWorkerDao;
    private final UserDao userDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerServiceImpl.class);

    @Autowired
    public WorkerServiceImpl(WorkerDao workerDao, ProfessionWorkerDao professionWorkerDao, UserDao userDao) {
        this.workerDao = workerDao;
        this.professionWorkerDao = professionWorkerDao;
        this.userDao = userDao;
    }
    // ---------------------------------------------- WORKERS INSERT -----------------------------------------------------
    @Override
    public Worker createWorker(String mail, String name, String surname, String password, int identification, String phoneNumber, String address, Language language, long[] professionIds, String businessName) {
        LOGGER.info("Creating Worker with mail {}", mail);
        User user = userDao.createUser(mail, password, name, surname, 0, language, false, UserRole.WORKER, identification);
        System.out.println("CREATED THE USER: " + user.getUserId());
        Worker worker = workerDao.createWorker(user.getUserId(), phoneNumber, address, businessName);
        for (long professionId : professionIds) {
            professionWorkerDao.addWorkerProfession(user.getUserId(), professionId);
        }
        return worker;
    }

    // ---------------------------------------------- WORKERS SELECT -----------------------------------------------------
    @Override
    public Optional<Worker> findWorkerById(long userId) {
        LOGGER.info("Finding Worker with id {}", userId);
        Optional<User> optionalUser = userDao.findUserById(userId);
        return optionalUser.isPresent() ? workerDao.findWorkerById(userId) : Optional.empty();
    }

    @Override
    public Optional<Worker> findWorkerByMail(String mail) {
        LOGGER.info("Finding Worker with mail {}", mail);
        Optional<User> optionalUser = userDao.findUserByMail(mail);
        return optionalUser.isPresent() ? workerDao.findWorkerById(optionalUser.get().getUserId()) : Optional.empty();
    }

    @Override
    public List<Worker> getWorkersByCriteria(int page, int size, List<String> professions, long neighborhoodId) {
        LOGGER.info("Getting Workers from Neighborhood {} with professions {}", neighborhoodId, professions);
        return workerDao.getWorkersByCriteria(page, size, professions, neighborhoodId);
    }

    @Override
    public int getWorkersCountByCriteria(List<String> professions, long neighborhoodId) {
        LOGGER.info("Getting Workers Count for Neighborhood {} with professions {}", neighborhoodId, professions);
        return workerDao.getWorkersCountByCriteria(professions, neighborhoodId);
    }

    // ---------------------------------------------- WORKERS UPDATE -----------------------------------------------------
    @Override
    public void updateWorker(long userId, String name, String surname, String password, int identification,
                             String phoneNumber, String address, Language language, boolean darkMode,
                             String businessName, long profilePictureId, long backgroundPictureId, String bio) {
        LOGGER.info("Updating Worker {}", userId);
        userDao.setUserValues(userId, password, name, surname, language, darkMode, profilePictureId, UserRole.WORKER, identification, 0);
        workerDao.updateWorker(userId, phoneNumber, address, businessName, backgroundPictureId, bio);
    }
}

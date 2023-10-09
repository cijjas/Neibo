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

import java.util.List;
import java.util.Optional;

@Service
public class WorkerServiceImpl implements WorkerService {
    private final WorkerDao workerDao;
    private final ProfessionWorkerDao professionWorkerDao;
    private final UserDao userDao;

    @Autowired
    public WorkerServiceImpl(WorkerDao workerDao, ProfessionWorkerDao professionWorkerDao, UserDao userDao) {
        this.workerDao = workerDao;
        this.professionWorkerDao = professionWorkerDao;
        this.userDao = userDao;
    }
    // ---------------------------------------------- WORKERS INSERT -----------------------------------------------------
    @Override
    public Worker createWorker(String mail, String name, String surname, String password, int identification, String phoneNumber, String address, Language language, long professionId, String businessName) {
        User user = userDao.createUser(mail, password, name, surname, 0, language, false, UserRole.WORKER, identification);
        System.out.println("CREATED THE USER: " + user.getUserId());
        Worker worker = workerDao.createWorker(user.getUserId(), phoneNumber, address, businessName);
        professionWorkerDao.addWorkerProfession(user.getUserId(), professionId);
        return worker;
    }

    // ---------------------------------------------- WORKERS SELECT -----------------------------------------------------
    @Override
    public Optional<Worker> findWorkerById(long userId) {
        Optional<User> optionalUser = userDao.findUserById(userId);
        return optionalUser.isPresent() ? workerDao.findWorkerById(userId) : Optional.empty();
    }

    @Override
    public Optional<Worker> findWorkerByMail(String mail) {
        Optional<User> optionalUser = userDao.findUserByMail(mail);
        return optionalUser.isPresent() ? workerDao.findWorkerById(optionalUser.get().getUserId()) : Optional.empty();
    }

    @Override
    public List<Worker> getWorkersByCriteria(int page, int size, List<String> professions, long neighborhoodId) {
        return workerDao.getWorkersByCriteria(page, size, professions, neighborhoodId);
    }

    @Override
    public int getWorkersCountByCriteria(List<String> professions, long neighborhoodId) {
        return workerDao.getWorkersCountByCriteria(professions, neighborhoodId);
    }

    // ---------------------------------------------- WORKERS UPDATE -----------------------------------------------------
    @Override
    public void updateWorker(long userId, String name, String surname, String password, int identification,
                             String phoneNumber, String address, Language language, boolean darkMode,
                             String businessName, long profilePictureId, long backgroundPictureId, String bio) {
        userDao.setUserValues(userId, password, name, surname, language, darkMode, profilePictureId, UserRole.WORKER, identification);
        workerDao.updateWorker(userId, phoneNumber, address, businessName, backgroundPictureId, bio);
    }
}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodWorkerDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.NeighborhoodWorkerService;
import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.models.User;
import enums.Language;
import enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NeighborhoodWorkerServiceImpl implements NeighborhoodWorkerService {
    private final NeighborhoodWorkerDao neighborhoodWorkerDao;
    private final UserDao userDao;
    private final EmailService emailService;
    private final NeighborhoodDao neighborhoodDao;


    @Autowired
    public NeighborhoodWorkerServiceImpl(NeighborhoodWorkerDao neighborhoodWorkerDao, UserDao userDao, EmailService emailService, NeighborhoodDao neighborhoodDao) {
        this.neighborhoodWorkerDao = neighborhoodWorkerDao;
        this.userDao = userDao;
        this.emailService = emailService;
        this.neighborhoodDao = neighborhoodDao;
    }

    // --------------------------------------- NIEGHBORHOODWORKERS SELECT ------------------------------------------
    @Override
    public void addWorkerToNeighborhood(long workerId, long neighborhoodId) {
        neighborhoodWorkerDao.addWorkerToNeighborhood(workerId, neighborhoodId);

        //send admin email notifying new worker
        User worker = userDao.findUserById(workerId).orElse(null);
        assert worker != null;
        emailService.sendNewUserMail(neighborhoodId, worker.getName(), UserRole.WORKER);
    }

    // --------------------------------------- NIEGHBORHOODWORKERS DELETE ------------------------------------------
    @Override
    public void removeWorkerFromNeighborhood(long workerId, long neighborhoodId) {
        neighborhoodWorkerDao.removeWorkerFromNeighborhood(workerId, neighborhoodId);
    }
}

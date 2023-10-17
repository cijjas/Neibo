package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodWorkerDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.NeighborhoodWorkerService;
import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class NeighborhoodWorkerServiceImpl implements NeighborhoodWorkerService {
    private final NeighborhoodWorkerDao neighborhoodWorkerDao;
    private final UserDao userDao;
    private final EmailService emailService;
    private final NeighborhoodDao neighborhoodDao;


    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborhoodWorkerServiceImpl.class);

    @Autowired
    public NeighborhoodWorkerServiceImpl(NeighborhoodWorkerDao neighborhoodWorkerDao, UserDao userDao, EmailService emailService, NeighborhoodDao neighborhoodDao) {
        this.neighborhoodWorkerDao = neighborhoodWorkerDao;
        this.userDao = userDao;
        this.emailService = emailService;
        this.neighborhoodDao = neighborhoodDao;
    }

    // --------------------------------------- NIEGHBORHOODWORKERS INSERT ------------------------------------------
    @Override
    public void addWorkerToNeighborhood(long workerId, long neighborhoodId) {
        LOGGER.info("Adding Worker {} to Neighborhood {}", workerId, neighborhoodId);
        neighborhoodWorkerDao.addWorkerToNeighborhood(workerId, neighborhoodId);

        //send admin email notifying new worker
        User worker = userDao.findUserById(workerId).orElse(null);
        assert worker != null;
        emailService.sendNewUserMail(neighborhoodId, worker.getName(), UserRole.WORKER);
    }

    // --------------------------------------- NIEGHBORHOODWORKERS SELECT ------------------------------------------
    @Override
    public List<Neighborhood> getNeighborhoods(long workerId) {
        LOGGER.info("Getting Neighborhoods for Worker {}", workerId);
        return neighborhoodWorkerDao.getNeighborhoods(workerId);
    }

    @Override
    public List<Neighborhood> getOtherNeighborhoods(long workerId) {
        LOGGER.info("Getting Other Neighborhoods for Worker {}", workerId);
        List<Neighborhood> allNeighborhoods = neighborhoodDao.getNeighborhoods();
        List<Neighborhood> workerNeighborhoods = neighborhoodWorkerDao.getNeighborhoods(workerId);

        for(Neighborhood neighborhood : workerNeighborhoods){
            allNeighborhoods.removeIf(neighborhoodName -> neighborhoodName.getName().equals(neighborhood.getName()));
        }
        allNeighborhoods.removeIf(neighborhood -> neighborhood.getName().equals("Worker Neighborhood"));
        allNeighborhoods.removeIf(neighborhood -> neighborhood.getName().equals("Rejected"));
        return allNeighborhoods;
    }

    // --------------------------------------- NIEGHBORHOODWORKERS DELETE ------------------------------------------
    @Override
    public void removeWorkerFromNeighborhood(long workerId, long neighborhoodId) {
        LOGGER.info("Removing Worker {} from Neighborhood {}", workerId, neighborhoodId);
        neighborhoodWorkerDao.removeWorkerFromNeighborhood(workerId, neighborhoodId);
    }
}

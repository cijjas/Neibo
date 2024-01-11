package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodWorkerDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.NeighborhoodWorkerService;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.models.Entities.WorkerArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class NeighborhoodWorkerServiceImpl implements NeighborhoodWorkerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborhoodWorkerServiceImpl.class);
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

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void addWorkerToNeighborhood(long workerId, long neighborhoodId) {
        LOGGER.info("Adding Worker {} to Neighborhood {}", workerId, neighborhoodId);
        neighborhoodWorkerDao.createWorkerArea(workerId, neighborhoodId);

        //send admin email notifying new worker
        User worker = userDao.findUserById(workerId).orElse(null);
        assert worker != null;
        emailService.sendNewUserMail(neighborhoodId, worker.getName(), UserRole.WORKER);
    }


    @Override
    public void addWorkerToNeighborhoods(long workerId, String neighborhoodIds) {
        //convert the id's string into a List<Long>, where the values are comma separated in the string
        String[] idsString = neighborhoodIds.split(",");
        Long[] idsLong = new Long[idsString.length];
        for(int i = 0; i < idsString.length; i++) {
            idsLong[i] = Long.parseLong(idsString[i]);
        }

        for (long neighborhoodId : idsLong) {
            addWorkerToNeighborhood(workerId, neighborhoodId);
            setNeighborhoodRole(workerId, WorkerRole.UNVERIFIED_WORKER, neighborhoodId);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Set<Neighborhood> getNeighborhoods(long workerId) {
        LOGGER.info("Getting Neighborhoods for Worker {}", workerId);
        return neighborhoodWorkerDao.getNeighborhoods(workerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Neighborhood> getOtherNeighborhoods(long workerId) {
        LOGGER.info("Getting Other Neighborhoods for Worker {}", workerId);
        List<Neighborhood> allNeighborhoods = neighborhoodDao.getNeighborhoods();
        Set<Neighborhood> workerNeighborhoods = neighborhoodWorkerDao.getNeighborhoods(workerId);

        for (Neighborhood neighborhood : workerNeighborhoods) {
            allNeighborhoods.removeIf(neighborhoodName -> neighborhoodName.getName().equals(neighborhood.getName()));
        }
        allNeighborhoods.removeIf(neighborhood -> neighborhood.getName().equals("Worker Neighborhood"));
        allNeighborhoods.removeIf(neighborhood -> neighborhood.getName().equals("Rejected"));
        return allNeighborhoods;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void verifyWorkerInNeighborhood(long workerId, long neighborhoodId) {
        LOGGER.info("Verifying Worker {} in Neighborhood {}", workerId, neighborhoodId);
        setNeighborhoodRole(workerId, WorkerRole.VERIFIED_WORKER, neighborhoodId);
        User worker = userDao.findUserById(workerId).orElse(null);
        assert worker != null;
        String neighborhoodName = neighborhoodDao.findNeighborhoodById(worker.getNeighborhood().getNeighborhoodId()).orElseThrow(() -> new NotFoundException("Neighborhood not found")).getName();
        emailService.sendVerifiedNeighborMail(worker, neighborhoodName);
    }

    @Override
    public void rejectWorkerFromNeighborhood(long workerId, long neighborhoodId) {
        LOGGER.info("Rejecting Worker {} from Neighborhood {}", workerId, neighborhoodId);
        setNeighborhoodRole(workerId, WorkerRole.REJECTED, neighborhoodId);
    }

    @Override
    public void unverifyWorkerFromNeighborhood(long workerId, long neighborhoodId) {
        LOGGER.info("Un-verifying Worker {} from Neighborhood {}", workerId, neighborhoodId);
        setNeighborhoodRole(workerId, WorkerRole.UNVERIFIED_WORKER, neighborhoodId);
    }

    private void setNeighborhoodRole(long workerId, WorkerRole role, long neighborhoodId) {
        LOGGER.debug("Setting Worker {} role to {} in Neighborhood {}", workerId, role, neighborhoodId);
        WorkerArea workerArea = neighborhoodWorkerDao.findWorkerArea(workerId, neighborhoodId).orElseThrow(()-> new NotFoundException("Worker Area Not Found"));
        workerArea.setRole(role);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void removeWorkerFromNeighborhood(long workerId, long neighborhoodId) {
        LOGGER.info("Removing Worker {} from Neighborhood {}", workerId, neighborhoodId);
        neighborhoodWorkerDao.deleteWorkerArea(workerId, neighborhoodId);
    }
}



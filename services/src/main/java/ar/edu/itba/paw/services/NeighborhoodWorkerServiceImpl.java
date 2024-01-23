package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.AffiliationDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.WorkerDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.NeighborhoodWorkerService;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.models.Entities.Affiliation;
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
    private final AffiliationDao affiliationDao;
    private final UserDao userDao;
    private final WorkerDao workerDao;
    private final EmailService emailService;
    private final NeighborhoodDao neighborhoodDao;

    @Autowired
    public NeighborhoodWorkerServiceImpl(AffiliationDao affiliationDao, UserDao userDao, EmailService emailService,
                                         NeighborhoodDao neighborhoodDao, WorkerDao workerDao) {
        this.affiliationDao = affiliationDao;
        this.userDao = userDao;
        this.emailService = emailService;
        this.neighborhoodDao = neighborhoodDao;
        this.workerDao = workerDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void addWorkerToNeighborhood(long workerId, long neighborhoodId) {
        LOGGER.info("Adding Worker {} to Neighborhood {}", workerId, neighborhoodId);

        affiliationDao.createAffiliation(workerId, neighborhoodId);
        //send admin email notifying new worker
        User worker = userDao.findUser(workerId).orElse(null);
        assert worker != null;
        emailService.sendNewUserMail(neighborhoodId, worker.getName(), UserRole.WORKER);
    }


    @Override
    public void addWorkerToNeighborhoods(long workerId, String neighborhoodIds) {
        LOGGER.info("Adding Worker {} to Neighborhoods {}", workerId, neighborhoodIds);

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
    public Set<Affiliation> getAffiliations(Long workerId, Long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Affiliations between Worker {} and Neighborhood {}", workerId, neighborhoodId);

        ValidationUtils.checkWorkerId(workerId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPageAndSize(page, size);

        return affiliationDao.getAffiliations(workerId, neighborhoodId, page, size);
    }

    @Override
    public int countAffiliations(Long workerId, Long neighborhoodId){
        LOGGER.info("Counting Affiliations between Worker {} and Neighborhood {}", workerId, neighborhoodId);

        ValidationUtils.checkWorkerId(workerId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        return affiliationDao.countAffiliations(workerId, neighborhoodId);
    }

    @Override
    public int calculateAffiliationPages(Long workerId, Long neighborhoodId, int size) {
        LOGGER.info("Calculating Affiliation Pages between Worker {} and Neighborhood {}", workerId, neighborhoodId);

        ValidationUtils.checkWorkerId(workerId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(affiliationDao.countAffiliations(workerId, neighborhoodId), size);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Neighborhood> getNeighborhoods(long workerId) {
        LOGGER.info("Getting the Neighborhoods that Worker {} belongs to", workerId);

        ValidationUtils.checkWorkerId(workerId);

        workerDao.findWorker(workerId).orElseThrow(NotFoundException::new);

        return affiliationDao.getNeighborhoods(workerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Neighborhood> getOtherNeighborhoods(long workerId) {
        LOGGER.info("Getting Neighborhoods that Worker {} does not belong to", workerId);

        ValidationUtils.checkWorkerId(workerId);

        List<Neighborhood> allNeighborhoods = neighborhoodDao.getNeighborhoods();
        Set<Neighborhood> workerNeighborhoods = affiliationDao.getNeighborhoods(workerId);

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

        ValidationUtils.checkWorkerId(workerId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        setNeighborhoodRole(workerId, WorkerRole.VERIFIED_WORKER, neighborhoodId);
        User worker = userDao.findUser(workerId).orElseThrow(()->  new NotFoundException("User Not Found"));
        String neighborhoodName = neighborhoodDao.findNeighborhood(worker.getNeighborhood().getNeighborhoodId()).orElseThrow(() -> new NotFoundException("Neighborhood not found")).getName();
        emailService.sendVerifiedNeighborMail(worker, neighborhoodName);
    }

    @Override
    public void rejectWorkerFromNeighborhood(long workerId, long neighborhoodId) {
        LOGGER.info("Rejecting Worker {} from Neighborhood {}", workerId, neighborhoodId);

        ValidationUtils.checkWorkerId(workerId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        setNeighborhoodRole(workerId, WorkerRole.REJECTED, neighborhoodId);
    }

    @Override
    public void unverifyWorkerFromNeighborhood(long workerId, long neighborhoodId) {
        LOGGER.info("Un-verifying Worker {} from Neighborhood {}", workerId, neighborhoodId);

        ValidationUtils.checkWorkerId(workerId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        setNeighborhoodRole(workerId, WorkerRole.UNVERIFIED_WORKER, neighborhoodId);
    }

    private void setNeighborhoodRole(long workerId, WorkerRole role, long neighborhoodId) {
        LOGGER.debug("Setting Worker {} role to {} in Neighborhood {}", workerId, role, neighborhoodId);

        Affiliation affiliation = affiliationDao.findAffiliation(workerId, neighborhoodId).orElseThrow(()-> new NotFoundException("Worker Area Not Found"));
        affiliation.setRole(role);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void removeWorkerFromNeighborhood(long workerId, long neighborhoodId) {
        LOGGER.info("Removing Worker {} from Neighborhood {}", workerId, neighborhoodId);

        ValidationUtils.checkAffiliationIds(workerId, neighborhoodId);

        affiliationDao.deleteAffiliation(workerId, neighborhoodId);
    }
}



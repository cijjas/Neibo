package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.AffiliationDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.WorkerDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.AffiliationService;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.models.Entities.Affiliation;
import ar.edu.itba.paw.models.TwoIds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class AffiliationServiceImpl implements AffiliationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AffiliationServiceImpl.class);
    private final AffiliationDao affiliationDao;
    private final UserDao userDao;
    private final WorkerDao workerDao;
    private final EmailService emailService;
    private final NeighborhoodDao neighborhoodDao;

    @Autowired
    public AffiliationServiceImpl(AffiliationDao affiliationDao, UserDao userDao, EmailService emailService,
                                  NeighborhoodDao neighborhoodDao, WorkerDao workerDao) {
        this.affiliationDao = affiliationDao;
        this.userDao = userDao;
        this.emailService = emailService;
        this.neighborhoodDao = neighborhoodDao;
        this.workerDao = workerDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public List<Affiliation> getAffiliations(String workerURN, String neighborhoodURN, int page, int size) {
        LOGGER.info("Getting Affiliations between Worker {} and Neighborhood {}", workerURN, neighborhoodURN);

        Long workerId = ValidationUtils.checkURNAndExtractWorkerId(workerURN);
        Long neighborhoodId = ValidationUtils.checkURNAndExtractNeighborhoodId(neighborhoodURN);

        ValidationUtils.checkPageAndSize(page, size);

        return affiliationDao.getAffiliations(workerId, neighborhoodId, page, size);
    }

    @Override
    public int countAffiliations(String workerURN, String neighborhoodURN){
        LOGGER.info("Counting Affiliations between Worker {} and Neighborhood {}", workerURN, neighborhoodURN);

        Long workerId = ValidationUtils.checkURNAndExtractWorkerId(workerURN);
        Long neighborhoodId = ValidationUtils.checkURNAndExtractNeighborhoodId(neighborhoodURN);

        return affiliationDao.countAffiliations(workerId, neighborhoodId);
    }

    @Override
    public int calculateAffiliationPages(String workerURN, String neighborhoodURN, int size) {
        LOGGER.info("Calculating Affiliation Pages between Worker {} and Neighborhood {}", workerURN, neighborhoodURN);

        Long workerId = ValidationUtils.checkURNAndExtractWorkerId(workerURN);
        Long neighborhoodId = ValidationUtils.checkURNAndExtractNeighborhoodId(neighborhoodURN);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(affiliationDao.countAffiliations(workerId, neighborhoodId), size);
    }

    @Override
    public Optional<Affiliation> findAffiliation(String workerURN, String neighborhoodURN) {
        long workerId = ValidationUtils.extractURNId(workerURN);
        long neighborhoodId = ValidationUtils.extractURNId(neighborhoodURN);

        ValidationUtils.checkWorkerId(workerId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        return affiliationDao.findAffiliation(workerId, neighborhoodId);
    }


    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Affiliation createAffiliation(String workerURN, String neighborhoodURN, String workerRoleURN) {
        LOGGER.info("Creating Affiliation between Worker {} and Neighborhood {} with Role {}", workerURN, neighborhoodURN, workerRoleURN);

        TwoIds workerTwoIds = ValidationUtils.extractTwoURNIds(workerURN);
        long neighborhoodId = ValidationUtils.extractURNId(neighborhoodURN);
        Long workerRoleId = null;
        if(workerRoleURN != null){
            workerRoleId = ValidationUtils.extractURNId(workerRoleURN);
            ValidationUtils.checkWorkerRoleId(workerRoleId);
        }

        // should also check for the first id in the worker two ids, it will be fixed once the validation style is unified
        ValidationUtils.checkWorkerId(workerTwoIds.getSecondId());
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        workerDao.findWorker(workerTwoIds.getSecondId()).orElseThrow(()-> new NotFoundException("Worker Not Found"));
        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(()-> new NotFoundException("Neighborhood Not Found"));

        return affiliationDao.createAffiliation(workerTwoIds.getSecondId(), neighborhoodId, workerRoleId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Affiliation updateAffiliation(String workerURN, String neighborhoodURN, String workerRole) {
        LOGGER.info("Creating Affiliation between Worker {} and Neighborhood {} to Role {}", workerURN, neighborhoodURN, workerRole);

        long workerId = ValidationUtils.extractURNId(workerURN);
        long neighborhoodId = ValidationUtils.extractURNId(neighborhoodURN);

        ValidationUtils.checkWorkerId(workerId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkWorkerRoleString(workerRole);

        Affiliation affiliation = affiliationDao.findAffiliation(workerId, neighborhoodId).orElseThrow(()-> new NotFoundException("Affiliation Not Found"));
        affiliation.setRole(WorkerRole.valueOf(workerRole));

        return affiliation;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteAffiliation(String workerURN, String neighborhoodURN) {
        LOGGER.info("Deleting Affiliation between Worker {} from Neighborhood {}", workerURN, neighborhoodURN);

        long workerId = ValidationUtils.extractURNId(workerURN);
        long neighborhoodId = ValidationUtils.extractURNId(neighborhoodURN);

        ValidationUtils.checkWorkerId(workerId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return affiliationDao.deleteAffiliation(workerId, neighborhoodId);
    }


    // -----------------------------------------------------------------------------------------------------------------
    // will deprecate

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
}



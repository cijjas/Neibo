package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.AffiliationDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.WorkerDao;
import ar.edu.itba.paw.interfaces.services.AffiliationService;
import ar.edu.itba.paw.models.Entities.Affiliation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AffiliationServiceImpl implements AffiliationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AffiliationServiceImpl.class);

    private final AffiliationDao affiliationDao;
    private final WorkerDao workerDao;
    private final NeighborhoodDao neighborhoodDao;

    @Autowired
    public AffiliationServiceImpl(AffiliationDao affiliationDao, NeighborhoodDao neighborhoodDao, WorkerDao workerDao) {
        this.affiliationDao = affiliationDao;
        this.neighborhoodDao = neighborhoodDao;
        this.workerDao = workerDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Affiliation createAffiliation(String workerURN, String neighborhoodURN, String workerRoleURN) {
        LOGGER.info("Creating Affiliation between Worker {} and Neighborhood {} with Role {}", workerURN, neighborhoodURN, workerRoleURN);

        long workerId = ValidationUtils.extractURNId(workerURN);
        long neighborhoodId = ValidationUtils.extractURNId(neighborhoodURN);
        Long workerRoleId = null;
        if (workerRoleURN != null) {
            workerRoleId = ValidationUtils.extractURNId(workerRoleURN);
            ValidationUtils.checkWorkerRoleId(workerRoleId);
        }

        // should also check for the first id in the worker two ids, it will be fixed once the validation style is unified
        ValidationUtils.checkWorkerId(workerId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        workerDao.findWorker(workerId).orElseThrow(() -> new NotFoundException("Worker Not Found"));
        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(() -> new NotFoundException("Neighborhood Not Found"));

        return affiliationDao.createAffiliation(workerId, neighborhoodId, workerRoleId);
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
    public int calculateAffiliationPages(String workerURN, String neighborhoodURN, int size) {
        LOGGER.info("Calculating Affiliation Pages between Worker {} and Neighborhood {}", workerURN, neighborhoodURN);

        Long workerId = ValidationUtils.checkURNAndExtractWorkerId(workerURN);
        Long neighborhoodId = ValidationUtils.checkURNAndExtractNeighborhoodId(neighborhoodURN);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(affiliationDao.countAffiliations(workerId, neighborhoodId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Affiliation updateAffiliation(String workerURN, String neighborhoodURN, String workerRoleURN) {
        LOGGER.info("Creating Affiliation between Worker {} and Neighborhood {} to Role {}", workerURN, neighborhoodURN, workerRoleURN);

        // check not null workerURN and neighborhoodURN

        long workerId = ValidationUtils.extractURNId(workerURN);
        long neighborhoodId = ValidationUtils.extractURNId(neighborhoodURN);

        ValidationUtils.checkWorkerId(workerId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        Affiliation affiliation = affiliationDao.findAffiliation(workerId, neighborhoodId).orElseThrow(() -> new NotFoundException("Affiliation Not Found"));
        long workerRoleId = ValidationUtils.extractURNId(workerRoleURN);
        affiliation.setRole(WorkerRole.fromId(workerRoleId));

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
}



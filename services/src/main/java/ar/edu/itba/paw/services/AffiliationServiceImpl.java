package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.AffiliationDao;
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

    @Autowired
    public AffiliationServiceImpl(AffiliationDao affiliationDao){
        this.affiliationDao = affiliationDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Affiliation createAffiliation(long workerId, long neighborhoodId, long workerRoleId) {
        LOGGER.info("Creating Affiliation between Worker {} and Neighborhood {} with Role {}", workerId, neighborhoodId, workerRoleId);

        return affiliationDao.createAffiliation(workerId, neighborhoodId, workerRoleId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public List<Affiliation> getAffiliations(Long workerId, Long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Affiliations between Worker {} and Neighborhood {}", workerId, neighborhoodId);

        return affiliationDao.getAffiliations(workerId, neighborhoodId, page, size);
    }

    @Override
    public int calculateAffiliationPages(Long workerId, Long neighborhoodId, int size) {
        LOGGER.info("Calculating Affiliation Pages between Worker {} and Neighborhood {}", workerId, neighborhoodId);

        return PaginationUtils.calculatePages(affiliationDao.countAffiliations(workerId, neighborhoodId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Affiliation updateAffiliation(long workerId, long neighborhoodId, Long workerRoleId) {
        LOGGER.info("Creating Affiliation between Worker {} and Neighborhood {} to Role {}", workerId, neighborhoodId, workerRoleId);

        Affiliation affiliation = affiliationDao.findAffiliation(workerId, neighborhoodId).orElseThrow(NotFoundException::new);
        if (workerRoleId != null)
            affiliation.setRole(WorkerRole.fromId(workerRoleId));

        return affiliation;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteAffiliation(long workerId, long neighborhoodId) {
        LOGGER.info("Deleting Affiliation between Worker {} from Neighborhood {}", workerId, neighborhoodId);

        return affiliationDao.deleteAffiliation(workerId, neighborhoodId);
    }
}



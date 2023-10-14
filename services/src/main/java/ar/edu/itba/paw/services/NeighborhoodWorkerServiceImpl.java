package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.NeighborhoodWorkerDao;
import ar.edu.itba.paw.interfaces.services.NeighborhoodWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class NeighborhoodWorkerServiceImpl implements NeighborhoodWorkerService {
    private final NeighborhoodWorkerDao neighborhoodWorkerDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborhoodWorkerServiceImpl.class);

    @Autowired
    public NeighborhoodWorkerServiceImpl(NeighborhoodWorkerDao neighborhoodWorkerDao) {
        this.neighborhoodWorkerDao = neighborhoodWorkerDao;
    }

    // --------------------------------------- NIEGHBORHOODWORKERS SELECT ------------------------------------------
    @Override
    public void addWorkerToNeighborhood(long workerId, long neighborhoodId) {
        LOGGER.info("Adding Worker {} to Neighborhood {}", workerId, neighborhoodId);
        neighborhoodWorkerDao.addWorkerToNeighborhood(workerId, neighborhoodId);
    }

    // --------------------------------------- NIEGHBORHOODWORKERS DELETE ------------------------------------------
    @Override
    public void removeWorkerFromNeighborhood(long workerId, long neighborhoodId) {
        LOGGER.info("Removing Worker {} from Neighborhood {}", workerId, neighborhoodId);
        neighborhoodWorkerDao.removeWorkerFromNeighborhood(workerId, neighborhoodId);
    }
}

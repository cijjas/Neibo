package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.NeighborhoodWorkerDao;
import ar.edu.itba.paw.interfaces.services.NeighborhoodWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NeighborhoodWorkerServiceImpl implements NeighborhoodWorkerService {
    private final NeighborhoodWorkerDao neighborhoodWorkerDao;

    @Autowired
    public NeighborhoodWorkerServiceImpl(NeighborhoodWorkerDao neighborhoodWorkerDao) {
        this.neighborhoodWorkerDao = neighborhoodWorkerDao;
    }

    // --------------------------------------- NIEGHBORHOODWORKERS SELECT ------------------------------------------
    @Override
    public void addWorkerToNeighborhood(long workerId, long neighborhoodId) {
        neighborhoodWorkerDao.addWorkerToNeighborhood(workerId, neighborhoodId);
    }

    // --------------------------------------- NIEGHBORHOODWORKERS DELETE ------------------------------------------
    @Override
    public void removeWorkerFromNeighborhood(long workerId, long neighborhoodId) {
        neighborhoodWorkerDao.removeWorkerFromNeighborhood(workerId, neighborhoodId);
    }
}

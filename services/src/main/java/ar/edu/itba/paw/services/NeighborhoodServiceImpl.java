package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NeighborhoodServiceImpl implements NeighborhoodService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborhoodServiceImpl.class);

    private final NeighborhoodDao neighborhoodDao;

    @Autowired
    public NeighborhoodServiceImpl(final NeighborhoodDao neighborhoodDao) {
        this.neighborhoodDao = neighborhoodDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Neighborhood createNeighborhood(String name) {
        LOGGER.info("Creating Neighborhood {}", name);

        return neighborhoodDao.createNeighborhood(name);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Neighborhood> findNeighborhood(String name) {
        LOGGER.info("Finding Neighborhood {}", name);
        return neighborhoodDao.findNeighborhood(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Neighborhood> findNeighborhood(long neighborhoodId) {
        LOGGER.info("Finding Neighborhood {}", neighborhoodId);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return neighborhoodDao.findNeighborhood(neighborhoodId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Neighborhood> getNeighborhoods() {
        LOGGER.info("Getting Neighborhoods");

        List<Neighborhood> neighborhoods = neighborhoodDao.getNeighborhoods();
        neighborhoods.removeIf(neighborhood -> neighborhood.getNeighborhoodId().intValue() == 0);
        neighborhoods.removeIf(neighborhood -> neighborhood.getNeighborhoodId().intValue() == -1);

        return neighborhoods;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Neighborhood> getNeighborhoods(int page, int size, String workerURN) {
        LOGGER.info("Getting Neighborhoods");

        Long workerId = ValidationUtils.checkURNAndExtractWorkerId(workerURN);

        ValidationUtils.checkPageAndSize(page, size);

        return neighborhoodDao.getNeighborhoods(page, size, workerId);
    }

    // ---------------------------------------------------

    @Override
    public int calculateNeighborhoodPages(String workerURN, int size) {
        LOGGER.info("Calculating Neighborhood Pages");

        Long workerId = ValidationUtils.checkURNAndExtractWorkerId(workerURN);

        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(neighborhoodDao.countNeighborhoods(workerId), size);
    }

    @Override
    public boolean deleteNeighborhood(long neighborhoodId) {
        LOGGER.info("Deleting Neighborhood {}", neighborhoodId);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return neighborhoodDao.deleteNeighborhood(neighborhoodId);
    }
}

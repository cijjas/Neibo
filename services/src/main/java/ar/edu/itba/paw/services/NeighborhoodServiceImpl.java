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
    public Optional<Neighborhood> findNeighborhoodByName(String name) {
        LOGGER.info("Finding Neighborhood with name {}", name);
        return neighborhoodDao.findNeighborhoodByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Neighborhood> findNeighborhoodById(long id) {
        LOGGER.info("Selecting Neighborhood with id {}", id);
        ValidationUtils.checkId(id, "Neighborhood");
        return neighborhoodDao.findNeighborhoodById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Neighborhood> getNeighborhoods() {
        LOGGER.info("Getting All Neighborhoods");
        List<Neighborhood> neighborhoods = neighborhoodDao.getNeighborhoods();
        neighborhoods.removeIf(neighborhood -> neighborhood.getName().equals("Worker Neighborhood"));
        neighborhoods.removeIf(neighborhood -> neighborhood.getName().equals("Rejected"));
        return neighborhoods;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Neighborhood> getNeighborhoodsByCriteria(int page, int size) {
        LOGGER.info("Getting All Neighborhoods");
        ValidationUtils.checkPageAndSize(page, size);

        List<Neighborhood> neighborhoods = neighborhoodDao.getNeighborhoodsByCriteria(page, size);
        neighborhoods.removeIf(neighborhood -> neighborhood.getNeighborhoodId().intValue() == 0);
        neighborhoods.removeIf(neighborhood -> neighborhood.getNeighborhoodId().intValue() == -1);
        return neighborhoods;

    }

    @Override
    public int getTotalNeighborhoodPages(int size) {
        LOGGER.info("Getting Total Neighborhood Pages");
        return (int) Math.ceil((double) neighborhoodDao.getNeighborhoodsCount() / size);
    }
}

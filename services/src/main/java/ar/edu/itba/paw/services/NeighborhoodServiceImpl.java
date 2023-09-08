package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.NeighborDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.models.Neighborhood;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NeighborhoodServiceImpl implements NeighborhoodService {
    private final NeighborhoodDao neighborhoodDao;

    @Autowired
    public NeighborhoodServiceImpl(final NeighborhoodDao neighborhoodDao) {
        this.neighborhoodDao = neighborhoodDao;
    }
    @Override
    public Neighborhood createNeighborhood(String name) {
        return neighborhoodDao.create(name);
    }
}

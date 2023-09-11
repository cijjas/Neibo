package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.NeighborDao;
import ar.edu.itba.paw.interfaces.services.NeighborService;
import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NeighborServiceImpl implements NeighborService {

    private final NeighborDao neighborDao;

    @Autowired
    public NeighborServiceImpl(final NeighborDao neighborDao) {
        this.neighborDao = neighborDao;
    }

    @Override
    public Neighbor createNeighbor(String mail, String name, String surname, long neighborhoodId) {
        return neighborDao.create(mail, name, surname, neighborhoodId);
    }

    @Override
    public List<Neighbor> getAllNeighbors() { return neighborDao.getAllNeighbors(); }

    @Override
    public List<Neighbor> getAllNeighborsByNeighborhood(long neighborhoodId) { return neighborDao.getAllNeighborsByNeighborhood(neighborhoodId); }

    //@Override
    //public List<Neighbor> getAllNeighborsByCommunity(long communityId) { return neighborDao.getAllNeighborsByCommunity(communityId); }

    @Override
    public Optional<Neighbor> findNeighborById(long id) { return neighborDao.findNeighborById(id); }

}

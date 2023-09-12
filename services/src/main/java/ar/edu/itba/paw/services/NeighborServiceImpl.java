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
        return neighborDao.createNeighbor(mail, name, surname, neighborhoodId);
    }

    @Override
    public List<Neighbor> getNeighbors() { return neighborDao.getNeighbors(); }

    @Override
    public List<Neighbor> getNeighborsByNeighborhood(long neighborhoodId) { return neighborDao.getNeighborsByNeighborhood(neighborhoodId); }

    //@Override
    //public List<Neighbor> getNeighborsByCommunity(long communityId) { return neighborDao.getAllNeighborsByCommunity(communityId); }

    @Override
    public Optional<Neighbor> findNeighborById(long id) { return neighborDao.findNeighborById(id); }

    public Optional<Neighbor> findNeighborByMail(String mail) { return neighborDao.findNeighborByMail(mail); }

    @Override
    public List<Neighbor> getNeighborsSubscribedByPostId(long id) {
        return neighborDao.getNeighborsSubscribedByPostId(id);
    }
}

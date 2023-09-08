package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.NeighborDao;
import ar.edu.itba.paw.interfaces.services.NeighborService;
import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NeighborServiceImpl implements NeighborService {

    private final NeighborDao neighborDao;

    @Autowired
    public NeighborServiceImpl(final NeighborDao neighborDao) {
        this.neighborDao = neighborDao;
    }

    @Override
    public User createUser(final String email, final String password) {
        return neighborDao.create(email, password);
    }

    @Override
    public Optional<User> findById(long id) {
        return neighborDao.findById(id);
    }

    @Override
    public Neighbor createNeighbor(String mail, String name, String surname, int neighborhoodId) {
        return neighborDao.create2(mail, name, surname, neighborhoodId);
    }
}

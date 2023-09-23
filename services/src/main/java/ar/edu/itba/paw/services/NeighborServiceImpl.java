package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.NeighborDao;
import ar.edu.itba.paw.interfaces.services.NeighborService;
import ar.edu.itba.paw.models.Neighbor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NeighborServiceImpl implements NeighborService {
    private final NeighborDao neighborDao;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public NeighborServiceImpl(final NeighborDao neighborDao, final PasswordEncoder passwordEncoder) {
        this.neighborDao = neighborDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Neighbor createNeighbor(final String mail, final String password, final String name, final String surname,
                                   final long neighborhoodId, String language, boolean darkMode, boolean verification) {
        Neighbor n = findNeighborByMail(mail).orElse(null);
        if (n == null) {
            return neighborDao.createNeighbor(mail, passwordEncoder.encode(password), name, surname, neighborhoodId, language, darkMode, verification);
        }
        return n;
    }

    @Override
    public List<Neighbor> getNeighbors() { return neighborDao.getNeighbors(); }

    @Override
    public List<Neighbor> getNeighborsByNeighborhood(long neighborhoodId) { return neighborDao.getNeighborsByNeighborhood(neighborhoodId); }

    @Override
    public List<Neighbor> getNeighborsSubscribedByPostId(long id) {
        return neighborDao.getNeighborsSubscribedByPostId(id);
    }

    @Override
    public List<Neighbor> getVerifiedNeighborsByNeighborhood(long neighborhoodId){
        return neighborDao.getVerifiedNeighborsByNeighborhood(neighborhoodId);
    }

    @Override
    public List<Neighbor> getUnverifiedNeighborsByNeighborhood(long neighborhoodId){
        return neighborDao.getUnverifiedNeighborsByNeighborhood(neighborhoodId);
    }

    //@Override
    //public List<Neighbor> getNeighborsByCommunity(long communityId) { return neighborDao.getAllNeighborsByCommunity(communityId); }

    @Override
    public Optional<Neighbor> findNeighborById(long id) { return neighborDao.findNeighborById(id); }

    @Override
    public void toggleDarkMode(long id) {
        Neighbor n = findNeighborById(id).orElse(null);
        if (n != null)
            neighborDao.updateDarkMode(id, !n.isDarkMode());
    }

    @Override
    public void verifyNeighbor(long id) {
        neighborDao.updateNeighborVerification(id, true);
    }

    @Override
    public void unverifyNeighbor(long id) {
        neighborDao.updateNeighborVerification(id, false);
    }

    @Override
    public void updateLanguage(long id, String language) {
        neighborDao.updateLanguage(id, language);
    }

    @Override
    public void setDefaultValues(long id) {
        neighborDao.setDefaultValues(id);
    }

    @Override
    public Optional<Neighbor> findNeighborByMail(String mail) { return neighborDao.findNeighborByMail(mail); }

    @Override
    public void setNewPassword(long id, String newPassword){
        neighborDao.setNewPassword(id, newPassword);
    }
}

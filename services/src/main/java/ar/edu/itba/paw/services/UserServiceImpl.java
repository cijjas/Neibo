package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(final UserDao userDao, final PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getUsers() {
        return userDao.getUsers();
    }

    @Override
    public Optional<User> findUserById(long neighborId) {
        return userDao.findUserById(neighborId);
    }

    @Override
    public Optional<User> findUserByMail(String mail) {
        return userDao.findUserByMail(mail);
    }

    @Override
    public User createNeighbor(final String mail, final String password, final String name, final String surname,
                               final long neighborhoodId, String language, boolean darkMode, boolean verification) {
        User n = findNeighborByMail(mail).orElse(null);
        if (n == null) {
            return userDao.createNeighbor(mail, passwordEncoder.encode(password), name, surname, neighborhoodId, language, darkMode, verification);
        }
        return n;
    }

    @Override
    public List<User> getNeighbors() { return userDao.getNeighbors(); }

    @Override
    public List<User> getNeighborsByNeighborhood(long neighborhoodId) { return userDao.getNeighborsByNeighborhood(neighborhoodId); }

    @Override
    public List<User> getNeighborsSubscribedByPostId(long id) {
        return userDao.getNeighborsSubscribedByPostId(id);
    }

    @Override
    public List<User> getVerifiedNeighborsByNeighborhood(long neighborhoodId){
        return userDao.getNeighborsByNeighborhoodByVerification(neighborhoodId, true);
    }

    @Override
    public List<User> getUnverifiedNeighborsByNeighborhood(long neighborhoodId){
        return userDao.getNeighborsByNeighborhoodByVerification(neighborhoodId, false);
    }

    //@Override
    //public List<Neighbor> getNeighborsByCommunity(long communityId) { return neighborDao.getAllNeighborsByCommunity(communityId); }

    @Override
    public Optional<User> findNeighborById(long id) { return userDao.findNeighborById(id); }

    @Override
    public void toggleDarkMode(long id) {
        User n = findNeighborById(id).orElse(null);
        if (n != null)
            userDao.setUserValues(id, n.getName(), n.getSurname(), n.getPassword(), !n.isDarkMode(), n.getLanguage(), n.isVerified(), n.getRole());
    }

    @Override
    public void verifyNeighbor(long id) {
        User n = findNeighborById(id).orElse(null);
        if (n != null)
            userDao.setUserValues(id, n.getName(), n.getSurname(), n.getPassword(), n.isDarkMode(), n.getLanguage(), true, n.getRole());
    }

    @Override
    public void unverifyNeighbor(long id) {
        User n = findNeighborById(id).orElse(null);
        if (n != null)
            userDao.setUserValues(id, n.getName(), n.getSurname(), n.getPassword(), n.isDarkMode(), n.getLanguage(), false, n.getRole());
    }

    @Override
    public void updateLanguage(long id, String language) {
        User n = findNeighborById(id).orElse(null);
        if (n != null)
            userDao.setUserValues(id, n.getName(), n.getSurname(), n.getPassword(), n.isDarkMode(), language, n.isVerified(), n.getRole());
    }

    @Override
    public void resetPreferenceValues(long id) {
        User n = findNeighborById(id).orElse(null);
        if (n != null)
            userDao.setUserValues(id, n.getName(), n.getSurname(), n.getPassword(), false, "English", n.isVerified(), n.getRole());
    }

    @Override
    public Optional<User> findNeighborByMail(String mail) { return userDao.findNeighborByMail(mail); }

    @Override
    public void setNewPassword(long id, String newPassword){
        User n = findNeighborById(id).orElse(null);
        if (n != null)
            userDao.setUserValues(id, n.getName(), n.getSurname(), newPassword, n.isDarkMode(), n.getLanguage(), n.isVerified(), n.getRole());
    }
}

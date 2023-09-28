package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import enums.Language;
import enums.UserRole;
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
                               final long neighborhoodId, Language language) {
        User n = findUserByMail(mail).orElse(null);
        if (n == null) {
            return userDao.createUser(mail, passwordEncoder.encode(password), name, surname, neighborhoodId, language, false, UserRole.UNVERIFIED_NEIGHBOR);
        }else if (n.getPassword() == null){
            // n is a user from an early version where signing up was not a requirement
            userDao.setUserValues(n.getUserId(), passwordEncoder.encode(password), n.getName(), n.getSurname(), language, false, UserRole.UNVERIFIED_NEIGHBOR);
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
    public List<User> getUnverifiedNeighborsByNeighborhood(long neighborhoodId){
        return userDao.getUnverifiedNeighborsByNeighborhood(neighborhoodId);
    }

    //@Override
    //public List<Neighbor> getNeighborsByCommunity(long communityId) { return neighborDao.getAllNeighborsByCommunity(communityId); }

    @Override
    public Optional<User> findNeighborById(long id) { return userDao.findNeighborById(id); }

    @Override
    public void toggleDarkMode(long id) {
        findUserById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), n.getLanguage(), !n.isDarkMode(), n.getRole()));
    }

    @Override
    public void verifyNeighbor(long id) {
        findNeighborById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), n.getLanguage(), n.isDarkMode(), UserRole.NEIGHBOR));
    }

    @Override
    public void unverifyNeighbor(long id) {
        findNeighborById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), n.getLanguage(), n.isDarkMode(), UserRole.UNVERIFIED_NEIGHBOR));
    }

    @Override
    public void updateLanguage(long id, Language language) {
        findUserById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), language, n.isDarkMode(), n.getRole()));
    }

    @Override
    public void resetPreferenceValues(long id) {
        findNeighborById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), Language.ENGLISH, false, n.getRole()));
    }

    @Override
    public void setNewPassword(long id, String newPassword){
        findNeighborById(id).ifPresent(n -> userDao.setUserValues(id, passwordEncoder.encode(newPassword), n.getName(), n.getSurname(), n.getLanguage(), n.isDarkMode(), n.getRole()));
    }

    @Override
    public Optional<User> findNeighborByMail(String mail) { return userDao.findNeighborByMail(mail); }
}

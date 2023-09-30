package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.User;
import enums.Language;
import enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final ImageService is;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(final UserDao userDao, final ImageService is,final PasswordEncoder passwordEncoder) {
        this.is = is;
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------------------------------------------- USER CREATION ----------------------------------------------------

    @Override
    public User createNeighbor(final String mail, final String password, final String name, final String surname,
                               final long neighborhoodId, Language language) {
        User n = findUserByMail(mail).orElse(null);
        if (n == null) {
            return userDao.createUser(mail, passwordEncoder.encode(password), name, surname, neighborhoodId, language, false, UserRole.UNVERIFIED_NEIGHBOR);
        }else if (n.getPassword() == null){
            // n is a user from an early version where signing up was not a requirement
            userDao.setUserValues(n.getUserId(), passwordEncoder.encode(password), n.getName(), n.getSurname(), language, false,  n.getProfilePictureId(), UserRole.UNVERIFIED_NEIGHBOR);
        }
        return n;
    }

    // ---------------------------------------------- USER GETTERS -----------------------------------------------------

    @Override
    public Optional<User> findUserById(long neighborId) {
        return userDao.findUserById(neighborId);
    }

    @Override
    public Optional<User> findUserByMail(String mail) {
        return userDao.findUserByMail(mail);
    }

    @Override
    public List<User> getNeighborsSubscribedByPostId(long id) {
        return userDao.getNeighborsSubscribedByPostId(id);
    }

    @Override
    public List<User> getNeighbors(long neighborhoodId) {
        return userDao.getUsersByCriteria(UserRole.NEIGHBOR, neighborhoodId);
    }

    @Override
    public List<User> getUnverifiedNeighbors(long neighborhoodId){
        return userDao.getUsersByCriteria(UserRole.UNVERIFIED_NEIGHBOR, neighborhoodId);
    }

    // ---------------------------------------------- USER SETTERS -----------------------------------------------------

    @Override
    public void storeProfilePicture(long userId, MultipartFile image){
        Image i = is.storeImage(image);
        findUserById(userId).ifPresent(n -> userDao.setUserValues(userId, n.getPassword(), n.getName(), n.getSurname(), n.getLanguage(), n.isDarkMode(), i.getImageId(), n.getRole()));
    }


    @Override
    public void toggleDarkMode(long id) {
        userDao.findUserById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), n.getLanguage(), !n.isDarkMode(), n.getProfilePictureId(), n.getRole()));
    }

    @Override
    public void verifyNeighbor(long id) {
        userDao.findUserById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), n.getLanguage(), n.isDarkMode(), n.getProfilePictureId(), UserRole.NEIGHBOR));
    }

    @Override
    public void unverifyNeighbor(long id) {
        userDao.findUserById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), n.getLanguage(), n.isDarkMode(),  n.getProfilePictureId(), UserRole.UNVERIFIED_NEIGHBOR));
    }

    @Override
    public void updateLanguage(long id, Language language) {
        userDao.findUserById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), language, n.isDarkMode(),  n.getProfilePictureId(), n.getRole()));
    }

    @Override
    public void resetPreferenceValues(long id) {
        userDao.findUserById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), Language.ENGLISH, false, n.getProfilePictureId(), n.getRole()));
    }

    @Override
    public void setNewPassword(long id, String newPassword){
        userDao.findUserById(id).ifPresent(n -> userDao.setUserValues(id, passwordEncoder.encode(newPassword), n.getName(), n.getSurname(), n.getLanguage(), n.isDarkMode(),  n.getProfilePictureId(), n.getRole()));
    }

}

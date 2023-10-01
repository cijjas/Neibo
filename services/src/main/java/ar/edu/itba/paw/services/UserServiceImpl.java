package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.User;
import enums.Language;
import enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final ImageService imageService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final NeighborhoodService neighborhoodService;

    @Autowired
    public UserServiceImpl(final UserDao userDao, final ImageService imageService, final PasswordEncoder passwordEncoder, final EmailService emailService, final NeighborhoodService neighborhoodService) {
        this.emailService = emailService;
        this.imageService = imageService;
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.neighborhoodService = neighborhoodService;
    }

    // ---------------------------------------------- USER CREATION ----------------------------------------------------

    @Override
    public User createNeighbor(final String mail, final String password, final String name, final String surname,
                               final long neighborhoodId, Language language, final int identification) {
        User n = findUserByMail(mail).orElse(null);
        if (n == null) {
            return userDao.createUser(mail, passwordEncoder.encode(password), name, surname, neighborhoodId, language, false, UserRole.UNVERIFIED_NEIGHBOR, identification);
        }else if (n.getPassword() == null){
            // n is a user from an early version where signing up was not a requirement
            userDao.setUserValues(n.getUserId(), passwordEncoder.encode(password), n.getName(), n.getSurname(), language, false,  n.getProfilePictureId(), UserRole.UNVERIFIED_NEIGHBOR, identification);
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
        return userDao.getUsersByCriteria(UserRole.NEIGHBOR, neighborhoodId, 0, 0);
    }

    @Override
    public List<User> getUsersPage(UserRole role, long neighborhoodId, int page, int size){
        return userDao.getUsersByCriteria(role, neighborhoodId, page, size);
    }

    public int getTotalPages(UserRole role, long neighborhoodId, int size ){
        return userDao.getTotalUsers(role, neighborhoodId)/size;
    }

    // ---------------------------------------------- USER SETTERS -----------------------------------------------------

    @Override
    public void updateProfilePicture(long userId, MultipartFile image){
        Image i = imageService.storeImage(image);
        findUserById(userId).ifPresent(n -> userDao.setUserValues(userId, n.getPassword(), n.getName(), n.getSurname(), n.getLanguage(), n.isDarkMode(), i.getImageId(), n.getRole(), n.getIdentification()));
    }


    @Override
    public void toggleDarkMode(long id) {
        userDao.findUserById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), n.getLanguage(), !n.isDarkMode(), n.getProfilePictureId(), n.getRole(), n.getIdentification()));
    }

    @Override
    public void verifyNeighbor(long id) {
        User user = userDao.findUserById(id).orElse(null);
        if ( user == null )
            return;
        userDao.setUserValues(id, user.getPassword(), user.getName(), user.getSurname(), user.getLanguage(), user.isDarkMode(), user.getProfilePictureId(), UserRole.NEIGHBOR, user.getIdentification());
        String neighborhood = neighborhoodService.findNeighborhoodById(user.getNeighborhoodId()).orElse(null).getName();
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", user.getName());
        vars.put("neighborhood", neighborhood);
        vars.put("loginPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/");
        if(user.getLanguage() == Language.ENGLISH)
            emailService.sendMessageUsingThymeleafTemplate(user.getMail(), "Verification", "verification-template_en.html", vars);
        else
            emailService.sendMessageUsingThymeleafTemplate(user.getMail(), "Verificación", "verification-template_es.html", vars);
    }

    @Override
    public void unverifyNeighbor(long id) {
        userDao.findUserById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), n.getLanguage(), n.isDarkMode(),  n.getProfilePictureId(), UserRole.UNVERIFIED_NEIGHBOR, n.getIdentification()));
    }

    @Override
    public void updateLanguage(long id, Language language) {
        userDao.findUserById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), language, n.isDarkMode(),  n.getProfilePictureId(), n.getRole(), n.getIdentification()));
    }

    // Will be deprecated if more languages are included
    @Override
    public void toggleLanguage(long id) {
        User user = userDao.findUserById(id).orElse(null);
        if (user == null) {
            return;
        }

        Language newLanguage = (user.getLanguage() == Language.ENGLISH) ? Language.SPANISH : Language.ENGLISH;
        updateLanguage(user.getUserId(), newLanguage);
    }


    @Override
    public void resetPreferenceValues(long id) {
        userDao.findUserById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), Language.ENGLISH, false, n.getProfilePictureId(), n.getRole(), n.getIdentification()));
    }

    @Override
    public void setNewPassword(long id, String newPassword){
        userDao.findUserById(id).ifPresent(n -> userDao.setUserValues(id, passwordEncoder.encode(newPassword), n.getName(), n.getSurname(), n.getLanguage(), n.isDarkMode(),  n.getProfilePictureId(), n.getRole(), n.getIdentification()));
    }

}

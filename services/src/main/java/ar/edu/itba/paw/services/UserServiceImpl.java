package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.BaseNeighborhood;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final ImageService imageService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final NeighborhoodService neighborhoodService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(final UserDao userDao, final ImageService imageService, final PasswordEncoder passwordEncoder, final EmailService emailService, final NeighborhoodService neighborhoodService) {
        this.emailService = emailService;
        this.imageService = imageService;
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.neighborhoodService = neighborhoodService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public User createNeighbor(final String mail, final String password, final String name, final String surname,
                               final long neighborhoodId, Language language, final int identification) {
        LOGGER.info("Creating Neighbor with mail {}", mail);
        User n = findUserByMail(mail).orElse(null);
        if (n == null) {
            User createdUser = userDao.createUser(mail, passwordEncoder.encode(password), name, surname, neighborhoodId, language, false, UserRole.UNVERIFIED_NEIGHBOR, identification);

            //if user created is a neighbor (not worker), send admin email notifying new neighbor
            if(neighborhoodId != 0) {
                emailService.sendNewUserMail(neighborhoodId, name, UserRole.NEIGHBOR);
            }
            return createdUser;
        }else if (n.getPassword() == null){
            // n is a user from an early version where signing up was not a requirement
            userDao.setUserValues(n.getUserId(), passwordEncoder.encode(password), n.getName(), n.getSurname(), language, false,  n.getProfilePictureId(), UserRole.UNVERIFIED_NEIGHBOR, identification, n.getNeighborhoodId());
        }
        return n;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserById(long neighborId) {
        LOGGER.info("Finding User with id {}", neighborId);
        return userDao.findUserById(neighborId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserByMail(String mail) {
        LOGGER.info("Finding User with mail {}", mail);
        return userDao.findUserByMail(mail);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAttending(long eventId, long userId) {
        LOGGER.info("Checking if User {} is attending Event {}", userId,eventId);
        return userDao.isAttending(eventId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getNeighborsSubscribedByPostId(long id) {
        LOGGER.info("Getting Neighbors Subscribed to Post {}", id);
        return userDao.getNeighborsSubscribedByPostId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getNeighbors(long neighborhoodId) {
        LOGGER.info("Getting Neighbors from Neighborhood {}", neighborhoodId);
        return userDao.getUsersByCriteria(UserRole.NEIGHBOR, neighborhoodId, 0, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersPage(UserRole role, long neighborhoodId, int page, int size){
        LOGGER.info("Getting Users from Neighborhood {} with Role {}", neighborhoodId, role);
        return userDao.getUsersByCriteria(role, neighborhoodId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalPages(UserRole role, long neighborhoodId, int size ){
        LOGGER.info("Getting Pages of Users with size {} from Neighborhood {} with Role {}", size, neighborhoodId, role);
        return userDao.getTotalUsers(role, neighborhoodId)/size;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getEventUsers (long eventId) {
        LOGGER.info("Getting User attending Event {}", eventId);
        return userDao.getEventUsers(eventId);
    }


    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void updateProfilePicture(long userId, MultipartFile image){
        LOGGER.info("Updating User {} profile picture", userId);
        Image i = imageService.storeImage(image);
        findUserById(userId).ifPresent(n -> userDao.setUserValues(userId, n.getPassword(), n.getName(), n.getSurname(), n.getLanguage(), n.isDarkMode(), i.getImageId(), n.getRole(), n.getIdentification(), n.getNeighborhoodId()));
    }


    @Override
    public void toggleDarkMode(long id) {
        LOGGER.info("Toggling Dark Mode for User {}", id);
        userDao.findUserById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), n.getLanguage(), !n.isDarkMode(), n.getProfilePictureId(), n.getRole(), n.getIdentification(), n.getNeighborhoodId()));
    }

    @Override
    public void verifyNeighbor(long id) {
        LOGGER.info("Verifying User {}", id);
        User user = userDao.findUserById(id).orElse(null);
        if ( user == null )
            return;
        userDao.setUserValues(id, user.getPassword(), user.getName(), user.getSurname(), user.getLanguage(), user.isDarkMode(), user.getProfilePictureId(), UserRole.NEIGHBOR, user.getIdentification(), user.getNeighborhoodId());
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

    //for users that were rejected/removed from a neighborhood and have selected a new one to become a part of
    @Override
    public void unverifyNeighbor(long id, long neighborhoodId) {
        LOGGER.info("Un-verifying User {}", id);
        userDao.findUserById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), n.getLanguage(), n.isDarkMode(),  n.getProfilePictureId(), UserRole.UNVERIFIED_NEIGHBOR, n.getIdentification(), neighborhoodId));
    }

    @Override
    public void rejectNeighbor(long id) {
        LOGGER.info("Rejecting User {}", id);
        userDao.findUserById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), n.getLanguage(), n.isDarkMode(),  n.getProfilePictureId(), UserRole.REJECTED, n.getIdentification(), BaseNeighborhood.REJECTED_NEIGHBORHOOD.getId()));
    }

    @Override
    public void updateLanguage(long id, Language language) {
        LOGGER.info("Updating Language for User {} to {}", id, language);
        userDao.findUserById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), language, n.isDarkMode(),  n.getProfilePictureId(), n.getRole(), n.getIdentification(), n.getNeighborhoodId()));
    }

    // Will be deprecated if more languages are included
    @Override
    public void toggleLanguage(long id) {
        LOGGER.info("Toggling Language for User {}", id);
        User user = userDao.findUserById(id).orElse(null);
        if (user == null) {
            return;
        }

        Language newLanguage = (user.getLanguage() == Language.ENGLISH) ? Language.SPANISH : Language.ENGLISH;
        updateLanguage(user.getUserId(), newLanguage);
    }


    @Override
    public void resetPreferenceValues(long id) {
        LOGGER.info("Resetting preferences for User {}", id);
        userDao.findUserById(id).ifPresent(n -> userDao.setUserValues(id, n.getPassword(), n.getName(), n.getSurname(), Language.ENGLISH, false, n.getProfilePictureId(), n.getRole(), n.getIdentification(), n.getNeighborhoodId()));
    }

    @Override
    public void setNewPassword(long id, String newPassword){
        LOGGER.info("Setting new password for User {}", id);
        userDao.findUserById(id).ifPresent(n -> userDao.setUserValues(id, passwordEncoder.encode(newPassword), n.getName(), n.getSurname(), n.getLanguage(), n.isDarkMode(),  n.getProfilePictureId(), n.getRole(), n.getIdentification(), n.getNeighborhoodId()));
    }

}

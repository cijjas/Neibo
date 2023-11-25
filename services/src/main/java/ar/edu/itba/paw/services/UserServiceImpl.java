package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.BaseNeighborhood;
import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.UnexpectedException;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.MainEntities.Image;
import ar.edu.itba.paw.models.MainEntities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
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

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public User createNeighbor(final String mail, final String password, final String name, final String surname,
                               final long neighborhoodId, Language language, final String identification) {
        LOGGER.info("Creating Neighbor with mail {}", mail);

        int id = 0;
        try {
            id = Integer.parseInt(identification);
        } catch (NumberFormatException e) {
            LOGGER.error("Error whilst formatting Identification");
            throw new UnexpectedException("Unexpected Error while creating Neighbor");
        }

        User n = findUserByMail(mail).orElse(null);
        if (n == null) {
            User createdUser = userDao.createUser(mail, passwordEncoder.encode(password), name, surname, neighborhoodId, language, false, UserRole.UNVERIFIED_NEIGHBOR, id);

            //if user created is a neighbor (not worker), send admin email notifying new neighbor
            if (neighborhoodId != 0) {
                emailService.sendNewUserMail(neighborhoodId, name, UserRole.NEIGHBOR);
            }
            return createdUser;
        } else if (n.getPassword() == null) {
            // n is a user from an early version where signing up was not a requirement
            n.setPassword(passwordEncoder.encode(password));
            n.setLanguage(language);
            n.setRole(UserRole.UNVERIFIED_NEIGHBOR);
            n.setIdentification(id);
            n.setDarkMode(false);
            n.setName(name);
            n.setSurname(surname);
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
        LOGGER.info("Checking if User {} is attending Event {}", userId, eventId);
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
    public List<User> getUsersPage(UserRole role, long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Users from Neighborhood {} with Role {}", neighborhoodId, role);
        return userDao.getUsersByCriteria(role, neighborhoodId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalPages(UserRole role, long neighborhoodId, int size) {
        LOGGER.info("Getting Pages of Users with size {} from Neighborhood {} with Role {}", size, neighborhoodId, role);
        return (int) Math.ceil((double) userDao.getTotalUsers(role, neighborhoodId) / size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getEventUsers(long eventId) {
        LOGGER.info("Getting User attending Event {}", eventId);
        return userDao.getEventUsers(eventId);
    }

    @Override
    public List<User> getProductRequesters(long productId, int page, int size) {
        return userDao.getProductRequesters(productId, page, size);
    }


    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void updateProfilePicture(long userId, MultipartFile image) {
        LOGGER.info("Updating User {} profile picture", userId);
        Image i = imageService.storeImage(image);
        User user = getUser(userId);
        user.setProfilePicture(i);
    }


    @Override
    public void toggleDarkMode(long id) {
        LOGGER.info("Toggling Dark Mode for User {}", id);
        User user = getUser(id);
        user.setDarkMode(!user.isDarkMode());
    }

    @Override
    public void verifyNeighbor(long id) {
        LOGGER.info("Verifying User {}", id);
        User user = getUser(id);
        // This method has to change
        user.setRole(UserRole.NEIGHBOR);
        String neighborhoodName = neighborhoodService.findNeighborhoodById(user.getNeighborhood().getNeighborhoodId()).orElseThrow(() -> new NotFoundException("Neighborhood not found")).getName();
        emailService.sendVerifiedNeighborMail(user, neighborhoodName);
    }

    //for users that were rejected/removed from a neighborhood and have selected a new one to become a part of
    @Override
    public void unverifyNeighbor(long id, long neighborhoodId) {
        LOGGER.info("Un-verifying User {}", id);
        User user = getUser(id);
        user.setRole(UserRole.UNVERIFIED_NEIGHBOR);
    }

    @Override
    public void rejectNeighbor(long id) {
        LOGGER.info("Rejecting User {}", id);
        User user = getUser(id);
        user.setRole(UserRole.REJECTED);
    }

    // Will be deprecated if more languages are included
    @Override
    public void toggleLanguage(long id) {
        LOGGER.info("Toggling Language for User {}", id);
        User user = getUser(id);
        Language newLanguage = (user.getLanguage() == Language.ENGLISH) ? Language.SPANISH : Language.ENGLISH;
        user.setLanguage(newLanguage);
    }

    @Override
    public void changeNeighborhood(long userId, long neighborhoodId) {
        LOGGER.info("Setting new neighborhood for User {}", userId);
        User user = getUser(userId);
        user.setNeighborhood(neighborhoodService.findNeighborhoodById(neighborhoodId).orElseThrow(()-> new NotFoundException("Neighborhood Not Found")));
    }

    private User getUser(long userId){
        return userDao.findUserById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }
}

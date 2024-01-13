package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.UnexpectedException;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
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

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

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
    public Optional<User> findUserById(long userId) {
        LOGGER.info("Finding User with id {}", userId);

        ValidationUtils.checkUserId(userId);

        return userDao.findUserById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserByMail(String mail) {
        LOGGER.info("Finding User with mail {}", mail);
        return userDao.findUserByMail(mail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getNeighborsSubscribedByPostId(long postId) {
        LOGGER.info("Getting Neighbors Subscribed to Post {}", postId);

        ValidationUtils.checkPostId(postId);

        return userDao.getNeighborsSubscribedByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getNeighbors(long neighborhoodId) {
        LOGGER.info("Getting Neighbors from Neighborhood {}", neighborhoodId);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return userDao.getUsersByCriteria(UserRole.NEIGHBOR, neighborhoodId, 0, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByCriteria(UserRole role, long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Users from Neighborhood {} with Role {}", neighborhoodId, role);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPageAndSize(page, size);

        return userDao.getUsersByCriteria(role, neighborhoodId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalPages(UserRole role, long neighborhoodId, int size) {
        LOGGER.info("Getting Pages of Users with size {} from Neighborhood {} with Role {}", size, neighborhoodId, role);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return (int) Math.ceil((double) userDao.getTotalUsers(role, neighborhoodId) / size);
    }

    //funcion deprecada?? ahora existe attendanceController
    @Override
    @Transactional(readOnly = true)
    public List<User> getEventUsers(long eventId) {
        LOGGER.info("Getting Users attending Event {}", eventId);

        ValidationUtils.checkEventId(eventId);

        return userDao.getEventUsers(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getEventUsersByCriteria(long eventId, int page, int size) {
        LOGGER.info("Getting User attending Event {}", eventId);

        ValidationUtils.checkEventId(eventId);
        ValidationUtils.checkPageAndSize(page, size);

        return userDao.getEventUsersByCriteria(eventId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalEventPages(long eventId, int size) {
        LOGGER.info("Getting Pages of Users with size {} attending Event {}", size, eventId);

        ValidationUtils.checkEventId(eventId);

        return (int) Math.ceil((double) userDao.getEventUsers(eventId).size() / size);
    }

    @Override
    public List<User> getProductRequesters(long productId, int page, int size) {

        ValidationUtils.checkProductId(productId);
        ValidationUtils.checkPageAndSize(page, size);

        return userDao.getProductRequesters(productId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAttending(long eventId, long userId) {
        LOGGER.info("Checking if User {} is attending Event {}", userId, eventId);

        ValidationUtils.checkAttendanceId(eventId, userId);

        return userDao.isAttending(eventId, userId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void updateProfilePicture(long userId, MultipartFile image) {
        LOGGER.info("Updating User {} profile picture", userId);

        ValidationUtils.checkUserId(userId);

        Image i = imageService.storeImage(image);
        User user = getUser(userId);
        user.setProfilePicture(i);
    }

    @Override
    public void updatePhoneNumber(long userId, String phoneNumber) {
        LOGGER.info("Updating User {} phone number", userId);

        ValidationUtils.checkUserId(userId);

        User user = getUser(userId);
        user.setPhoneNumber(phoneNumber);
    }


    @Override
    public void toggleDarkMode(long userId) {
        LOGGER.info("Toggling Dark Mode for User {}", userId);

        ValidationUtils.checkUserId(userId);

        User user = getUser(userId);
        user.setDarkMode(!user.isDarkMode());
    }

    @Override
    public void verifyNeighbor(long userId) {
        LOGGER.info("Verifying User {}", userId);

        ValidationUtils.checkUserId(userId);

        User user = getUser(userId);
        // This method has to change
        user.setRole(UserRole.NEIGHBOR);
        String neighborhoodName = neighborhoodService.findNeighborhoodById(user.getNeighborhood().getNeighborhoodId()).orElseThrow(() -> new NotFoundException("Neighborhood not found")).getName();
        emailService.sendVerifiedNeighborMail(user, neighborhoodName);
    }

    //for users that were rejected/removed from a neighborhood and have selected a new one to become a part of
    @Override
    public void unverifyNeighbor(long userId, long neighborhoodId) {
        LOGGER.info("Un-verifying User {}", userId);

        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        User user = getUser(userId);
        user.setRole(UserRole.UNVERIFIED_NEIGHBOR);
    }

    @Override
    public void rejectNeighbor(long userId) {
        LOGGER.info("Rejecting User {}", userId);

        ValidationUtils.checkUserId(userId);

        User user = getUser(userId);
        user.setRole(UserRole.REJECTED);
    }

    // Will be deprecated if more languages are included
    @Override
    public void toggleLanguage(long userId) {
        LOGGER.info("Toggling Language for User {}", userId);

        ValidationUtils.checkUserId(userId);

        User user = getUser(userId);
        Language newLanguage = (user.getLanguage() == Language.ENGLISH) ? Language.SPANISH : Language.ENGLISH;
        user.setLanguage(newLanguage);
    }

    @Override
    public void changeNeighborhood(long userId, long neighborhoodId) {
        LOGGER.info("Setting new neighborhood for User {}", userId);

        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        User user = getUser(userId);
        user.setNeighborhood(neighborhoodService.findNeighborhoodById(neighborhoodId).orElseThrow(()-> new NotFoundException("Neighborhood Not Found")));
    }

    private User getUser(long userId){

        ValidationUtils.checkUserId(userId);

        return userDao.findUserById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public User updateUser(long userId, String mail, String name, String surname, String password, Boolean darkMode, String phoneNumber, MultipartFile profilePicture, Integer identification, Integer languageId, Integer userRoleId) {
        LOGGER.info("Updating User {}", userId);

        ValidationUtils.checkUserId(userId);

        User user = getUser(userId);

        if (mail != null && !mail.isEmpty())
            user.setMail(mail);
        if (name != null && !name.isEmpty())
            user.setName(name);
        if (surname != null && !surname.isEmpty())
            user.setSurname(surname);
        if (password != null && !password.isEmpty())
            user.setPassword(passwordEncoder.encode(password));
        if (darkMode != null)
            user.setDarkMode(darkMode);
        if (phoneNumber != null && !phoneNumber.isEmpty())
            user.setPhoneNumber(phoneNumber);
        if (profilePicture != null && !profilePicture.isEmpty()) {
            Image i = imageService.storeImage(profilePicture);
            user.setProfilePicture(i);
        }
        if (identification != null)
            user.setIdentification(identification);
        if(languageId != null)
            user.setLanguage(Language.fromId(languageId));
        if(userRoleId != null)
            user.setRole(UserRole.fromId(userRoleId));

        return user;
    }
}

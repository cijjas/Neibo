package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.exceptions.UnexpectedException;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;
    private final NeighborhoodDao neighborhoodDao;
    private final ImageService imageService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(final UserDao userDao, final ImageService imageService, final PasswordEncoder passwordEncoder, final EmailService emailService, final NeighborhoodDao neighborhoodDao) {
        this.emailService = emailService;
        this.imageService = imageService;
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.neighborhoodDao = neighborhoodDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public User createNeighbor(final String mail, final String password, final String name, final String surname,
                               final long neighborhoodId, String languageURN, final Integer identification) {
        LOGGER.info("Creating Neighbor with mail {}", mail);

        Language language = Language.ENGLISH;
        if (languageURN != null) {
            long languageId = ValidationUtils.extractURNId(languageURN);
            ValidationUtils.checkLanguageId(languageId);
            language = Language.fromId(languageId);
        }

        User n = findUser(mail).orElse(null);
        if (n == null) {
            User createdUser = userDao.createUser(mail, passwordEncoder.encode(password), name, surname, neighborhoodId, language, false, UserRole.UNVERIFIED_NEIGHBOR, identification);

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
            n.setIdentification(identification);
            n.setDarkMode(false);
            n.setName(name);
            n.setSurname(surname);
        }
        return n;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUser(long userId) {
        LOGGER.info("Finding User {}", userId);

        ValidationUtils.checkUserId(userId);

        return userDao.findUser(userId);
    }

    @Override
    public Optional<User> findUser(long userId, long neighborhoodId) {
        LOGGER.info("Finding User {} from Neighborhood {}", userId, neighborhoodId);

        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkNeighborhoodIdInUsers(neighborhoodId);

        return userDao.findUser(userId, neighborhoodId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUser(String mail) {
        LOGGER.info("Finding User {}", mail);

        return userDao.findUser(mail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers(String userRoleURN, long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Users with Role {} from Neighborhood {} ", userRoleURN, neighborhoodId);

        Long userRoleId = ValidationUtils.checkURNAndExtractUserRoleId(userRoleURN);

        ValidationUtils.checkPageAndSize(page, size);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return userDao.getUsers(userRoleId, neighborhoodId, page, size);
    }

    // ---------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public int calculateUserPages(String userRoleURN, long neighborhoodId, int size) {
        LOGGER.info("Calculating User Pages with Role {} from Neighborhood {} ", userRoleURN, neighborhoodId);

        Long userRoleId = ValidationUtils.checkURNAndExtractUserRoleId(userRoleURN);

        ValidationUtils.checkNeighborhoodIdInUsers(neighborhoodId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(userDao.countUsers(userRoleId, neighborhoodId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public User updateUser(long userId, String mail, String name, String surname, String password, Boolean darkMode, String phoneNumber, String profilePictureURN, Integer identification, String languageURN, String userRoleURN) {
        LOGGER.info("Updating User {}", userId);

        User user = userDao.findUser(userId).orElseThrow(() -> new NotFoundException("User not found"));

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
        if (profilePictureURN != null) {
            long imageId = ValidationUtils.extractURNId(profilePictureURN);
            ValidationUtils.checkImageId(imageId);
            Image i = imageService.findImage(imageId).orElseThrow(() -> new NotFoundException("Image not found"));
            user.setProfilePicture(i);
        }
        if (identification != null)
            user.setIdentification(identification);
        if (languageURN != null) {
            long languageId = ValidationUtils.extractURNId(languageURN);
            ValidationUtils.checkLanguageId(languageId);
            user.setLanguage(Language.fromId(languageId));
        }
        if (userRoleURN != null) {
            long userRoleId = ValidationUtils.extractURNId(userRoleURN);
            ValidationUtils.checkUserRoleId(userRoleId);
            user.setRole(UserRole.fromId(userRoleId));
        }

        return user;
    }

    // ---------------------------------------------- USERS DELETE -----------------------------------------------------

    @Override
    public boolean deleteUser(long userId) {
        LOGGER.info("Deleting User {}", userId);

        ValidationUtils.checkUserId(userId);

        return userDao.deleteUser(userId);
    }
}

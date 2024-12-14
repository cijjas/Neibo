package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
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
    private final ImageService imageService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(final UserDao userDao, final ImageService imageService, final PasswordEncoder passwordEncoder, final EmailService emailService) {
        this.emailService = emailService;
        this.imageService = imageService;
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public User createNeighbor(final String mail, final String password, final String name, final String surname,
                               final long neighborhoodId, Long languageId, final Integer identification) {
        LOGGER.info("Creating Neighbor with mail {}", mail);

        Language language = Language.ENGLISH;
        if (languageId != null)
            language = Language.fromId(languageId);

        Optional<User> n = userDao.findUser(mail);
        if (!n.isPresent()) {
            User createdUser = userDao.createUser(mail, passwordEncoder.encode(password), name, surname, neighborhoodId, language, false, UserRole.UNVERIFIED_NEIGHBOR, identification);

            // If user created is a neighbor (not worker), send admin email notifying new neighbor
            if (neighborhoodId != 0) {
                emailService.sendNewUserMail(neighborhoodId, name, UserRole.NEIGHBOR);
            }
            return createdUser;
        } else if (n.get().getPassword() == null) {
            User earlyAccessUser = n.get();
            // n is a user from an early version where signing up was not a requirement
            earlyAccessUser.setPassword(passwordEncoder.encode(password));
            earlyAccessUser.setLanguage(language);
            earlyAccessUser.setRole(UserRole.UNVERIFIED_NEIGHBOR);
            earlyAccessUser.setIdentification(identification);
            earlyAccessUser.setDarkMode(false);
            earlyAccessUser.setName(name);
            earlyAccessUser.setSurname(surname);
        }

        // Case where a User already exists with this email

        return n.get();
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUser(long userId, long neighborhoodId) {
        LOGGER.info("Finding User {} from Neighborhood {}", userId, neighborhoodId);

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
    public List<User> getUsers(Long userRoleId, long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Users with Role {} from Neighborhood {} ", userRoleId, neighborhoodId);

        return userDao.getUsers(userRoleId, neighborhoodId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateUserPages(Long userRoleId, long neighborhoodId, int size) {
        LOGGER.info("Calculating User Pages with Role {} from Neighborhood {} ", userRoleId, neighborhoodId);

        return PaginationUtils.calculatePages(userDao.countUsers(userRoleId, neighborhoodId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public User updateUser(long userId, String mail, String name, String surname, String password, Boolean darkMode, String phoneNumber, Long profilePictureId, Integer identification, Long languageId, Long userRoleId) {
        LOGGER.info("Updating User {}", userId);

        User user = userDao.findUser(userId).orElseThrow(NotFoundException::new);

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
        if (profilePictureId != null) {
            Image i = imageService.findImage(profilePictureId).orElseThrow(NotFoundException::new);
            user.setProfilePicture(i);
        }
        if (identification != null)
            user.setIdentification(identification);
        if (languageId != null)
            user.setLanguage(Language.fromId(languageId));
        if (userRoleId != null)
            user.setRole(UserRole.fromId(userRoleId));

        return user;
    }
}

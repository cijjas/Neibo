package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.Neighborhood;
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
    private final ImageDao imageDao;
    private final NeighborhoodDao neighborhoodDao;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, ImageDao imageDao, NeighborhoodDao neighborhoodDao, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.emailService = emailService;
        this.imageDao = imageDao;
        this.userDao = userDao;
        this.neighborhoodDao = neighborhoodDao;
        this.passwordEncoder = passwordEncoder;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public User createUser(long neighborhoodId, String mail, String name, String surname, String password,
                           Integer identification, Long languageId, Long userRoleId) {
        LOGGER.info("Creating Neighbor with mail {}, name {}, surname {}, identification {}, Language {} in Neighborhood {}", mail, name, surname, identification, languageId, neighborhoodId);

        UserRole userRole = UserRole.UNVERIFIED_NEIGHBOR;
        if (userRoleId != null)
            userRole = UserRole.fromId(userRoleId).get(); // Controller layer guarantees non-empty Optional
        Language language = Language.ENGLISH;
        if (languageId != null)
            language = Language.fromId(languageId).get(); // Controller layer guarantees non-empty Optional

        Optional<User> n = userDao.findUser(mail);
        if (!n.isPresent()) {
            User createdUser = userDao.createUser(neighborhoodId, mail, name, surname, passwordEncoder.encode(password), identification, language, false, userRole);

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
            earlyAccessUser.setRole(userRole);
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
    public Optional<User> findUser(long userId) {
        LOGGER.info("Finding User {}", userId);

        return userDao.findUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUser(String mail) {
        LOGGER.info("Finding User {}", mail);

        return userDao.findUser(mail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers(Long neighborhoodId, Long userRoleId, int page, int size) {
        LOGGER.info("Getting Users with Role {} from Neighborhood {} ", userRoleId, neighborhoodId);

        return userDao.getUsers(neighborhoodId, userRoleId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateUserPages(Long neighborhoodId, Long userRoleId, int size) {
        LOGGER.info("Calculating User Pages with Role {} from Neighborhood {} ", userRoleId, neighborhoodId);

        return PaginationUtils.calculatePages(userDao.countUsers(neighborhoodId, userRoleId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public User updateUser(long userId, Long neighborhoodId, String mail, String name, String surname, String password, Integer identification, Long languageId, Long profilePictureId, Boolean darkMode, String phoneNumber, Long userRoleId) {
        LOGGER.info("Updating User {} from Neighborhood {}", userId, neighborhoodId);

        User user = userDao.findUser(userId).orElseThrow(NotFoundException::new);

        if (neighborhoodId != null){
            Neighborhood n = neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);
            user.setNeighborhood(n);
        }
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
            Image i = imageDao.findImage(profilePictureId).orElseThrow(NotFoundException::new);
            user.setProfilePicture(i);
        }
        if (identification != null)
            user.setIdentification(identification);
        if (languageId != null)
            user.setLanguage(Language.fromId(languageId).get()); // Controller layer guarantees non-empty optional
        if (userRoleId != null)
            user.setRole(UserRole.fromId(userRoleId).get()); // Controller layer guarantees non-empty optional

        return user;
    }
}

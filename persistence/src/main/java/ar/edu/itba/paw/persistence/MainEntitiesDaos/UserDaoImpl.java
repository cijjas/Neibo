package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

@Repository
public class UserDaoImpl implements UserDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // ---------------------------------------------- USERS INSERT -----------------------------------------------------

    @Override
    public User createUser(final String mail, final String password, final String name, final String surname,
                           final long neighborhoodId, final Language language, final boolean darkMode, final UserRole role, final int identification) {
        LOGGER.debug("Inserting User {}", mail);

        User user = new User.Builder()
                .name(name).mail(mail)
                .surname(surname)
                .password(password)
                .neighborhood(em.find(Neighborhood.class, neighborhoodId))
                .darkMode(darkMode)
                .language(language)
                .role(role)
                .identification(identification)
                .creationDate(new Date(System.currentTimeMillis()))
                .build();
        em.persist(user);
        return user;
    }

    // ---------------------------------------------- USERS SELECT -----------------------------------------------------

    @Override
    public Optional<User> findUser(final long userId) {
        LOGGER.debug("Selecting User with Id {}", userId);

        return Optional.ofNullable(em.find(User.class, userId));
    }

    @Override
    public Optional<User> findUser(final long userId, long neighborhoodId) {
        LOGGER.debug("Selecting User with userId {}, neighborhoodId {}", userId, neighborhoodId);

        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.userId = :userId AND u.neighborhood.id = :neighborhoodId",
                User.class
        );

        query.setParameter("userId", userId);
        query.setParameter("neighborhoodId", neighborhoodId);

        List<User> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Optional<User> findUser(final String mail) {
        LOGGER.debug("Selecting User with mail {}", mail);
        TypedQuery<User> query = em.createQuery("FROM User WHERE mail = :mail", User.class);
        query.setParameter("mail", mail);
        return query.getResultList().stream().findFirst();
    }


    @Override
    public List<User> getUsers(Long userRoleId, long neighborhoodId, int page, int size) {
        LOGGER.debug("Selecting Users with Role {} and from Neighborhood {}", userRoleId, neighborhoodId);

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // Query to fetch user IDs
        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<User> idRoot = idQuery.from(User.class);
        idQuery.select(idRoot.get("userId"));

        List<Predicate> predicates = new ArrayList<>();
        if (userRoleId != null) {
            predicates.add(cb.equal(idRoot.get("role"), UserRole.fromId(userRoleId)));
        }
        if (neighborhoodId >= 0) {
            Join<User, Neighborhood> neighborhoodJoin = idRoot.join("neighborhood");
            predicates.add(cb.equal(neighborhoodJoin.get("neighborhoodId"), neighborhoodId));
        }
        idQuery.where(predicates.toArray(new Predicate[0]));

        // Order by creationDate
        idQuery.orderBy(cb.asc(idRoot.get("creationDate")));

        TypedQuery<Long> idTypedQuery = em.createQuery(idQuery);
        if (page > 0) {
            idTypedQuery.setFirstResult((page - 1) * size);
            idTypedQuery.setMaxResults(size);
        }
        List<Long> userIds = idTypedQuery.getResultList();
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Query to fetch User entities using the IDs
        CriteriaQuery<User> dataQuery = cb.createQuery(User.class);
        Root<User> dataRoot = dataQuery.from(User.class);
        dataQuery.where(dataRoot.get("userId").in(userIds));
        dataQuery.orderBy(cb.asc(dataRoot.get("creationDate"))); // Order by creationDate
        TypedQuery<User> dataTypedQuery = em.createQuery(dataQuery);

        return dataTypedQuery.getResultList();
    }

    @Override
    public int countUsers(Long userRoleId, long neighborhoodId) {
        LOGGER.debug("Selecting Users Count that have Role {} and from Neighborhood {}", userRoleId, neighborhoodId);

        StringBuilder jpqlConditions = new StringBuilder("SELECT COUNT(u) FROM User u WHERE 1 = 1");
        if (userRoleId != null)
            jpqlConditions.append(" AND u.role = :role");
        if (neighborhoodId > 0)
            jpqlConditions.append(" AND u.neighborhood.id = :neighborhoodId");
        TypedQuery<Long> query = em.createQuery(jpqlConditions.toString(), Long.class);
        if (userRoleId != null)
            query.setParameter("role", UserRole.fromId(userRoleId));
        if (neighborhoodId > 0)
            query.setParameter("neighborhoodId", neighborhoodId);
        return query.getSingleResult().intValue();
    }

    @Override
    public List<User> getEventUsers(long eventId) {
        LOGGER.debug("Selecting Users that will attend Event {}", eventId);

        String hql = "SELECT u FROM User u " +
                "JOIN u.eventsSubscribed e " +
                "WHERE e.eventId = :eventId";
        return em.createQuery(hql, User.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }

    // ---------------------------------------------- USERS DELETE-----------------------------------------------------

    @Override
    public boolean deleteUser(long userId) {
        LOGGER.debug("Deleting User with Id {}", userId);
        User user = em.find(User.class, userId);
        if (user != null) {
            em.remove(user);
            return true;
        }
        return false;
    }
}

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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    public List<User> getNeighborsSubscribed(long postId) {
        LOGGER.debug("Selecting Neighbors that are subscribed to Post {}", postId);

        String hql = "SELECT u FROM User u " +
                "JOIN u.posts p " +
                "WHERE p.postId = :postId AND u.role = :role";

        return em.createQuery(hql, User.class)
                .setParameter("postId", postId)
                .setParameter("role", UserRole.NEIGHBOR)
                .getResultList();
    }

    @Override
    public List<User> getUsers(String role, long neighborhoodId, int page, int size) {
        LOGGER.debug("Selecting Users with Role {} and from Neighborhood {}", role, neighborhoodId);

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<User> idRoot = idQuery.from(User.class);
        idQuery.select(idRoot.get("userId"));
        List<Predicate> predicates = new ArrayList<>();
        if (role != null)
            predicates.add(cb.equal(idRoot.get("role"), UserRole.valueOf(role.toUpperCase())));
        if (neighborhoodId > 0) {
            Join<User, Neighborhood> neighborhoodJoin = idRoot.join("neighborhood");
            predicates.add(cb.equal(neighborhoodJoin.get("neighborhoodId"), neighborhoodId));
        }
        idQuery.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Long> idTypedQuery = em.createQuery(idQuery);
        if (page > 0) {
            idTypedQuery.setFirstResult((page - 1) * size);
            idTypedQuery.setMaxResults(size);
        }
        List<Long> userIds = idTypedQuery.getResultList();
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }

        CriteriaQuery<User> dataQuery = cb.createQuery(User.class);
        Root<User> dataRoot = dataQuery.from(User.class);
        dataQuery.where(dataRoot.get("userId").in(userIds));
        TypedQuery<User> dataTypedQuery = em.createQuery(dataQuery);

        return dataTypedQuery.getResultList();
    }

    @Override
    public int countTotalUsers(String role, long neighborhoodId) {
        LOGGER.debug("Selecting Users Count that have Role {} and from Neighborhood {}", role, neighborhoodId);

        StringBuilder jpqlConditions = new StringBuilder("SELECT COUNT(u) FROM User u WHERE 1 = 1");
        if (role != null)
            jpqlConditions.append(" AND u.role = :role");
        if (neighborhoodId > 0)
            jpqlConditions.append(" AND u.neighborhood.id = :neighborhoodId");
        TypedQuery<Long> query = em.createQuery(jpqlConditions.toString(), Long.class);
        if (role != null)
            query.setParameter("role", UserRole.valueOf(role.toUpperCase()));
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

    @Override
    public List<User> getEventUsers(long eventId, int page, int size) {
        LOGGER.debug("Selecting Users that will attend Event {}", eventId);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u JOIN u.eventsSubscribed e WHERE e.eventId = :eventId", User.class);
        query.setParameter("eventId", eventId);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public boolean isAttending(long eventId, long userId) {
        LOGGER.debug("Selecting User {} that attends Event {}", userId, eventId);

        String sql = "SELECT COUNT(*) FROM events_users WHERE eventid = :eventId AND userid = :userId";
        BigInteger result = (BigInteger) em.createNativeQuery(sql)
                .setParameter("eventId", eventId)
                .setParameter("userId", userId)
                .getSingleResult();

        return result.intValue() == 1;
    }

    @Override
    public List<User> getProductRequesters(long productId, int page, int size) {
        LOGGER.debug("Selecting Users that requested product {}", productId);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u JOIN u.requestedProducts p WHERE p.productId = :productId", User.class);
        query.setParameter("productId", productId);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }
}

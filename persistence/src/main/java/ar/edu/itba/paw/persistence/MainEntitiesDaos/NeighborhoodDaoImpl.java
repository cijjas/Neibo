package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class NeighborhoodDaoImpl implements NeighborhoodDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborhoodDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // ----------------------------------------- NEIGHBORHOODS INSERT --------------------------------------------------

    @Override
    public Neighborhood createNeighborhood(String name) {
        LOGGER.debug("Inserting Neighborhood {}", name);
        Neighborhood neighborhood = new Neighborhood.Builder()
                .name(name)
                .build();
        em.persist(neighborhood);
        return neighborhood;
    }

    // ----------------------------------------- NEIGHBORHOODS SELECT --------------------------------------------------

    @Override
    public Optional<Neighborhood> findNeighborhood(long id) {
        LOGGER.debug("Selecting Neighborhood with id {}", id);
        return Optional.ofNullable(em.find(Neighborhood.class,  id));
    }

    @Override
    public Optional<Neighborhood> findNeighborhood(String name) {
        LOGGER.debug("Selecting Neighborhood with name {}", name);
        TypedQuery<Neighborhood> query = em.createQuery("FROM Neighborhood WHERE neighborhoodname = :neighborhoodName", Neighborhood.class);
        query.setParameter("neighborhoodName", name);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<Neighborhood> getNeighborhoods() {
        LOGGER.debug("Selecting All Neighborhoods");
        String jpql = "SELECT n FROM Neighborhood n";
        TypedQuery<Neighborhood> query = em.createQuery(jpql, Neighborhood.class);
        return query.getResultList();
    }

    @Override
    public List<Neighborhood> getNeighborhoods(int page, int size) {
        LOGGER.debug("Selecting All Neighborhoods");
        String jpql = "SELECT n FROM Neighborhood n";
        TypedQuery<Neighborhood> query = em.createQuery(jpql, Neighborhood.class);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    // ---------------------------------------------------

    @Override
    public int getNeighborhoodsCount() {
        LOGGER.debug("Counting All Neighborhoods");
        String jpql = "SELECT COUNT(n) FROM Neighborhood n";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        return query.getSingleResult().intValue();
    }

}

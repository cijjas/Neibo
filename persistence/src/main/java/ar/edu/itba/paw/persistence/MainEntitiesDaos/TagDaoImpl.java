package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.models.Entities.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class TagDaoImpl implements TagDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // ---------------------------------------------- TAGS INSERT ------------------------------------------------------

    @Override
    public Tag createTag(String name) {
        LOGGER.debug("Inserting Tag {}", name);
        Tag tag = new Tag.Builder()
                .tag(name)
                .build();
        em.persist(tag);
        return tag;
    }

    // ---------------------------------------------- TAGS SELECT ------------------------------------------------------

    @Override
    public List<Tag> getTags(Long postId, long neighborhoodId, int page, int size) {
        LOGGER.debug("Selecting Tags By Criteria");
        TypedQuery<Tag> query = null;
        if(postId == null ) {
            query = em.createQuery("SELECT t FROM Tag t ", Tag.class);
        } else {
            query = em.createQuery("SELECT DISTINCT t FROM Tag t " +
                            "JOIN t.posts p " +
                            "JOIN p.user u " +
                            "JOIN u.neighborhood nh " +
                            "WHERE nh.neighborhoodId = :neighborhoodId", Tag.class)
                    .setParameter("neighborhoodId", neighborhoodId);
        }
        return query.setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public int countTags(Long postId, long neighborhoodId) {
        LOGGER.debug("Selecting Tags By Criteria");
        TypedQuery<Long> query = null;
        if(postId != null) {
            query = em.createQuery("SELECT COUNT(t) FROM Tag t JOIN t.posts p WHERE p.postId = :postId", Long.class)
                    .setParameter("postId", postId);
        } else {
            query = em.createQuery("SELECT DISTINCT COUNT(t) FROM Tag t " +
                            "JOIN t.posts p " +
                            "JOIN p.user u " +
                            "JOIN u.neighborhood nh " +
                            "WHERE nh.neighborhoodId = :neighborhoodId", Long.class)
                    .setParameter("neighborhoodId", neighborhoodId);
        }
        Long count = query.getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public List<Tag> getTags(long postId) {
        LOGGER.debug("Selecting Tags for Post with postId {}", postId);
        return em.createQuery("SELECT t FROM Tag t JOIN t.posts p WHERE p.postId = :postId", Tag.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    @Override
    public List<Tag> getNeighborhoodTags(long neighborhoodId) {
        LOGGER.debug("Selecting Tag List from Neighborhood {}", neighborhoodId);
        return em.createQuery("SELECT DISTINCT t FROM Tag t " +
                        "JOIN t.posts p " +
                        "JOIN p.user u " +
                        "JOIN u.neighborhood nh " +
                        "WHERE nh.neighborhoodId = :neighborhoodId", Tag.class)
                .setParameter("neighborhoodId", neighborhoodId)
                .getResultList();
    }

//    @Override
//    public int getTagsCount(long neighborhoodId) {
//        LOGGER.debug("Selecting Tag List from Neighborhood {}", neighborhoodId);
//        return em.createQuery("SELECT DISTINCT t FROM Tag t " +
//                        "JOIN t.posts p " +
//                        "JOIN p.user u " +
//                        "JOIN u.neighborhood nh " +
//                        "WHERE nh.neighborhoodId = :neighborhoodId", Tag.class)
//                .setParameter("neighborhoodId", neighborhoodId)
//                .getResultList().size();
//    }
//
//    @Override
//    public int getTagsCountByPostId(long postId) {
//        LOGGER.debug("Selecting Tag List from Neighborhood {}", postId);
//        return em.createQuery("SELECT DISTINCT t FROM Tag t " +
//                        "JOIN t.posts p " +
//                        "WHERE p.postId = :postId", Tag.class)
//                .setParameter("postId", postId)
//                .getResultList().size();
//    }

    @Override
    public List<Tag> getAllTags() {
        LOGGER.debug("Selecting Complete Tag List");
        return em.createQuery("SELECT t FROM Tag t", Tag.class).getResultList();
    }
}

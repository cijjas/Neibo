package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.models.Entities.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Optional<Tag> findTag(long neighborhoodId, long tagId) {
        LOGGER.debug("Selecting Tag with Neighborhood Id {} and Tag Id {}", neighborhoodId, tagId);

        TypedQuery<Tag> query = em.createQuery(
                "SELECT t FROM Tag t JOIN t.neighborhoods n WHERE t.tagId = :tagId AND n.neighborhoodId = :neighborhoodId",
                Tag.class
        );

        query.setParameter("tagId", tagId);
        query.setParameter("neighborhoodId", neighborhoodId);

        List<Tag> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Optional<Tag> findTag(String tagName) {
        LOGGER.debug("Selecting Tag with name {}", tagName);

        TypedQuery<Tag> query = em.createQuery("FROM Tag WHERE tag = :tag", Tag.class);
        query.setParameter("tag", tagName);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<Tag> getTags(long neighborhoodId, Long postId, int page, int size) {
        LOGGER.debug("Selecting Tags with Neighborhood Id {} and Post Id {}", neighborhoodId, postId);

        String queryStr;
        TypedQuery<Object[]> idQuery;

        if (postId != null) {
            queryStr =
                    "SELECT DISTINCT t.tagId, t.tag " +
                            "FROM Tag t " +
                            "JOIN t.posts p " +
                            "WHERE p.postId = :postId " +
                            "ORDER BY t.tag";
            idQuery = em.createQuery(queryStr, Object[].class)
                    .setParameter("postId", postId);
        } else {
            queryStr =
                    "SELECT DISTINCT t.tagId, t.tag " +
                            "FROM Tag t " +
                            "JOIN t.neighborhoods n " +
                            "WHERE n.neighborhoodId = :neighborhoodId " +
                            "ORDER BY t.tag";
            idQuery = em.createQuery(queryStr, Object[].class)
                    .setParameter("neighborhoodId", neighborhoodId);
        }

        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);

        List<Object[]> results = idQuery.getResultList();
        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> tagIds = results.stream()
                .map(obj -> (Long) obj[0])
                .collect(Collectors.toList());

        TypedQuery<Tag> tagQuery = em.createQuery(
                "SELECT t FROM Tag t " +
                        "WHERE t.tagId IN :tagIds " +
                        "ORDER BY t.tag",
                Tag.class
        );
        tagQuery.setParameter("tagIds", tagIds);

        return tagQuery.getResultList();
    }


    @Override
    public int countTags(long neighborhoodId, Long postId) {
        LOGGER.debug("Counting Tags with Neighborhood Id {} and Post Id {}", neighborhoodId, postId);

        String queryStr;
        TypedQuery<Long> query;

        if (postId != null) {
            queryStr = "SELECT COUNT(DISTINCT t.tagId) " +
                    "FROM Tag t " +
                    "JOIN t.posts p " +
                    "WHERE p.postId = :postId";
            query = em.createQuery(queryStr, Long.class)
                    .setParameter("postId", postId);
        } else {
            queryStr = "SELECT COUNT(DISTINCT t.tagId) " +
                    "FROM Tag t " +
                    "JOIN t.neighborhoods n " +
                    "WHERE n.neighborhoodId = :neighborhoodId";
            query = em.createQuery(queryStr, Long.class)
                    .setParameter("neighborhoodId", neighborhoodId);
        }

        return query.getSingleResult().intValue();
    }

    // ---------------------------------------------- TAGS DELETE ------------------------------------------------------

    @Override
    public boolean deleteTag(long tagId) {
        LOGGER.debug("Deleting Tag with Tag Id {}", tagId);
        Tag tag = em.find(Tag.class, tagId);
        if (tag != null) {
            em.remove(tag);
            return true;
        }
        return false;
    }
}

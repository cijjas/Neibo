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
        LOGGER.debug("Selecting Tag {} in Neighborhood {}", tagId, neighborhoodId);

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
        LOGGER.debug("Selecting Tags By Criteria");

        TypedQuery<Long> query = null;
        if (postId != null) {
            query = em.createQuery("SELECT t.tagId FROM Tag t JOIN t.posts p WHERE p.postId = :postId ORDER BY t.tagId", Long.class)
                    .setParameter("postId", postId);
        } else {
            // Case when postId is not provided but neighborhoodId is provided (applies to both normal user/admin and superadmin)
            query = em.createQuery(
                            "SELECT DISTINCT t.tagId FROM Tag t JOIN t.neighborhoods n WHERE n.neighborhoodId = :neighborhoodId ORDER BY t.tagId", Long.class)
                    .setParameter("neighborhoodId", neighborhoodId);
        }
        query.setFirstResult((page - 1) * size)
                .setMaxResults(size);
        List<Long> tagIds = query.getResultList();

        if (!tagIds.isEmpty()) {
            TypedQuery<Tag> tagQuery = em.createQuery(
                    "SELECT t FROM Tag t WHERE t.tagId IN :tagIds ORDER BY t.tagId", Tag.class);
            tagQuery.setParameter("tagIds", tagIds);
            return tagQuery.getResultList();
        }

        return Collections.emptyList();
    }


    @Override
    public int countTags(long neighborhoodId, Long postId) {
        LOGGER.debug("Counting Tags By Criteria");

        TypedQuery<Long> query = null;
        if (postId != null) {
            query = em.createQuery("SELECT t.tagId FROM Tag t JOIN t.posts p WHERE p.postId = :postId ORDER BY t.tagId", Long.class)
                    .setParameter("postId", postId);
        } else {
            // Case when postId is not provided but neighborhoodId is provided (applies to both normal user/admin and superadmin)
            query = em.createQuery(
                            "SELECT DISTINCT t.tagId FROM Tag t JOIN t.neighborhoods n WHERE n.neighborhoodId = :neighborhoodId ORDER BY t.tagId", Long.class)
                    .setParameter("neighborhoodId", neighborhoodId);
        }

        List<Long> tagIds = query.getResultList();

        if (!tagIds.isEmpty()) {
            TypedQuery<Tag> tagQuery = em.createQuery(
                    "SELECT t FROM Tag t WHERE t.tagId IN :tagIds ORDER BY t.tagId", Tag.class);
            tagQuery.setParameter("tagIds", tagIds);
            return tagQuery.getResultList().size();
        }

        return 0;
    }

    // ---------------------------------------------- TAGS DELETE ------------------------------------------------------
    @Override
    public boolean deleteTag(long tagId) {
        LOGGER.debug("Deleting Tag with tagId {}", tagId);
        Tag tag = em.find(Tag.class, tagId);
        if (tag != null) {
            em.remove(tag);
            return true;
        }
        return false;
    }

}

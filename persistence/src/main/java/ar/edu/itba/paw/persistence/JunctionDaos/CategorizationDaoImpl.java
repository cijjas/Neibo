package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.models.Entities.Categorization;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.models.Entities.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class CategorizationDaoImpl implements CategorizationDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategorizationDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // ---------------------------------------------- CATEGORIZATION INSERT ------------------------------------------------------

    @Override
    public Categorization createCategorization(final long tagId, final long postId) {
        LOGGER.debug("Inserting Category");

        Categorization categorization = new Categorization(em.find(Post.class, postId), em.find(Tag.class, tagId));
        em.persist(categorization);
        return categorization;
    }

    // ---------------------------------------------- CATEGORIZATION INSERT ------------------------------------------------------

    @Override
    public Optional<Categorization> findCategorization(final long tagId, final long postId) {
        LOGGER.debug("Selecting Category for Post {} and Tag {}", postId, tagId);

        return em.createQuery("SELECT c FROM Categorization c WHERE c.post.postId = :postId AND c.tag.tagId = :tagId", Categorization.class)
                .setParameter("postId", postId)
                .setParameter("tagId", tagId)
                .getResultList().stream().findFirst();
    }

    // ---------------------------------------------- CATEGORIZATION DELETE ------------------------------------------------------

    @Override
    public boolean deleteCategorization(long tagId, long postId) {
        LOGGER.debug("Deleting Category for Post {} and Tag {}", postId, tagId);

        int deleteCount = em.createQuery("DELETE FROM Categorization c WHERE c.post.postId = :postId AND c.tag.tagId = :tagId")
                .setParameter("postId", postId)
                .setParameter("tagId", tagId)
                .executeUpdate();

        return deleteCount > 0;
    }
}

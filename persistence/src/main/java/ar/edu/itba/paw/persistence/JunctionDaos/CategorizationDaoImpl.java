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
    public Categorization createCategorization(long postId, long tagId) {
        LOGGER.debug("Inserting Category with Post Id {} and Tag Id {}", postId, tagId);

        Categorization categorization = new Categorization(em.find(Post.class, postId), em.find(Tag.class, tagId));
        em.persist(categorization);
        return categorization;
    }

    // ---------------------------------------------- CATEGORIZATION INSERT ------------------------------------------------------

    @Override
    public Optional<Categorization> findCategorization(long postId, long tagId) {
        LOGGER.debug("Selecting Category with Post Id {} and Tag Id {}", postId, tagId);

        return em.createQuery("SELECT c FROM Categorization c WHERE c.post.postId = :postId AND c.tag.tagId = :tagId", Categorization.class)
                .setParameter("postId", postId)
                .setParameter("tagId", tagId)
                .getResultList().stream().findFirst();
    }

    // ---------------------------------------------- CATEGORIZATION DELETE ------------------------------------------------------

    @Override
    public boolean deleteCategorization(long postId, long tagId) {
        LOGGER.debug("Deleting Category with Post Id {} and Tag Id {}", postId, tagId);

        int deleteCount = em.createQuery("DELETE FROM Categorization c WHERE c.post.postId = :postId AND c.tag.tagId = :tagId")
                .setParameter("postId", postId)
                .setParameter("tagId", tagId)
                .executeUpdate();

        return deleteCount > 0;
    }
}

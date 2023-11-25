package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.models.JunctionEntities.Categorization;
import ar.edu.itba.paw.models.MainEntities.Post;
import ar.edu.itba.paw.models.MainEntities.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CategorizationDaoImpl implements CategorizationDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategorizationDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // ---------------------------------------------- TAGS INSERT ------------------------------------------------------

    @Override
    public Categorization createCategorization(final long tagId, final long postId) {
        LOGGER.debug("Inserting Category");
        Categorization categorization = new Categorization(em.find(Post.class, postId), em.find(Tag.class, tagId));
        em.persist(categorization);
        return categorization;
    }
}

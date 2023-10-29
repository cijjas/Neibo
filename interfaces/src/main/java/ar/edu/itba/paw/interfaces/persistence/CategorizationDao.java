package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.JunctionEntities.Categorization;

public interface CategorizationDao {

    // -------------------------------------------- POSTS_TAGS INSERT --------------------------------------------------

    Categorization createCategorization(long tagId, long postId);
}

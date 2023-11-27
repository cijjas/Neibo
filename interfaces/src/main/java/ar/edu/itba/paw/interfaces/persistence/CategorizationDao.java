package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Categorization;

public interface CategorizationDao {

    // -------------------------------------------- POSTS TAGS INSERT --------------------------------------------------

    Categorization createCategorization(long tagId, long postId);
}

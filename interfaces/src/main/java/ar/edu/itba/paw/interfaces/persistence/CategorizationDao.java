package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Categorization;

import java.util.Optional;

public interface CategorizationDao {

    // -------------------------------------------- POSTS TAGS INSERT --------------------------------------------------

    Categorization createCategorization(long postId, long tagId);

    // -------------------------------------------- POSTS TAGS SELECT --------------------------------------------------

    Optional<Categorization> findCategorization(long postId, long tagId);

    // -------------------------------------------- POSTS TAGS DELETE --------------------------------------------------

    boolean deleteCategorization(long postId, long tagId);
}

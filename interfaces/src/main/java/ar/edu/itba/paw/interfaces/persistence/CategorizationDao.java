package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Categorization;

import java.util.Optional;

public interface CategorizationDao {

    // -------------------------------------------- POSTS TAGS INSERT --------------------------------------------------

    Categorization createCategorization(long tagId, long postId);

    // -------------------------------------------- POSTS TAGS SELECT --------------------------------------------------

    Optional<Categorization> findCategorization(long tagId, long postId);

    // -------------------------------------------- POSTS TAGS DELETE --------------------------------------------------

    boolean deleteCategorization(long tagId, long postId);
}

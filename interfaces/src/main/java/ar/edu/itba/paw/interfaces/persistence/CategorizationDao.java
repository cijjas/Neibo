package ar.edu.itba.paw.interfaces.persistence;

public interface CategorizationDao {

    // -------------------------------------------- POSTS_TAGS INSERT --------------------------------------------------

    void createCategory(long tagId, long postId);
}

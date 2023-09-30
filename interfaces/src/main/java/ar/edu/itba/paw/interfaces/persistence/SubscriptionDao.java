package ar.edu.itba.paw.interfaces.persistence;

public interface SubscriptionDao {

    // ---------------------------------------------- POST_USERS INSERT ------------------------------------------------

    void createSubscription(long neighborId, long postId);
}

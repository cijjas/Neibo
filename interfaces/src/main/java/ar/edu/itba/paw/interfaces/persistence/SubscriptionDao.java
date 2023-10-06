package ar.edu.itba.paw.interfaces.persistence;

public interface SubscriptionDao {

    // ---------------------------------------------- POST_USERS_SUBSCRIPTIONS INSERT ------------------------------------------------

    void createSubscription(long neighborId, long postId);
}

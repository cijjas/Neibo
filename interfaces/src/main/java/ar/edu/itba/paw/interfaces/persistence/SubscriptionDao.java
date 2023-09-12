package ar.edu.itba.paw.interfaces.persistence;

public interface SubscriptionDao {
    void createSubscription(long neighborId, long postId);
}

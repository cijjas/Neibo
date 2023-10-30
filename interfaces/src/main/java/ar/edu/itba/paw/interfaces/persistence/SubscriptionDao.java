package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.JunctionEntities.Subscription;

public interface SubscriptionDao {

    // ---------------------------------------------- POST_USERS_SUBSCRIPTIONS INSERT ------------------------------------------------

    Subscription createSubscription(long neighborId, long postId);
}

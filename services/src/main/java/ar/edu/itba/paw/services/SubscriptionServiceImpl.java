package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.SubscriptionDao;
import ar.edu.itba.paw.interfaces.services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionDao subscriptionDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    @Autowired
    public SubscriptionServiceImpl(final SubscriptionDao subscriptionDao) {
        this.subscriptionDao = subscriptionDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void createSubscription(long neighborId, long postId) {
        LOGGER.info("Creating Subscription for Post {} from User {}", postId, neighborId);
        subscriptionDao.createSubscription(neighborId, postId);
    }
}

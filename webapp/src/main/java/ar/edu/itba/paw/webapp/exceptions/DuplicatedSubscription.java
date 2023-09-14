package ar.edu.itba.paw.webapp.exceptions;

public class DuplicatedSubscription extends NeiboException{
    public DuplicatedSubscription() {
        super("Duplicated Subscription, you are already subscribed");
    }

    public DuplicatedSubscription(String message) {
        super(message);
    }
}

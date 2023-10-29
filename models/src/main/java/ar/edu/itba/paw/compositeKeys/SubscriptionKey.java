package ar.edu.itba.paw.compositeKeys;

import java.io.Serializable;
import java.util.Objects;

public class SubscriptionKey implements Serializable {
    private Long postId;
    private Long userId;

    public SubscriptionKey() {
    }

    public SubscriptionKey(Long userId, Long eventId) {
        this.postId = userId;
        this.userId = eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionKey that = (SubscriptionKey) o;
        return postId.equals(that.postId) && userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, userId);
    }
}

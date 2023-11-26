package ar.edu.itba.paw.models.compositeKeys;

import java.io.Serializable;
import java.util.Objects;

public class CategorizationKey implements Serializable {
    private Long postId;
    private Long tagId;

    public CategorizationKey() {
    }

    public CategorizationKey(Long userId, Long eventId) {
        this.postId = userId;
        this.tagId = eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategorizationKey)) return false;
        CategorizationKey that = (CategorizationKey) o;
        return Objects.equals(postId, that.postId) && Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, tagId);
    }
}

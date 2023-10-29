package ar.edu.itba.paw.compositeKeys;

import java.io.Serializable;
import java.util.Objects;

public class LikeKey implements Serializable {
    private Long postId;
    private Long userId;

    public LikeKey() {
    }

    public LikeKey(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeKey that = (LikeKey) o;
        return postId.equals(that.postId) && userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, userId);
    }
}

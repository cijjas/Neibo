package ar.edu.itba.paw.models.compositeKeys;

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
        if (!(o instanceof LikeKey)) return false;
        LikeKey likeKey = (LikeKey) o;
        return Objects.equals(postId, likeKey.postId) && Objects.equals(userId, likeKey.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, userId);
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

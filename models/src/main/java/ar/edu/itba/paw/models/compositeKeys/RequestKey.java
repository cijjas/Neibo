package ar.edu.itba.paw.models.compositeKeys;

import java.io.Serializable;
import java.util.Objects;

public class RequestKey implements Serializable {
    private Long productId;
    private Long userId;

    public RequestKey() {
    }

    public RequestKey(Long productId, Long userId) {
        this.productId = productId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestKey)) return false;
        RequestKey that = (RequestKey) o;
        return Objects.equals(productId, that.productId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, userId);
    }
}

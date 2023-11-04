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
        if (o == null || getClass() != o.getClass()) return false;
        RequestKey that = (RequestKey) o;
        return productId.equals(that.productId) && userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, userId);
    }
}

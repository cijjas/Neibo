package ar.edu.itba.paw.models.compositeKeys;

import java.io.Serializable;
import java.util.Objects;

public class InquiryKey implements Serializable {
    private Long productId;
    private Long userId;

    public InquiryKey() {
    }

    public InquiryKey(Long productId, Long userId) {
        this.productId = productId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InquiryKey)) return false;
        InquiryKey that = (InquiryKey) o;
        return Objects.equals(productId, that.productId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, userId);
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

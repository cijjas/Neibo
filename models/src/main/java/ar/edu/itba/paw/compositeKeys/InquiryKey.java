package ar.edu.itba.paw.compositeKeys;

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
        if (o == null || getClass() != o.getClass()) return false;
        InquiryKey that = (InquiryKey) o;
        return productId.equals(that.productId) && userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, userId);
    }
}

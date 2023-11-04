package ar.edu.itba.paw.models.compositeKeys;

import java.io.Serializable;
import java.util.Objects;

public class ClassificationKey implements Serializable {
    private Long productId;
    private Long tagId;

    public ClassificationKey() {
    }

    public ClassificationKey(Long productId, Long tagId) {
        this.productId = productId;
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassificationKey that = (ClassificationKey) o;
        return productId.equals(that.productId) && tagId.equals(that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, tagId);
    }
}

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
        if (!(o instanceof ClassificationKey)) return false;
        ClassificationKey that = (ClassificationKey) o;
        return Objects.equals(productId, that.productId) && Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, tagId);
    }
}

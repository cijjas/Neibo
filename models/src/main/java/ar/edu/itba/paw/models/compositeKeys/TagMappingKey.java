package ar.edu.itba.paw.models.compositeKeys;

import java.io.Serializable;
import java.util.Objects;

public class TagMappingKey implements Serializable {

    private Long neighborhoodId;
    private Long tagId;

    public TagMappingKey() {
    }

    public TagMappingKey(Long neighborhoodId, Long tagId) {
        this.neighborhoodId = neighborhoodId;
        this.tagId = tagId;
    }

    @Override
    public String toString() {
        return "TagMappingKey{" +
                "neighborhoodId=" + neighborhoodId +
                ", tagId=" + tagId +
                '}';
    }

    public Long getNeighborhoodId() {
        return neighborhoodId;
    }

    public void setNeighborhoodId(Long neighborhoodId) {
        this.neighborhoodId = neighborhoodId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagMappingKey)) return false;
        TagMappingKey that = (TagMappingKey) o;
        return Objects.equals(neighborhoodId, that.neighborhoodId) && Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(neighborhoodId, tagId);
    }
}

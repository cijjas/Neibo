package ar.edu.itba.paw.models.Entities;

import ar.edu.itba.paw.models.compositeKeys.TagMappingKey;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "neighborhoods_tags")
public class TagMapping {
    @EmbeddedId
    private TagMappingKey id;

    @ManyToOne
    @MapsId("neighborhoodId")
    @JoinColumn(name = "neighborhoodid")
    private Neighborhood neighborhood;

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tagid")
    private Tag tag;

    TagMapping() {
        this.id = new TagMappingKey();
    }

    public TagMapping(Neighborhood neighborhood, Tag tag) {
        this.id = new TagMappingKey(neighborhood.getNeighborhoodId(), tag.getTagId());
        this.neighborhood = neighborhood;
        this.tag = tag;
    }

    public TagMappingKey getId() {
        return id;
    }

    public void setId(TagMappingKey id) {
        this.id = id;
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(Neighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagMapping)) return false;
        TagMapping that = (TagMapping) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

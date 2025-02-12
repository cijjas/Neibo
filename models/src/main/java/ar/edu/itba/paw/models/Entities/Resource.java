package ar.edu.itba.paw.models.Entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "resources")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resources_resourceid_seq")
    @SequenceGenerator(sequenceName = "resources_resourceid_seq", name = "resources_resourceid_seq", allocationSize = 1)
    @Column(name = "resourceid")
    private Long resourceId;

    @Column(name = "resourcetitle", length = 64)
    private String title;

    @Column(name = "resourcedescription", length = 255)
    private String description;

    @ManyToOne
    @JoinColumn(name = "resourceimageid")
    private Image image;

    @ManyToOne
    @JoinColumn(name = "neighborhoodid", nullable = false)
    private Neighborhood neighborhood;

    Resource() {
    }

    private Resource(Builder builder) {
        this.resourceId = builder.resourceId;
        this.title = builder.title;
        this.description = builder.description;
        this.image = builder.image;
        this.neighborhood = builder.neighborhood;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(Neighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;
        Resource resource = (Resource) o;
        return Objects.equals(resourceId, resource.resourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(resourceId);
    }

    public static class Builder {
        private Long resourceId;
        private String title;
        private String description;
        private Image image;
        private Neighborhood neighborhood;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder image(Image image) {
            this.image = image;
            return this;
        }

        public Builder neighborhood(Neighborhood neighborhood) {
            this.neighborhood = neighborhood;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Builder)) return false;
            Builder builder = (Builder) o;
            return Objects.equals(resourceId, builder.resourceId) && Objects.equals(title, builder.title) && Objects.equals(description, builder.description) && Objects.equals(image, builder.image) && Objects.equals(neighborhood, builder.neighborhood);
        }

        @Override
        public int hashCode() {
            return Objects.hash(resourceId, title, description, image, neighborhood);
        }

        public Builder resourceId(Long resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public Resource build() {
            return new Resource(this);
        }
    }
}

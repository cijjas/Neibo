package ar.edu.itba.paw.models.MainEntities;

public class Resource {
    private final Long resourceId;
    private final String description;
    private final Long imageId;
    private final String title;
    private final Long neighborhoodId;

    private Resource(Builder builder) {
        this.resourceId = builder.resourceId;
        this.description = builder.description;
        this.imageId = builder.imageId;
        this.title = builder.title;
        this.neighborhoodId = builder.neighborhoodId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public String getDescription() {
        return description;
    }

    public Long getImageId() {
        return imageId;
    }

    public String getTitle() {
        return title;
    }

    public Long getNeighborhoodId() {
        return neighborhoodId;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "resourceId=" + resourceId +
                "description='" + description + '\'' +
                ", image='" + imageId + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public static class Builder {
        private Long resourceId;
        private String description;
        private Long imageId;
        private String title;
        private Long neighborhoodId;

        public Builder resourceId(Long resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder imageId(Long imageId) {
            this.imageId = imageId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder neighborhoodId(Long neighborhoodId) {
            this.neighborhoodId = neighborhoodId;
            return this;
        }

        public Resource build() {
            return new Resource(this);
        }
    }
}


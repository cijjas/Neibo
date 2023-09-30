package ar.edu.itba.paw.models;

public class Resource {
    private final String description;
    private final long imageId;
    private final String title;
    private final long neighborhoodId;

    private Resource(Builder builder) {
        this.description = builder.description;
        this.imageId = builder.imageId;
        this.title = builder.title;
        this.neighborhoodId = builder.neighborhoodId;
    }

    public static class Builder {
        private String description;
        private long imageId;
        private String title;
        private long neighborhoodId;

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder imageId(long imageId) {
            this.imageId = imageId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder neighborhoodId(long neighborhoodId) {
            this.neighborhoodId = neighborhoodId;
            return this;
        }

        public Resource build() {
            return new Resource(this);
        }
    }

    public String getDescription() {
        return description;
    }

    public long getImageId() {
        return imageId;
    }

    public String getTitle() {
        return title;
    }

    public long getNeighborhoodId() {
        return neighborhoodId;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "description='" + description + '\'' +
                ", image='" + imageId + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}


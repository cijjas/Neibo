package ar.edu.itba.paw.models;

public class Amenity {
    private final long amenityId;
    private final String name;
    private final String description;

    private Amenity(Builder builder) {
        this.amenityId = builder.amenityId;
        this.name = builder.name;
        this.description = builder.description;
    }

    public static class Builder {
        private long amenityId;
        private String name;
        private String description;

        public Builder amenityId(long amenityId) {
            this.amenityId = amenityId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Amenity build() {
            return new Amenity(this);
        }
    }

    public long getAmenityId() {
        return amenityId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Amenity{" +
                "amenityId=" + amenityId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

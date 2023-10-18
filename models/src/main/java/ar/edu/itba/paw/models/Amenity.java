package ar.edu.itba.paw.models;

import java.util.List;

public class Amenity {
    private final long amenityId;
    private final String name;
    private final String description;
    private final long neighborhoodId;
    private final List<Shift> availableShifts;

    private Amenity(Builder builder) {
        this.amenityId = builder.amenityId;
        this.name = builder.name;
        this.description = builder.description;
        this.neighborhoodId = builder.neighborhoodId;
        this.availableShifts = builder.availableShifts;
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

    public long getNeighborhoodId() {
        return neighborhoodId;
    }

    public List<Shift> getAvailableShifts() {
        return availableShifts;
    }

    @Override
    public String toString() {
        return "Amenity{" +
                "amenityId=" + amenityId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", availableShifts=" + availableShifts +
                '}';
    }

    public static class Builder {
        private long amenityId;
        private String name;
        private String description;
        private long neighborhoodId;
        private List<Shift> availableShifts;

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

        public Builder neighborhoodId(long neighborhoodId) {
            this.neighborhoodId = neighborhoodId;
            return this;
        }

        public Builder availableShifts(List<Shift> availableShifts) {
            this.availableShifts = availableShifts;
            return this;
        }

        public Amenity build() {
            return new Amenity(this);
        }
    }
}

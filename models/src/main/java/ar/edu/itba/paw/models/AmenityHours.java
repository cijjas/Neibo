package ar.edu.itba.paw.models;

import java.sql.Time;
import java.util.Map;

public class AmenityHours {
    private final Amenity amenity;
    private final Map<String, DayTime> amenityHours;

    private AmenityHours(Builder builder) {
        this.amenity = builder.amenity;
        this.amenityHours = builder.amenityHours;
    }

    public static class Builder {
        private Amenity amenity;
        private Map<String, DayTime> amenityHours;

        public Builder amenity(Amenity amenity) {
            this.amenity = amenity;
            return this;
        }

        public Builder amenityHours(Map<String, DayTime> amenityHours) {
            this.amenityHours = amenityHours;
            return this;
        }

        public AmenityHours build() {
            return new AmenityHours(this);
        }
    }

    public Amenity getAmenity() {
        return amenity;
    }

    public Map<String, DayTime> getAmenityHours() {
        return amenityHours;
    }

    @Override
    public String toString() {
        return "Amenity{" +
                "amenity=" + amenity +
                ", hours='" + amenityHours + '\'' +
                '}';
    }
}

package ar.edu.itba.paw.models;

import java.sql.Time;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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
        List<String> customOrder = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        // Sort the map keys based on the custom order
        Map<String, DayTime> sortedMap = new LinkedHashMap<>();
        for (String day : customOrder) {
            if (amenityHours.containsKey(day)) {
                System.out.println("Adding day: " + day + " to the sorted map" +amenityHours.get(day)   );
                sortedMap.put(day, amenityHours.get(day));
            }
        }

        return sortedMap;
    }

    @Override
    public String toString() {
        return "Amenity{" +
                "amenity=" + amenity +
                ", hours='" + amenityHours + '\'' +
                '}';
    }
}

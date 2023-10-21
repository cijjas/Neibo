package ar.edu.itba.paw.models;

import java.util.List;

public class Availability {
    private Long availabilityId;
    private Amenity amenity;
    private List<Shift> schedule;

    private Availability(Builder builder) {
        this.availabilityId = builder.availabilityId;
        this.amenity = builder.amenity;
        this.schedule = builder.schedule;
    }

    public Long getAvailabilityId() { // Added availabilityId getter
        return availabilityId;
    }

    public Amenity getAmenity() {
        return amenity;
    }

    public List<Shift> getSchedule() {
        return schedule;
    }

    @Override
    public String toString() {
        return "Availability{" +
                "availabilityId=" + availabilityId +
                ", amenity=" + amenity +
                ", schedule=" + schedule +
                '}';
    }

    public static class Builder {
        private Long availabilityId;
        private Amenity amenity;
        private List<Shift> schedule;

        public Builder availabilityId(Long availabilityId) {
            this.availabilityId = availabilityId;
            return this;
        }

        public Builder amenity(Amenity amenity) {
            this.amenity = amenity;
            return this;
        }

        public Builder schedule(List<Shift> schedule) {
            this.schedule = schedule;
            return this;
        }

        public Availability build() {
            return new Availability(this);
        }
    }
}

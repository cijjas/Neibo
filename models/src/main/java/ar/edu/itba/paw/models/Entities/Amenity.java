package ar.edu.itba.paw.models.Entities;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "amenities")
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "amenities_amenityid_seq")
    @SequenceGenerator(sequenceName = "amenities_amenityid_seq", name = "amenities_amenityid_seq", allocationSize = 1)
    private Long amenityId;

    @Column(name = "name", length = 512, unique = true, nullable = false)
    private String name;

    @Column(name = "description", length = 512, unique = true, nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "neighborhoodid")
    private Neighborhood neighborhood;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Amenity)) return false;
        Amenity amenity = (Amenity) o;
        return Objects.equals(amenityId, amenity.amenityId) && Objects.equals(name, amenity.name) && Objects.equals(description, amenity.description) && Objects.equals(neighborhood, amenity.neighborhood);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amenityId, name, description, neighborhood);
    }

    @ManyToMany
    @JoinTable(name = "amenities_shifts_availability",
            joinColumns = @JoinColumn(name = "amenityid"),
            inverseJoinColumns = @JoinColumn(name = "shiftid"))
    private List<Shift> availableShifts;

    Amenity() {
    }

    private Amenity(Builder builder) {
        this.amenityId = builder.amenityId;
        this.name = builder.name;
        this.description = builder.description;
        this.neighborhood = builder.neighborhood;
        this.availableShifts = builder.availableShifts;
    }

    public Long getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(Long amenityId) {
        this.amenityId = amenityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(Neighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }

    public List<Shift> getAvailableShifts() {
        return availableShifts;
    }

    public void setAvailableShifts(List<Shift> availableShifts) {
        this.availableShifts = availableShifts;
    }

    public static class Builder {
        private Long amenityId;
        private String name;
        private String description;
        private Neighborhood neighborhood;
        private List<Shift> availableShifts;

        public Builder amenityId(Long amenityId) {
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

        public Builder neighborhood(Neighborhood neighborhood) {
            this.neighborhood = neighborhood;
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

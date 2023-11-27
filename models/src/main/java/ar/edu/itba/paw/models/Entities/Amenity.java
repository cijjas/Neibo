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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "neighborhoodid")
    private Neighborhood neighborhood;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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

    @Override
    public String toString() {
        return "Amenity{" +
                "amenityId=" + amenityId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", neighborhood=" + neighborhood +
                ", availableShifts=" + availableShifts +
                '}';
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Builder)) return false;
            Builder builder = (Builder) o;
            return Objects.equals(amenityId, builder.amenityId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(amenityId);
        }
    }

}

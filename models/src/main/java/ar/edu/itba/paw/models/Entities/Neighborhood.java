package ar.edu.itba.paw.models.Entities;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "neighborhoods")
public class Neighborhood {
    @OneToMany(mappedBy = "neighborhood")  // mappedBy refers to the field in the User entity
    private final Set<User> users = new HashSet<>();
    @OneToMany(mappedBy = "neighborhood")
    private final Set<Contact> contacts = new HashSet<>();
    @OneToMany(mappedBy = "neighborhood")
    private final Set<Event> events = new HashSet<>();
    @OneToMany(mappedBy = "neighborhood")
    private final Set<Resource> resources = new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "neighborhoods_neighborhoodid_seq")
    @SequenceGenerator(sequenceName = "neighborhoods_neighborhoodid_seq", name = "neighborhoods_neighborhoodid_seq", allocationSize = 1)
    private Long neighborhoodId;
    @Column(name = "neighborhoodname", length = 128, unique = true, nullable = false)
    private String name;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "neighborhoods_channels",
            joinColumns = @JoinColumn(name = "neighborhoodid"),
            inverseJoinColumns = @JoinColumn(name = "channelid"))
    private Set<Neighborhood> channels;
    @ManyToMany
    @JoinTable(name = "workers_neighborhoods", joinColumns = @JoinColumn(name = "neighborhoodid"), inverseJoinColumns = @JoinColumn(name = "workerid"))
    private Set<Worker> workers;

    @Version
    @ColumnDefault("1")
    private Long version;

    public Long getVersion() {
        return version;
    }

    Neighborhood() {
    }

    private Neighborhood(Builder builder) {
        this.neighborhoodId = builder.neighborhoodId;
        this.name = builder.name;
    }

    public Long getNeighborhoodId() {
        return neighborhoodId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Neighborhood{" +
                "neighborhoodId=" + neighborhoodId +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Neighborhood)) return false;
        Neighborhood that = (Neighborhood) o;
        return Objects.equals(neighborhoodId, that.neighborhoodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(neighborhoodId);
    }

    public static class Builder {
        private Long neighborhoodId;
        private String name;

        public Builder neighborhoodId(Long neighborhoodId) {
            this.neighborhoodId = neighborhoodId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Neighborhood build() {
            return new Neighborhood(this);
        }
    }
}

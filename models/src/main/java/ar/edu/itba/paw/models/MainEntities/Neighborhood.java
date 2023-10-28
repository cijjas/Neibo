package ar.edu.itba.paw.models.MainEntities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "neighborhoods")
public class Neighborhood {
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

    @OneToMany(mappedBy = "neighborhood")  // mappedBy refers to the field in the User entity
    private Set<User> users = new HashSet<>();

    public Neighborhood(){}

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

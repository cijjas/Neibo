package ar.edu.itba.paw.models.Entities;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "professions")
public class Profession {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "professions_professionid_seq")
    @SequenceGenerator(sequenceName = "professions_professionid_seq", name = "professions_professionid_seq", allocationSize = 1)
    private Long professionId;

    @Column(name = "profession", length = 64, unique = true, nullable = false)
    private String profession;

    @ManyToMany
    @JoinTable(name = "workers_professions", joinColumns = @JoinColumn(name = "professionid"), inverseJoinColumns = @JoinColumn(name = "workerid"))
    private Set<Worker> workers;

    Profession() {
    }

    private Profession(Builder builder) {
        this.professionId = builder.professionId;
        this.profession = builder.profession;
    }

    public Long getProfessionId() {
        return professionId;
    }

    public String getProfession() {
        return profession;
    }

    public Set<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(Set<Worker> workers) {
        this.workers = workers;
    }

    @Override
    public String toString() {
        return "Profession{" +
                "professionId=" + professionId +
                ", profession='" + profession + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profession)) return false;
        Profession that = (Profession) o;
        return Objects.equals(professionId, that.professionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(professionId);
    }

    public static class Builder {
        private Long professionId;
        private String profession;

        public Builder professionId(Long professionId) {
            this.professionId = professionId;
            return this;
        }

        public Builder profession(String profession) {
            this.profession = profession;
            return this;
        }

        public Profession build() {
            return new Profession(this);
        }
    }
}

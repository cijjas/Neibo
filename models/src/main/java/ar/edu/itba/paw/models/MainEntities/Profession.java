package ar.edu.itba.paw.models.MainEntities;

import ar.edu.itba.paw.enums.Professions;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "professions")
public class Profession {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "professions_professionid_seq")
    @SequenceGenerator(sequenceName = "professions_professionid_seq", name = "professions_professionid_seq", allocationSize = 1)
    private Long professionId;

    @Column(length = 64)
    @Enumerated(EnumType.STRING)
    private Professions profession;

    @ManyToMany
    @JoinTable(name = "workers_professions", joinColumns = @JoinColumn(name = "professionid"), inverseJoinColumns = @JoinColumn(name = "workerid"))
    private Set<Worker> workers;

    public Profession(){}

    private Profession(Builder builder) {
        this.professionId = builder.professionId;
        this.profession = builder.profession;
    }

    public Long getProfessionId() {
        return professionId;
    }

    public Professions getProfession() {
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

    public static class Builder {
        private Long professionId;
        private Professions profession;

        public Builder professionId(Long professionId) {
            this.professionId = professionId;
            return this;
        }

        public Builder profession(Professions profession) {
            this.profession = profession;
            return this;
        }

        public Profession build() {
            return new Profession(this);
        }
    }
}

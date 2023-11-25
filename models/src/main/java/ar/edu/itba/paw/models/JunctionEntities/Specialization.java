package ar.edu.itba.paw.models.JunctionEntities;

import ar.edu.itba.paw.models.MainEntities.Profession;
import ar.edu.itba.paw.models.MainEntities.Worker;
import ar.edu.itba.paw.models.compositeKeys.SpecializationKey;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "workers_professions")
public class Specialization implements Serializable {
    @EmbeddedId
    private SpecializationKey id;

    @ManyToOne
    @MapsId("workerId")
    @JoinColumn(name = "workerid")
    private Worker worker;

    @ManyToOne
    @MapsId("professionId")
    @JoinColumn(name = "professionid")
    private Profession profession;

    public Specialization() {
        this.id = new SpecializationKey();
    }

    public Specialization(Worker worker, Profession profession) {
        this.id = new SpecializationKey(worker.getUser().getUserId(), profession.getProfessionId());
        this.worker = worker;
        this.profession = profession;
    }

    public SpecializationKey getId() {
        return id;
    }

    public void setId(SpecializationKey id) {
        this.id = id;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Specialization)) return false;
        Specialization that = (Specialization) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

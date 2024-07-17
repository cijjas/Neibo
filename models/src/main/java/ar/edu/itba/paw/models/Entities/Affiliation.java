package ar.edu.itba.paw.models.Entities;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.models.compositeKeys.AffiliationKey;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "workers_neighborhoods")
public class Affiliation implements Serializable {
    @EmbeddedId
    private AffiliationKey id;

    @ManyToOne
    @MapsId("workerId")
    @JoinColumn(name = "workerid")
    private Worker worker;

    @ManyToOne
    @MapsId("neighborhoodId")
    @JoinColumn(name = "neighborhoodid")
    private Neighborhood neighborhood;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private WorkerRole role;

    Affiliation() {
        this.id = new AffiliationKey();
    }

    public Affiliation(Worker worker, Neighborhood neighborhood, WorkerRole workerRole) {
        this.id = new AffiliationKey(worker.getUser().getUserId(), neighborhood.getNeighborhoodId());
        this.worker = worker;
        this.neighborhood = neighborhood;
        this.role = workerRole;
    }

    public AffiliationKey getId() {
        return id;
    }

    public void setId(AffiliationKey id) {
        this.id = id;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public WorkerRole getRole() {
        return role;
    }

    public void setRole(WorkerRole role) {
        this.role = role;
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(Neighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Affiliation)) return false;
        Affiliation that = (Affiliation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

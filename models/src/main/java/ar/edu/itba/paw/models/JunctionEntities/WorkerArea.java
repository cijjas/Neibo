package ar.edu.itba.paw.models.JunctionEntities;

import ar.edu.itba.paw.compositeKeys.WorkerAreaKey;
import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.models.MainEntities.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "workers_neighborhoods")
public class WorkerArea implements Serializable {
    @EmbeddedId
    private WorkerAreaKey id;

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

    public WorkerArea() {
        this.id = new WorkerAreaKey();
    }

    public WorkerArea(Worker worker, Neighborhood neighborhood) {
        this.id = new WorkerAreaKey(worker.getUser().getUserId(), neighborhood.getNeighborhoodId());
        this.worker = worker;
        this.neighborhood = neighborhood;
    }

    public WorkerAreaKey getId() {
        return id;
    }

    public Worker getWorker() {
        return worker;
    }

    public WorkerRole getRole() { return role; }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public void setId(WorkerAreaKey id) {
        this.id = id;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public void setNeighborhood(Neighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }

    public void setRole(WorkerRole role) { this.role = role; }
}

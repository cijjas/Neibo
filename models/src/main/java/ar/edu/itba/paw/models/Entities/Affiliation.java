package ar.edu.itba.paw.models.Entities;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.models.compositeKeys.AffiliationKey;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
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

    @Column(name = "role", length = 255)
    @Enumerated(EnumType.STRING)
    private WorkerRole role;

    @Column(name = "requestDate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDate;

    Affiliation() {
        this.id = new AffiliationKey();
    }

    public Affiliation(Worker worker, Neighborhood neighborhood, WorkerRole workerRole, Date requestDate) {
        this.id = new AffiliationKey(worker.getUser().getUserId(), neighborhood.getNeighborhoodId());
        this.worker = worker;
        this.neighborhood = neighborhood;
        this.role = workerRole;
        this.requestDate = requestDate;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Affiliation)) return false;
        Affiliation that = (Affiliation) o;
        return Objects.equals(id, that.id) && Objects.equals(worker, that.worker) && Objects.equals(neighborhood, that.neighborhood) && role == that.role && Objects.equals(requestDate, that.requestDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, worker, neighborhood, role, requestDate);
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

}

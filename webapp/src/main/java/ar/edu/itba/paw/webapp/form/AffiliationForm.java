package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.enums.WorkerStatus;
import ar.edu.itba.paw.webapp.form.validation.constraints.NeighborhoodURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.WorkerURNConstraint;

import java.util.List;

public class AffiliationForm {
    @WorkerURNConstraint
    private String workerURN;

    @NeighborhoodURNConstraint
    private String neighborhoodURN;

    private String workerStatus;

    public String getWorkerStatus() {
        return workerStatus;
    }

    public void setWorkerStatus(String workerStatus) {
        this.workerStatus = workerStatus;
    }

    public String getNeighborhoodURN() {
        return neighborhoodURN;
    }

    public void setNeighborhoodURN(String neighborhoodURN) {
        this.neighborhoodURN = neighborhoodURN;
    }

    public String getWorkerURN() {
        return workerURN;
    }

    public void setWorkerURN(String workerURN) {
        this.workerURN = workerURN;
    }

    @Override
    public String toString() {
        return "NeighborhoodForm{" +
                ", neighborhoodId='" + neighborhoodURN + '\'' +
                '}';
    }
}
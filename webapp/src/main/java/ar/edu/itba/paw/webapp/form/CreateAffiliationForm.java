package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.enums.WorkerStatus;
import ar.edu.itba.paw.webapp.form.validation.constraints.NeighborhoodURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.WorkerRoleURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.WorkerURNConstraint;

import java.util.List;

public class CreateAffiliationForm {
    @WorkerURNConstraint
    private String workerURN;

    @NeighborhoodURNConstraint
    private String neighborhoodURN;

    @WorkerRoleURNConstraint
    private String workerRoleURN;

    public String getWorkerRoleURN() {
        return workerRoleURN;
    }

    public void setWorkerRoleURN(String workerRoleURN) {
        this.workerRoleURN = workerRoleURN;
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
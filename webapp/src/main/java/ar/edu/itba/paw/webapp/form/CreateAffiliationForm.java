package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.NeighborhoodURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.WorkerRoleURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.WorkerURNInAffiliationFormConstraint;

public class CreateAffiliationForm {
    @WorkerURNInAffiliationFormConstraint
    private String worker;

    @NeighborhoodURNConstraint
    private String neighborhood;

    @WorkerRoleURNConstraint
    private String workerRole;

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getWorkerRole() {
        return workerRole;
    }

    public void setWorkerRole(String workerRole) {
        this.workerRole = workerRole;
    }

    @Override
    public String toString() {
        return "CreateAffiliationForm{" +
                "worker='" + worker + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                ", workerRole='" + workerRole + '\'' +
                '}';
    }
}
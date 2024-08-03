package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.WorkerRoleURNConstraint;

public class AffiliationForm {
    @WorkerRoleURNConstraint
    private String workerRole;

    public String getWorkerRole() {
        return workerRole;
    }

    public void setWorkerRole(String workerRole) {
        this.workerRole = workerRole;
    }

    @Override
    public String toString() {
        return "AffiliationForm{" +
                "workerRole='" + workerRole + '\'' +
                '}';
    }
}
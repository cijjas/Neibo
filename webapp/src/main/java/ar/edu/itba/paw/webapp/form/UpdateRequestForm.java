package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.RequestStatusURNConstraint;

public class UpdateRequestForm {
    @RequestStatusURNConstraint
    private String requestStatus;

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Override
    public String toString() {
        return "UpdateRequestForm{" +
                "requestStatus='" + requestStatus + '\'' +
                '}';
    }
}

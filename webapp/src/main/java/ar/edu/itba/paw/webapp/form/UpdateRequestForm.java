package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.RequestStatusURNConstraint;

public class UpdateRequestForm {
    @RequestStatusURNConstraint
    private String requestStatusURN;

    public String getRequestStatusURN() {
        return requestStatusURN;
    }

    public void setRequestStatusURN(String requestStatusURN) {
        this.requestStatusURN = requestStatusURN;
    }

    @Override
    public String toString() {
        return "UpdateRequestForm{" +
                "requestStatusURN='" + requestStatusURN + '\'' +
                '}';
    }
}

package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.UserURNAuthorizationConstraint;

public class AttendanceForm {
    @UserURNAuthorizationConstraint
    private String userURN;

    public String getUserURN() {
        return userURN;
    }

    public void setUserURN(String userURN) {
        this.userURN = userURN;
    }

    @Override
    public String toString() {
        return "AttendanceForm{" +
                "userURN='" + userURN + '\'' +
                '}';
    }
}

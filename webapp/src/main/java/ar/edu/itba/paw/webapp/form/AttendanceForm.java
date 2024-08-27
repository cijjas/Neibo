package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.UserURNReferenceConstraint;

public class AttendanceForm {
    @UserURNReferenceConstraint
    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "AttendanceForm{" +
                "user='" + user + '\'' +
                '}';
    }
}

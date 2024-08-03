package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.UserURNAuthorizationConstraint;

public class AttendanceForm {
    @UserURNAuthorizationConstraint
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

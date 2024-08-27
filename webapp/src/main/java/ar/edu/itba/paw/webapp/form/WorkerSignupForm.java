package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ProfessionsURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.UserURNConstraint;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Arrays;

public class WorkerSignupForm {
    @UserURNConstraint
    private String user;

    @ProfessionsURNConstraint
    private String[] professions;

    @NotNull
    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[0-9+\\- ]*")
    private String phoneNumber;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9 -]*")
    @Size(min = 1, max = 128)
    private String businessName;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9 -]*")
    @Size(min = 1, max = 128)
    private String address;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String[] getProfessions() {
        return professions;
    }

    public void setProfessions(String[] professions) {
        this.professions = professions;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "WorkerSignupForm{" +
                "user='" + user + '\'' +
                ", professions=" + Arrays.toString(professions) +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", businessName='" + businessName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

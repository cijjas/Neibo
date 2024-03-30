package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class WorkerSignupForm {
    @NotNull
    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[a-zA-Z ]*")
    private String worker_name;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z ]*")
    @Size(min = 1, max = 64)
    private String worker_surname;

    @ProfessionsURNConstraint
    private String[] professionURNs;

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

    @NotNull
    @Size(min = 1, max = 128)
    @Email
    @EmailConstraint
    private String worker_mail;

    @NotNull
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9]*")
    private String worker_password;

    @NotNull
    @Size(min = 1, max = 9)
    @Pattern(regexp = "^[0-9]*")
    private String worker_identification;

    @LanguageURNConstraint
    private String worker_languageURN;

    public String getWorker_name() {
        return worker_name;
    }

    public void setWorker_name(String worker_name) {
        this.worker_name = worker_name;
    }

    public String getWorker_surname() {
        return worker_surname;
    }

    public void setWorker_surname(String worker_surname) {
        this.worker_surname = worker_surname;
    }

    public String[] getProfessionURNs() {
        return professionURNs;
    }

    public void setProfessionURNs(String[] professionURNs) {
        this.professionURNs = professionURNs;
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

    public String getWorker_mail() {
        return worker_mail;
    }

    public void setWorker_mail(String worker_mail) {
        this.worker_mail = worker_mail;
    }

    public String getWorker_password() {
        return worker_password;
    }

    public void setWorker_password(String worker_password) {
        this.worker_password = worker_password;
    }

    public String getWorker_identification() {
        return worker_identification;
    }

    public void setWorker_identification(String worker_identification) {
        this.worker_identification = worker_identification;
    }

    public String getWorker_languageURN() {
        return worker_languageURN;
    }

    public void setWorker_languageURN(String worker_languageURN) {
        this.worker_languageURN = worker_languageURN;
    }

    @Override
    public String toString() {
        return "PublishForm{" +
                "name='" + worker_name + '\'' +
                ", surname='" + worker_surname + '\'' +
                ", password='" + worker_password + '\'' +
                ", mail='" + worker_mail + '\'' +
                ", identification='" + worker_identification + '\'' +
                ", language='" + worker_languageURN + '\'' +
                "phoneNumber='" + phoneNumber + '\'' +
                ", businessName='" + businessName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

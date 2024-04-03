package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.*;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class WorkerSignupForm {
    @NotNull
    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[a-zA-Z ]*")
    private String workerName;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z ]*")
    @Size(min = 1, max = 64)
    private String workerSurname;

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
    private String workerMail;

    @NotNull
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9]*")
    private String workerPassword;

    @NotNull
    @Size(min = 1, max = 9)
    @Pattern(regexp = "^[0-9]*")
    private String workerIdentification;

    @LanguageURNConstraint
    private String workerLanguageURN;

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getWorkerSurname() {
        return workerSurname;
    }

    public void setWorkerSurname(String workerSurname) {
        this.workerSurname = workerSurname;
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

    public String getWorkerMail() {
        return workerMail;
    }

    public void setWorkerMail(String workerMail) {
        this.workerMail = workerMail;
    }

    public String getWorkerPassword() {
        return workerPassword;
    }

    public void setWorkerPassword(String workerPassword) {
        this.workerPassword = workerPassword;
    }

    public String getWorkerIdentification() {
        return workerIdentification;
    }

    public void setWorkerIdentification(String workerIdentification) {
        this.workerIdentification = workerIdentification;
    }

    public String getWorkerLanguageURN() {
        return workerLanguageURN;
    }

    public void setWorkerLanguageURN(String workerLanguageURN) {
        this.workerLanguageURN = workerLanguageURN;
    }

    @Override
    public String toString() {
        return "PublishForm{" +
                "name='" + workerName + '\'' +
                ", surname='" + workerSurname + '\'' +
                ", password='" + workerPassword + '\'' +
                ", mail='" + workerMail + '\'' +
                ", identification='" + workerIdentification + '\'' +
                ", language='" + workerLanguageURN + '\'' +
                "phoneNumber='" + phoneNumber + '\'' +
                ", businessName='" + businessName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

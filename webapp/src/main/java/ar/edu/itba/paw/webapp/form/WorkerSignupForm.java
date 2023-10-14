package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class WorkerSignupForm {
    @NotBlank
    @Size(max = 64)
    @Pattern(regexp = "^[a-zA-Z ]+")
    private String w_name;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z ]+")
    @Size(max = 64)
    private String w_surname;

    private long[] professionIds;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String businessName;

    @NotBlank
    private String address;

    @NotBlank
    @Size(min = 6, max = 128)
    @Email
    private String w_mail;

    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String w_password;

    @NotBlank
    @Size(max = 9)
    @Pattern(regexp = "^[0-9]+$")
    private String w_identification;

    private String w_language;

    public String getW_identification() {
        return w_identification;
    }

    public void setW_identification(String identification) {
        this.w_identification = identification;
    }

    public String getW_language() {
        return w_language;
    }

    public void setW_language(String language) {
        this.w_language = language;
    }

    public String getW_name() {
        return w_name;
    }

    public void setW_name(String name) {
        this.w_name = name;
    }

    public String getW_surname() {
        return w_surname;
    }

    public void setW_surname(String surname) {
        this.w_surname = surname;
    }

    public String getW_mail() {
        return w_mail;
    }

    public void setW_mail(String mail) {
        this.w_mail = mail;
    }

    public String getW_password() {
        return w_password;
    }

    public void setW_password(String password) {
        this.w_password = password;
    }

    public long[] getProfessionIds() {
        return professionIds;
    }

    public void setProfessionIds(long[] professionIds) {
        this.professionIds = professionIds;
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
        return "PublishForm{" +
                "name='" + w_name + '\'' +
                ", surname='" + w_surname + '\'' +
                ", password='" + w_password + '\'' +
                ", mail='" + w_mail + '\'' +
                ", identification='" + w_identification + '\'' +
                ", language='" + w_language + '\'' +
                "phoneNumber='" + phoneNumber + '\'' +
                ", businessName='" + businessName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

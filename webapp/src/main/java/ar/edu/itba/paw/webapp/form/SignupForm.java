package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class SignupForm {
    @NotBlank
    @Size(max = 64)
    @Pattern(regexp = "^[a-zA-Z ]+")
    private String name;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z ]+")
    @Size(max = 64)
    private String surname;

    //will never be blank because its a dropdown (has a preselected value)
    private long neighborhoodId;

    @NotBlank
    @Size(min = 6, max = 128)
    @Email
    private String mail;

    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String password;

    @Max(2147483646)
    private Integer identification;

    private String language;

    public Integer getIdentification() {
        return identification;
    }

    public void setIdentification(int identification) {
        this.identification = identification;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public long getNeighborhoodId() {
        return neighborhoodId;
    }

    public void setNeighborhoodId(long neighborhoodId) {
        this.neighborhoodId = neighborhoodId;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "PublishForm{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", neighborhoodId='" + neighborhoodId + '\'' +
                ", password='" + password + '\'' +
                ", mail='" + mail + '\'' +
                '}';
    }
}

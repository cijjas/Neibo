package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.EmailConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.LanguageConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.NeighborhoodConstraint;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

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

    @NeighborhoodConstraint
    private long neighborhoodId;

    @NotBlank
    @Size(min = 6, max = 128)
    @Email
    @EmailConstraint
    private String mail;

    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String password;

    @NotBlank
    @Size(max = 9)
    @Pattern(regexp = "^[0-9]+$")
    private String identification;

    @LanguageConstraint
    private String language;

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
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

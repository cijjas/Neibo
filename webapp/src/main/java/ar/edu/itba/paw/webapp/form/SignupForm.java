package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class SignupForm {
    @NotNull
    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[a-zA-Z ]*")
    private String name;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z ]*")
    @Size(min = 1, max = 64)
    private String surname;

    @NotNull
    @NeighborhoodURNConstraint
    private String neighborhoodURN;

    @NotNull
    @Size(min = 1, max = 128)
    @Email
    @EmailConstraint
    private String mail;

    @NotNull
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9]*")
    private String password;

    @NotNull
    @Size(min = 1, max = 9)
    @Pattern(regexp = "^[0-9]*")
    private String identification;

    @NotNull
    @LanguageURNConstraint
    private String languageURN;

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
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

    public String getNeighborhoodURN() {
        return neighborhoodURN;
    }

    public void setNeighborhoodURN(String neighborhoodURN) {
        this.neighborhoodURN = neighborhoodURN;
    }

    public String getLanguageURN() {
        return languageURN;
    }

    public void setLanguageURN(String languageURN) {
        this.languageURN = languageURN;
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
                ", neighborhoodId='" + neighborhoodURN + '\'' +
                ", password='" + password + '\'' +
                ", mail='" + mail + '\'' +
                '}';
    }
}

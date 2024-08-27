package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.EmailConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.LanguageURNConstraint;
import org.hibernate.validator.constraints.Email;

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
    @Size(min = 1, max = 128)
    @Email
    @EmailConstraint
    private String email;

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
    private String language;

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "SignupForm{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", identification='" + identification + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}

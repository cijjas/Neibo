package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Required;

import javax.validation.constraints.Size;

// Para ver ejemplos de todos los constraints que se pueden poner, mirar javax.validation.constraints. Esto incluye
// @Min, @Max, @NotNull, @Size, @Pattern, y más. Además de los que se definen ahí (los de javax.validation son solo las
// definiciones, hibernate tiene las implementaciones) podemos ver los que suma hibernate suma otros validadores
// como @Email, @Length, @NotBlank, @NotEmpty, @URL, @SafeHtml, @Range, @CreditCardNumber...

public class PublishForm {
    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    @NotBlank
    @Size(min = 1, max = 100)
    private String surname;

    @NotBlank
    @Size(min = 1, max = 100)
    private String neighborhood;

    @NotBlank
    @Size(min = 1, max = 100)
    private String subject;

    @NotBlank
    @Size(min = 1, max = 255)
    private String message;

    @NotBlank
    @Size(min = 6, max = 100) // Especifico rango del length del string
    // @Pattern(regexp = "^[a-zA-Z0-9]+$") // Debe validar con tal regex, lo dejo como ejemplo pero mejor usar email
    @Email
    private String email;

    private String imageFile;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
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

    public String getNeighborhood() {
        return neighborhood;
    }
    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImage(String image) {
        this.imageFile = image;
    }

    @Override
    public String toString() {
        return "PublishForm{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                ", subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                ", email='" + email + '\'' +
                ", imageFile='" + imageFile + '\'' +
                '}';
    }
}

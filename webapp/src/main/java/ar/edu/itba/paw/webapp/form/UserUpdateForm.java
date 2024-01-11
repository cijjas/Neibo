package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Email;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

public class UserUpdateForm {

    @Size(min = 6, max = 100)

    @Email
    private String email;

    @Size(max = 64)
    private String name;

    @Size(max = 64)
    private String surname;

    @Size(min = 8, max = 128)
    private String password;

    private Integer languageId;

    private Integer userRoleId;

    private Boolean darkMode;

    private String phoneNumber;

    private MultipartFile profilePicture;

    private Integer identification;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName(){ return name; }

    public void setName(String name){ this.name = name; }

    public String getSurname(){ return surname; }

    public void setSurname(String surname){ this.surname = surname; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getDarkMode() { return darkMode; }

    public void setDarkMode(Boolean darkMode) { this.darkMode = darkMode; }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public MultipartFile getProfilePicture() { return profilePicture; }

    public void setProfilePicture(MultipartFile profilePicture) { this.profilePicture = profilePicture; }

    public Integer getIdentification() { return identification; }

    public void setIdentification(Integer identification) { this.identification = identification; }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public Integer getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(Integer userRoleId) {
        this.userRoleId = userRoleId;
    }

    @Override
    public String toString() {
        return "UserUpdateForm{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", password='" + password + '\'' +
                ", languageId=" + languageId +
                ", userRoleId=" + userRoleId +
                ", darkMode=" + darkMode +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profilePicture=" + profilePicture +
                ", identification=" + identification +
                '}';
    }
}

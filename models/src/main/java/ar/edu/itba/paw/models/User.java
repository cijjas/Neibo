package ar.edu.itba.paw.models;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;

import java.util.Date;
import java.util.Objects;

public class User {
    private final Long userId;
    private final String mail;
    private final String name;
    private final String surname;
    private final String password;
    private final Long neighborhoodId;
    private final boolean darkMode;
    private final Language language;
    private final UserRole role;
    private final Date creationDate;
    private final Long profilePictureId;
    private final int identification;

    private User(Builder builder) {
        this.userId = builder.userId;
        this.mail = builder.mail;
        this.name = builder.name;
        this.surname = builder.surname;
        this.password = builder.password;
        this.neighborhoodId = builder.neighborhoodId;
        this.darkMode = builder.darkMode;
        this.language = builder.language;
        this.role = builder.role;
        this.creationDate = builder.creationDate;
        this.profilePictureId = builder.profilePictureId;
        this.identification = builder.identification;
    }

    public Long getUserId() {
        return userId;
    }

    public String getMail() {
        return mail;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPassword() {
        return password;
    }

    public Long getNeighborhoodId() {
        return neighborhoodId;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public Language getLanguage() {
        return language;
    }

    public UserRole getRole() {
        return role;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Long getProfilePictureId() {
        return profilePictureId;
    }

    public int getIdentification() {
        return identification;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", mail='" + mail + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", password='" + password + '\'' +
                ", neighborhoodId=" + neighborhoodId +
                ", darkMode=" + darkMode +
                ", language=" + language +
                ", role=" + role +
                ", creationDate=" + creationDate +
                ", profilePictureId=" + profilePictureId +
                ", identification=" + identification +
                '}';
    }

    public static class Builder {
        private Long userId;
        private String mail;
        private String name;
        private String surname;
        private String password;
        private Long neighborhoodId;
        private boolean darkMode;
        private Language language;
        private UserRole role;
        private Date creationDate;
        private Long profilePictureId;
        private int identification;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder mail(String mail) {
            this.mail = mail;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder neighborhoodId(Long neighborhoodId) {
            this.neighborhoodId = neighborhoodId;
            return this;
        }

        public Builder darkMode(boolean darkMode) {
            this.darkMode = darkMode;
            return this;
        }

        public Builder language(Language language) {
            this.language = language;
            return this;
        }

        public Builder role(UserRole role) {
            this.role = role;
            return this;
        }

        public Builder creationDate(Date creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder profilePictureId(Long profilePictureId) {
            this.profilePictureId = profilePictureId;
            return this;
        }

        public Builder identification(int identification) {
            this.identification = identification;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User otherUser = (User) o;
        return userId == otherUser.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}

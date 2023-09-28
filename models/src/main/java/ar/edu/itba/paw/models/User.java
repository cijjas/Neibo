package ar.edu.itba.paw.models;

import enums.Language;
import enums.UserRole;
import java.util.Date;

public class User {
    private final long userId;
    private final String mail;
    private final String name;
    private final String surname;
    private final String password;
    private final long neighborhoodId;
    private final boolean darkMode;
    private final Language language;
    private final UserRole role;
    private final Date creationDate;

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
    }

    public static class Builder {
        private long userId;
        private String mail;
        private String name;
        private String surname;
        private String password;
        private long neighborhoodId;
        private boolean darkMode;
        private Language language;
        private UserRole role;
        private Date creationDate;

        public Builder userId(long userId) {
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

        public Builder password(String password){
            this.password = password;
            return this;
        }

        public Builder neighborhoodId(long neighborhoodId){
            this.neighborhoodId = neighborhoodId;
            return this;
        }

        public Builder darkMode(boolean darkMode){
            this.darkMode = darkMode;
            return this;
        }

        public Builder language(Language language){
            this.language = language;
            return this;
        }

        public Builder role(UserRole role){
            this.role = role;
            return this;
        }

        public Builder creationDate(Date creationDate){
            this.creationDate = creationDate;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    public long getUserId() {
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

    public String getPassword(){
        return password;
    }

    public long getNeighborhoodId(){
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
                '}';
    }
}

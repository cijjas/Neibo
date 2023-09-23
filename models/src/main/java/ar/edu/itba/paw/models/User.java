package ar.edu.itba.paw.models;

public class User {
    private final long userId;
    private final String mail;
    private final String name;
    private final String surname;
    private final String password;
    private final long neighborhoodId;
    private final boolean darkMode;
    private final boolean verified;
    private final String language;
    private final String role;

    private User(Builder builder) {
        this.userId = builder.userId;
        this.mail = builder.mail;
        this.name = builder.name;
        this.surname = builder.surname;
        this.password = builder.password;
        this.neighborhoodId = builder.neighborhoodId;
        this.darkMode = builder.darkMode;
        this.verified = builder.verified;
        this.language = builder.language;
        this.role = builder.role;
    }

    public static class Builder {
        private long userId;
        private String mail;
        private String name;
        private String surname;
        private String password;
        private long neighborhoodId;
        private boolean darkMode;
        private boolean verified;
        private String language;
        private String role;

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

        public Builder verified(boolean verified){
            this.verified = verified;
            return this;
        }

        public Builder role(String role){
            this.role = role;
            return this;
        }

        public Builder language(String language){
            this.language = language;
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

    public boolean isVerified() {
        return verified;
    }

    public String getRole() {
        return role;
    }

    public String getLanguage() {
        return language;
    }

    @Override
    public String toString() {
        return "Neighbor{" +
                "userId=" + userId +
                ", mail='" + mail + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", password='" + password + '\'' +
                ", neighborhoodId=" + neighborhoodId +
                ", darkMode=" + darkMode +
                ", verified=" + verified +
                ", language='" + language + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}

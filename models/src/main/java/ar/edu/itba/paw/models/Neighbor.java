package ar.edu.itba.paw.models;

public class Neighbor {
    private final long neighborId;
    private final String mail;
    private final String name;
    private final String surname;
    private final String password;
    private final long neighborhoodId;
    private final boolean darkMode;
    private final boolean verified;
    private final String language;

    private Neighbor(Builder builder) {
        this.neighborId = builder.neighborId;
        this.mail = builder.mail;
        this.name = builder.name;
        this.surname = builder.surname;
        this.password = builder.password;
        this.neighborhoodId = builder.neighborhoodId;
        this.darkMode = builder.darkMode;
        this.verified = builder.verified;
        this.language = builder.language;
    }

    public static class Builder {
        private long neighborId;
        private String mail;
        private String name;
        private String surname;
        private String password;
        private long neighborhoodId;
        private boolean darkMode;
        private boolean verified;
        private String language;

        public Builder neighborId(long neighborId) {
            this.neighborId = neighborId;
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

        public Builder language(String language){
            this.language = language;
            return this;
        }

        public Neighbor build() {
            return new Neighbor(this);
        }
    }

    public long getNeighborId() {
        return neighborId;
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

    public String getLanguage() {
        return language;
    }

    @Override
    public String toString() {
        return "Neighbor{" +
                "neighborId=" + neighborId +
                ", mail='" + mail + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", password='" + password + '\'' +
                ", neighborhoodId=" + neighborhoodId +
                ", darkMode=" + darkMode +
                ", verification=" + verified +
                ", language='" + language + '\'' +
                '}';
    }
}

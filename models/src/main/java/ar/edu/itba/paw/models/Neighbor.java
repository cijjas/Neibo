package ar.edu.itba.paw.models;

public class Neighbor {
    private final long neighborId;
    private final String mail;
    private final String name;
    private final String surname;
    private final Neighborhood neighborhood; // Represent neighborhood as an object

    // Private constructor to enforce object creation through the builder
    private Neighbor(Builder builder) {
        this.neighborId = builder.neighborId;
        this.mail = builder.mail;
        this.name = builder.name;
        this.surname = builder.surname;
        this.neighborhood = builder.neighborhood;
    }

    public static class Builder {
        private long neighborId;
        private String mail;
        private String name;
        private String surname;
        private Neighborhood neighborhood;

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

        public Builder neighborhood(Neighborhood neighborhood) {
            this.neighborhood = neighborhood;
            return this;
        }

        public Neighbor build() {
            return new Neighbor(this);
        }
    }

    // Getter methods for your fields (no setters as they are final)
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

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    @Override
    public String toString() {
        return "Neighbor{" +
                "neighborId=" + neighborId +
                ", mail='" + mail + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", neighborhood=" + neighborhood +
                '}';
    }
}

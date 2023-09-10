package ar.edu.itba.paw.models;

public class Neighbor {
    private final long neighborId;
    private final String mail;
    private final String name;
    private final String surname;

    // Private constructor to enforce object creation through the builder
    private Neighbor(Builder builder) {
        this.neighborId = builder.neighborId;
        this.mail = builder.mail;
        this.name = builder.name;
        this.surname = builder.surname;
    }

    public static class Builder {
        private long neighborId;
        private String mail;
        private String name;
        private String surname;

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

    @Override
    public String toString() {
        return "Neighbor{" +
                "neighborId=" + neighborId +
                ", mail='" + mail + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}

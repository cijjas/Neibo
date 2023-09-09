package ar.edu.itba.paw.models;

public class Neighborhood {
    private final long neighborhoodId;
    private final String name;

    private Neighborhood(Builder builder) {
        this.neighborhoodId = builder.neighborhoodId;
        this.name = builder.name;
    }

    public static class Builder {
        private long neighborhoodId;
        private String name;

        public Builder neighborhoodId(long neighborhoodId) {
            this.neighborhoodId = neighborhoodId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Neighborhood build() {
            return new Neighborhood(this);
        }
    }

    public long getNeighborhoodId() {
        return neighborhoodId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Neighborhood{" +
                "neighborhoodId=" + neighborhoodId +
                ", name='" + name + '\'' +
                '}';
    }
}

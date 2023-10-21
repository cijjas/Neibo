package ar.edu.itba.paw.models;

public class Neighborhood {
    private final Long neighborhoodId;
    private final String name;

    private Neighborhood(Builder builder) {
        this.neighborhoodId = builder.neighborhoodId;
        this.name = builder.name;
    }

    public Long getNeighborhoodId() {
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

    public static class Builder {
        private Long neighborhoodId;
        private String name;

        public Builder neighborhoodId(Long neighborhoodId) {
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
}

package ar.edu.itba.paw.enums;

public enum BaseNeighborhood {
    WORKERS_NEIGHBORHOOD(0),
    REJECTED_NEIGHBORHOOD(-1);

    private final int id;

    BaseNeighborhood(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}


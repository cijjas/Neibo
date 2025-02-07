package ar.edu.itba.paw.enums;

import java.util.Arrays;

public enum BaseNeighborhood {
    SUPER_ADMINISTRATOR(-2),
    REJECTED(-1),
    WORKERS(0);

    private final int id;

    BaseNeighborhood(int id) {
        this.id = id;
    }

    public static boolean isABaseNeighborhood(long id) {
        return Arrays.stream(BaseNeighborhood.values())
                .anyMatch(neighborhood -> neighborhood.getId() == id);
    }

    public int getId() {
        return id;
    }
}


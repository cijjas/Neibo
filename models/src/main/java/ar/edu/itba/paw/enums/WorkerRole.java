package ar.edu.itba.paw.enums;

import java.util.Arrays;
import java.util.Optional;

public enum WorkerRole {
    VERIFIED_WORKER(1),
    UNVERIFIED_WORKER(2),
    REJECTED(3);

    private final int id;

    WorkerRole(int id) {
        this.id = id;
    }

    public static Optional<WorkerRole> fromId(long id) {
        return Arrays.stream(values())
                .filter(wr -> wr.getId() == id)
                .findFirst();
    }

    public int getId() {
        return id;
    }
}


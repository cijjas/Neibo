package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

public enum WorkerRole {
    VERIFIED_WORKER(1),
    UNVERIFIED_WORKER(2),
    REJECTED(3);

    private final int id;

    WorkerRole(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static WorkerRole fromId(long id) {
        return Arrays.stream(values())
                .filter(wr -> wr.getId() == id)
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}


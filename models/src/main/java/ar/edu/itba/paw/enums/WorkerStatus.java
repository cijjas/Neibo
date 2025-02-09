package ar.edu.itba.paw.enums;

import java.util.Arrays;
import java.util.Optional;

public enum WorkerStatus {
    HOT(1),
    NONE(2);

    private final int id;

    WorkerStatus(int id) {
        this.id = id;
    }

    public static Optional<WorkerStatus> fromId(long id) {
        return Arrays.stream(values())
                .filter(ws -> ws.getId() == id)
                .findFirst();
    }

    public int getId() {
        return id;
    }
}

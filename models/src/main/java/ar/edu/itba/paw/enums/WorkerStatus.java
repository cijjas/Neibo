package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

public enum WorkerStatus {
    HOT(1),
    NONE(2);

    private final int id;

    WorkerStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static WorkerStatus fromId(long id) {
        return Arrays.stream(values())
                .filter(ws -> ws.getId() == id)
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}

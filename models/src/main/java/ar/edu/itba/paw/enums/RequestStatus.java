package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

public enum RequestStatus {
    REQUESTED(1),
    DECLINED(2),
    ACCEPTED(3);

    private final int id;

    RequestStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static RequestStatus fromId(long id) {
        return Arrays.stream(values())
                .filter(rs -> rs.getId() == id)
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}

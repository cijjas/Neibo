package ar.edu.itba.paw.enums;

import java.util.Arrays;
import java.util.Optional;

public enum RequestStatus {
    REQUESTED(1),
    DECLINED(2),
    ACCEPTED(3);

    private final int id;

    RequestStatus(int id) {
        this.id = id;
    }

    public static Optional<RequestStatus> fromId(long id) {
        return Arrays.stream(values())
                .filter(rs -> rs.getId() == id)
                .findFirst();
    }

    public int getId() {
        return id;
    }
}

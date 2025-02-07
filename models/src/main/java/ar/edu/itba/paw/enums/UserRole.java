package ar.edu.itba.paw.enums;

import java.util.Arrays;
import java.util.Optional;

public enum UserRole {
    ADMINISTRATOR(1),
    NEIGHBOR(2),
    UNVERIFIED_NEIGHBOR(3),
    REJECTED(4),
    WORKER(5),
    SUPER_ADMINISTRATOR(6);

    private final int id;

    UserRole(int id) {
        this.id = id;
    }

    public static Optional<UserRole> fromId(long id) {
        return Arrays.stream(values())
                .filter(us -> us.getId() == id)
                .findFirst();
    }

    public int getId() {
        return id;
    }
}
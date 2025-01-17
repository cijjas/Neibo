package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

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

    public int getId() {
        return id;
    }

    public static UserRole fromId(long id) {
        return Arrays.stream(values())
                .filter(ur -> ur.getId() == id)
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}
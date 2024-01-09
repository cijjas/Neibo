package ar.edu.itba.paw.enums;

import java.util.Arrays;

public enum UserRole {
    ADMINISTRATOR,
    NEIGHBOR,
    UNVERIFIED_NEIGHBOR,
    WORKER,
    REJECTED;

    public int getId() {
        return ordinal() + 1;
    }

    public static UserRole fromId(int id) {
        return Arrays.stream(values())
                .filter(language -> language.getId() == id)
                .findFirst()
                .orElse(null);
    }
}

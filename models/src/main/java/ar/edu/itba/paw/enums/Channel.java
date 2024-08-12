package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

public enum Channel {
    ANNOUNCEMENTS,
    COMPLAINTS,
    FEED,
    WORKERS,

    RESERVATIONS,

    INFORMATION;

    @Override
    public String toString() {
        String name = name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static Channel fromId(long id) {
        if(id <= 0)
            throw new IllegalArgumentException("Invalid value (" + id + ") for the Base Channel ID. Please use a positive integer greater than 0.");
        return Arrays.stream(values())
                .filter(bc -> bc.getId() == id)
                .findFirst()
                .orElseThrow(()-> new NotFoundException("Base Channel Not Found"));
    }

    public static Channel nullableFromId(long id) {
        return Arrays.stream(values())
                .filter(bc -> bc.getId() == id)
                .findFirst().orElse(null);
    }

    public int getId() {
        return ordinal() + 1;
    }
}


package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

public enum BaseChannel {
    ANNOUNCEMENTS(1),
    COMPLAINTS(2),
    FEED(3),
    WORKERS(4);

    private final int id;

    BaseChannel(int id) {
        this.id = id;
    }

    public static BaseChannel fromId(long id) {
        if (id <= 0)
            throw new IllegalArgumentException("Invalid value (" + id + ") for the Base Channel ID. Please use a positive integer greater than 0.");
        return Arrays.stream(values())
                .filter(bc -> bc.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Base Channel Not Found"));
    }

    @Override
    public String toString() {
        String name = name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public int getId() {
        return id;
    }
}
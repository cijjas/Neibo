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
        return Arrays.stream(values())
                .filter(bc -> bc.getId() == id)
                .findFirst()
                .orElseThrow(NotFoundException::new);
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
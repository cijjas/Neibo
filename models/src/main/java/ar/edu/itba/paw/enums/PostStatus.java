package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

public enum PostStatus {
    HOT(1),
    TRENDING(2),
    NONE(3);

    private final int id;

    PostStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static PostStatus fromId(long id) {
        return Arrays.stream(values())
                .filter(ps -> ps.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Post Status Not Found"));
    }
}

package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

public enum PostStatus {
    HOT,
    TRENDING,
    NONE;

    public static PostStatus fromId(long id) {
        if (id <= 0)
            throw new IllegalArgumentException("Invalid value (" + id + ") for the Post Status ID. Please use a positive integer greater than 0.");
        return Arrays.stream(values())
                .filter(ps -> ps.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Post Status Not Found"));
    }

    public int getId() {
        return ordinal() + 1;
    }

}

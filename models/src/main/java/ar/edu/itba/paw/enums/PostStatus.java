package ar.edu.itba.paw.enums;

import java.util.Arrays;
import java.util.Optional;

public enum PostStatus {
    HOT(1),
    TRENDING(2),
    NONE(3);

    private final int id;

    PostStatus(int id) {
        this.id = id;
    }

    public static Optional<PostStatus> fromId(long id) {
        return Arrays.stream(values())
                .filter(ps -> ps.getId() == id)
                .findFirst();
    }

    public int getId() {
        return id;
    }
}

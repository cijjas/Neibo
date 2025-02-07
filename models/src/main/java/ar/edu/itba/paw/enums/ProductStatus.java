package ar.edu.itba.paw.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ProductStatus {
    BOUGHT(1),
    SOLD(2),
    SELLING(3);

    private final int id;

    ProductStatus(int id) {
        this.id = id;
    }

    public static Optional<ProductStatus> fromId(long id) {
        return Arrays.stream(values())
                .filter(ps -> ps.getId() == id)
                .findFirst();
    }

    public int getId() {
        return id;
    }
}

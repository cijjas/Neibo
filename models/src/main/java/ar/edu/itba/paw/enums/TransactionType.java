package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;
import java.util.Optional;

public enum TransactionType {
    PURCHASE(1),
    SALE(2);

    private final int id;

    TransactionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Optional<TransactionType> fromId(long id) {
        return Arrays.stream(values())
                .filter(tt -> tt.getId() == id)
                .findFirst();
    }
}

package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

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

    public static TransactionType fromId(long id) {
        return Arrays.stream(values())
                .filter(tt -> tt.getId() == id)
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}

package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

public enum TransactionType {
    PURCHASE,
    SALE;

    public static TransactionType fromId(long id) {
        if (id <= 0)
            throw new IllegalArgumentException("Invalid value (" + id + ") for the Transaction Type ID. Please use a positive integer greater than 0.");
        return Arrays.stream(values())
                .filter(tt -> tt.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Transaction Type Not Found"));
    }

    public int getId() {
        return ordinal() + 1;
    }
}

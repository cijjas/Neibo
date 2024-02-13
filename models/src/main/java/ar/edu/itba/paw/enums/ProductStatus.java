package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

public enum ProductStatus {
    BOUGHT, // Search in products bought by the user
    SOLD,   // Search in products sold by the user
    SELLING; // Search in products the user is currently selling

    public static ProductStatus fromId(long id) {
        if(id <= 0)
            throw new IllegalArgumentException("Invalid value (" + id + ") for the Product Status ID. Please use a positive integer greater than 0.");
        return Arrays.stream(values())
                .filter(ps -> ps.getId() == id)
                .findFirst()
                .orElseThrow(()-> new NotFoundException("Product Status Not Found"));
    }
    public int getId() {
        return ordinal() + 1;
    }
}

package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

public enum ShiftStatus {
    FREE, TAKEN;

    public static ShiftStatus fromId(long id) {
        if(id <= 0)
            throw new IllegalArgumentException("Invalid value (" + id + ") for the Shift Status ID. Please use a positive integer greater than 0.");
        return Arrays.stream(values())
                .filter(ss -> ss.getId() == id)
                .findFirst()
                .orElseThrow(()-> new NotFoundException("Shift Status Not Found"));
    }

    public int getId() {
        return ordinal() + 1;
    }
}

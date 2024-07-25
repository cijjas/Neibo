package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

public enum UserRole {
    ADMINISTRATOR,
    NEIGHBOR,
    UNVERIFIED_NEIGHBOR,
    REJECTED,
    WORKER,
    SUPER_ADMINISTRATOR;

    public int getId() {
        return ordinal() + 1;
    }

    public static UserRole fromId(long id) {
        if(id <= 0)
            throw new IllegalArgumentException("Invalid value (" + id + ") for the User Role ID. Please use a positive integer greater than 0.");
        return Arrays.stream(values())
                .filter(ur -> ur.getId() == id)
                .findFirst()
                .orElseThrow(()-> new NotFoundException("User Role Not Found"));
    }
}

package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

public enum WorkerRole {
    VERIFIED_WORKER,
    UNVERIFIED_WORKER,
    REJECTED;

    public static WorkerRole fromId(long id) {
        if(id <= 0)
            throw new IllegalArgumentException("Invalid value (" + id + ") for the Worker Role ID. Please use a positive integer greater than 0.");
        return Arrays.stream(values())
                .filter(wr -> wr.getId() == id)
                .findFirst()
                .orElseThrow(()-> new NotFoundException("Worker Role Not Found"));
    }

    public int getId() {
        return ordinal() + 1;
    }
}

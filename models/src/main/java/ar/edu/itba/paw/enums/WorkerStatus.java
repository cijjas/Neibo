package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

public enum WorkerStatus {
    HOT,
    NONE;

    public static WorkerStatus fromId(long id) {
        if(id <= 0)
            throw new IllegalArgumentException("Invalid value (" + id + ") for the Worker Status ID. Please use a positive integer greater than 0.");
        return Arrays.stream(values())
                .filter(ws -> ws.getId() == id)
                .findFirst()
                .orElseThrow(()-> new NotFoundException("Worker Status Not Found"));
    }

    public int getId() {
        return ordinal() + 1;
    }
}

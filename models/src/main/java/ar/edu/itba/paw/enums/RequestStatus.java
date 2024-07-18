package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

public enum RequestStatus {
    REQUESTED,
    DECLINED,
    ACCEPTED;

    public static RequestStatus fromId(long id) {
        if(id <= 0)
            throw new IllegalArgumentException("Invalid value (" + id + ") for the Request Status ID. Please use a positive integer greater than 0.");
        return Arrays.stream(values())
                .filter(rs -> rs.getId() == id)
                .findFirst()
                .orElseThrow(()-> new NotFoundException("Request Status Not Found"));
    }
    public int getId() {
        return ordinal() + 1;
    }
}

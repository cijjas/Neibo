package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Shift;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.sql.Time;

public class ShiftDto {
    private java.sql.Time endTime;
    private boolean taken;
    private URI self;
    private URI amenities;
    private URI day;
    private URI startTime;

    public static ShiftDto fromShift(Shift shift, UriInfo uriInfo){
        final ShiftDto dto = new ShiftDto();

        dto.endTime = shift.getEndTime();
        dto.taken = shift.isTaken();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("shifts")
                .path(String.valueOf(shift.getShiftId()))
                .build();

        return dto;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getAmenities() {
        return amenities;
    }

    public void setAmenities(URI amenities) {
        this.amenities = amenities;
    }

    public URI getDay() {
        return day;
    }

    public void setDay(URI day) {
        this.day = day;
    }

    public URI getStartTime() {
        return startTime;
    }

    public void setStartTime(URI startTime) {
        this.startTime = startTime;
    }
}

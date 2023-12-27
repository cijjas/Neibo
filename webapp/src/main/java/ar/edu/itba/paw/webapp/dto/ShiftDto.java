package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Shift;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

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


}

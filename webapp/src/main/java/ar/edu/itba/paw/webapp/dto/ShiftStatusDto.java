package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.ProductStatus;
import ar.edu.itba.paw.enums.ShiftStatus;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class ShiftStatusDto {

    private ShiftStatus shiftStatus;

    private URI self;

    public static ShiftStatusDto fromShiftStatus(ShiftStatus shiftStatus, UriInfo uriInfo){
        final ShiftStatusDto dto = new ShiftStatusDto();

        dto.shiftStatus = shiftStatus;

        dto.self = uriInfo.getBaseUriBuilder()
                .path("shift-statuses")
                .path(String.valueOf(shiftStatus.getId()))
                .build();

        return dto;
    }

    public ShiftStatus getShiftStatus() {
        return shiftStatus;
    }

    public void setShiftStatus(ShiftStatus shiftStatus) {
        this.shiftStatus = shiftStatus;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}

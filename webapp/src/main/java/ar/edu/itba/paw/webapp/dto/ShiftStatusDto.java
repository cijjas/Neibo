package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.ShiftStatus;

import javax.ws.rs.core.UriInfo;

public class ShiftStatusDto {

    private ShiftStatus shiftStatus;
    private Links _links;

    public static ShiftStatusDto fromShiftStatus(ShiftStatus shiftStatus, UriInfo uriInfo) {
        final ShiftStatusDto dto = new ShiftStatusDto();

        dto.shiftStatus = shiftStatus;

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("shift-statuses")
                .path(String.valueOf(shiftStatus.getId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public ShiftStatus getShiftStatus() {
        return shiftStatus;
    }

    public void setShiftStatus(ShiftStatus shiftStatus) {
        this.shiftStatus = shiftStatus;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}

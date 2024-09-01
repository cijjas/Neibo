package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Shift;

import javax.ws.rs.core.UriInfo;

public class ShiftDto {

    private String day;
    private String startTime;
    private Boolean isBooked;
    private Links _links;

    public static ShiftDto fromShift(Shift shift, UriInfo uriInfo) {
        final ShiftDto dto = new ShiftDto();

        dto.startTime = shift.getStartTime().getTimeInterval().toString();

        dto.day = shift.getDay().getDayName();

        if (shift.getTaken() != null)
            dto.isBooked= shift.getTaken();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("shifts")
                .path(String.valueOf(shift.getShiftId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public Boolean getIsBooked() {
        return isBooked;
    }

    public void setIsBooked(Boolean isBooked) {
        this.isBooked = isBooked;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
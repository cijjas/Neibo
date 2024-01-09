package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.DayOfTheWeek;
import ar.edu.itba.paw.enums.StandardTime;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class DayDto {
    private String day;
    private URI self;

    public static DayDto fromDay(DayOfTheWeek day, UriInfo uriInfo){
        final DayDto dto = new DayDto();

        dto.day = day.name();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("days")
                .path(String.valueOf(day.getId()))
                .build();

        return dto;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}

package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.StandardTime;
import ar.edu.itba.paw.models.Entities.Time;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class TimeDto {
    private String timeInterval;
    private URI self;

    public static TimeDto fromTime(StandardTime time, UriInfo uriInfo){
        final TimeDto dto = new TimeDto();

        dto.timeInterval = time.toString();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("times")
                .path(String.valueOf(time.getId()))
                .build();

        return dto;
    }

    public String getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(String timeInterval) {
        this.timeInterval = timeInterval;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}

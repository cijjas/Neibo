package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.Professions;
import ar.edu.itba.paw.models.Entities.Profession;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class ProfessionDto {

    private String name;
    private URI self;
    private URI workers;

    public static ProfessionDto fromProfession(Professions profession, UriInfo uriInfo){
        final ProfessionDto dto = new ProfessionDto();

        dto.name = profession.name();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("professions")
                .path(String.valueOf(profession.getId()))
                .build();

        dto.workers = uriInfo.getBaseUriBuilder()
                .path("workers")
                .queryParam("profession", String.valueOf(profession.getId()))
                .build();

        return dto;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getWorkers() {
        return workers;
    }

    public void setWorkers(URI workers) {
        this.workers = workers;
    }
}

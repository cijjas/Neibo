package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.Professions;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class ProfessionDto {

    private String name;
    private Links _links;

    public static ProfessionDto fromProfession(Professions profession, UriInfo uriInfo) {
        final ProfessionDto dto = new ProfessionDto();

        dto.name = profession.name();

        Links links = new Links();
        URI self = uriInfo.getBaseUriBuilder()
                .path("professions")
                .path(String.valueOf(profession.getId()))
                .build();

        links.setSelf(self);
        links.setWorkers(uriInfo.getBaseUriBuilder()
                .path("workers")
                .queryParam("withProfessions", self)
                .build());
        dto.set_links(links);
        return dto;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}

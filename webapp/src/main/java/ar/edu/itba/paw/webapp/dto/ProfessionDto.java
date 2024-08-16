package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Profession;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class ProfessionDto {

    private String profession;
    private Links _links;

    public static ProfessionDto fromProfession(Profession profession, UriInfo uriInfo) {
        final ProfessionDto dto = new ProfessionDto();

        dto.profession = profession.getProfession();

        Links links = new Links();
        URI self = uriInfo.getBaseUriBuilder()
                .path("professions")
                .path(String.valueOf(profession.getProfessionId()))
                .build();

        links.setSelf(self);
        links.setWorkers(uriInfo.getBaseUriBuilder()
                .path("workers")
                .queryParam("withProfessions", self)
                .build());
        dto.set_links(links);
        return dto;
    }


    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}

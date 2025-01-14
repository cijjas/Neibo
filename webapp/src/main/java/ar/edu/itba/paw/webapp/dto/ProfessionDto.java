package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Profession;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class ProfessionDto {
    @NotNull(groups = Null.class)
    @Size(min = 1, max = 20, groups = Basic.class)
    private String name;

    private Links _links;

    public static ProfessionDto fromProfession(Profession profession, UriInfo uriInfo) {
        final ProfessionDto dto = new ProfessionDto();

        dto.name = profession.getProfession();

        Links links = new Links();

        String professionId = String.valueOf(profession.getProfessionId());

        UriBuilder professionUri = uriInfo.getBaseUriBuilder().path(Endpoint.PROFESSIONS).path(professionId);
        UriBuilder workersUri = uriInfo.getBaseUriBuilder().path(Endpoint.WORKERS).queryParam(QueryParameter.WITH_PROFESSION, professionUri.build());

        links.setSelf(professionUri.build());
        links.setWorkers(workersUri.build());

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

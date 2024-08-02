package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Affiliation;

import javax.ws.rs.core.UriInfo;

public class AffiliationDto {

    private Links _links;

    public static AffiliationDto fromAffiliation(Affiliation affiliation, UriInfo uriInfo) {
        final AffiliationDto dto = new AffiliationDto();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("affiliations")
                .queryParam("inNeighborhood", uriInfo.getBaseUriBuilder()
                        .path("neighborhoods")
                        .path(String.valueOf(affiliation.getNeighborhood().getNeighborhoodId()))
                        .build())
                .queryParam("forWorker", uriInfo.getBaseUriBuilder()
                        .path("workers")
                        .path(String.valueOf(affiliation.getWorker().getWorkerId()))
                        .build())
                .build());
        links.setWorker(uriInfo.getBaseUriBuilder()
                .path("workers")
                .path(String.valueOf(affiliation.getWorker().getWorkerId()))
                .build());
        links.setWorkerRole(uriInfo.getBaseUriBuilder()
                .path("worker-roles")
                .path(String.valueOf(affiliation.getRole().getId()))
                .build());
        links.setNeighborhood(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(affiliation.getNeighborhood().getNeighborhoodId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}

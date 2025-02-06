package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Affiliation;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class AffiliationDto {

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = URIValidator.WORKER_ROLE_URI_REGEX)
    private String workerRole;

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = URIValidator.WORKER_URI_REGEX)
    private String worker;

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = URIValidator.NEIGHBORHOOD_URI_REGEX)
    private String neighborhood;

    private Links _links;

    public static AffiliationDto fromAffiliation(Affiliation affiliation, UriInfo uriInfo) {
        final AffiliationDto dto = new AffiliationDto();

        Links links = new Links();

        String neighborhoodId = String.valueOf(affiliation.getNeighborhood().getNeighborhoodId());
        String workerId = String.valueOf(affiliation.getWorker().getWorkerId());
        String workerRoleId = String.valueOf(affiliation.getRole().getId());

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder workerUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.WORKERS).path(workerId);
        UriBuilder affiliationUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.AFFILIATIONS)
                .queryParam(QueryParameter.IN_NEIGHBORHOOD, neighborhoodUri.build())
                .queryParam(QueryParameter.FOR_WORKER, workerUri.build());
        UriBuilder workerRoleUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.WORKER_ROLES).path(workerRoleId);

        links.setSelf(affiliationUri.build());
        links.setWorker(workerUri.build());
        links.setWorkerRole(workerRoleUri.build());
        links.setNeighborhood(neighborhoodUri.build());

        dto.set_links(links);
        return dto;
    }

    public String getWorkerRole() {
        return workerRole;
    }

    public void setWorkerRole(String workerRole) {
        this.workerRole = workerRole;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }
}

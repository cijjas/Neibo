package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Affiliation;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.WorkerRoleURIReferenceInAffiliationConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.WorkerURIReferenceInAffiliationConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.uri.NeighborhoodURIConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.uri.WorkerRoleURIConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.uri.WorkerURIConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Authorization;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.URI;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class AffiliationDto {

    @NotNull(groups = Null.class)
    @WorkerRoleURIConstraint(groups = URI.class)
    @WorkerRoleURIReferenceInAffiliationConstraint(groups = Authorization.class)
    private String workerRole;

    @NotNull(groups = Null.class)
    @WorkerURIConstraint(groups = URI.class)
    @WorkerURIReferenceInAffiliationConstraint(groups = Authorization.class)
    private String worker;

    @NotNull(groups = Null.class)
    @NeighborhoodURIConstraint(groups = URI.class)
    private String neighborhood;

    private Links _links;

    public static AffiliationDto fromAffiliation(Affiliation affiliation, UriInfo uriInfo) {
        final AffiliationDto dto = new AffiliationDto();

        Links links = new Links();

        String neighborhoodId = String.valueOf(affiliation.getNeighborhood().getNeighborhoodId());
        String workerId = String.valueOf(affiliation.getWorker().getWorkerId());
        String workerRoleId = String.valueOf(affiliation.getRole().getId());

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder workerUri = uriInfo.getBaseUriBuilder().path(Endpoint.WORKERS).path(workerId);
        UriBuilder affiliationUri = uriInfo.getBaseUriBuilder().path(Endpoint.AFFILIATIONS)
                .queryParam(QueryParameter.IN_NEIGHBORHOOD, neighborhoodUri.build())
                .queryParam(QueryParameter.FOR_WORKER, workerUri.build());
        UriBuilder workerRoleUri = uriInfo.getBaseUriBuilder().path(Endpoint.WORKER_ROLES).path(workerRoleId);

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

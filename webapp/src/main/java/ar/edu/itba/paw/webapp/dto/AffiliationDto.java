package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Affiliation;
import ar.edu.itba.paw.webapp.validation.constraints.NeighborhoodURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.WorkerRoleURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.WorkerURNInAffiliationFormConstraint;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;

public class AffiliationDto {

    @WorkerRoleURNConstraint
    private String workerRole;

    @NotNull(groups = OnCreate.class)
    @WorkerURNInAffiliationFormConstraint(groups = OnCreate.class)
    private String worker;

    @NotNull(groups = OnCreate.class)
    @NeighborhoodURNConstraint(groups = OnCreate.class)
    private String neighborhood;

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

    @Override
    public String toString() {
        return "CreateAffiliationForm{" +
                "worker='" + worker + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                ", workerRole='" + workerRole + '\'' +
                '}';
    }
}

package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Affiliation;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.WorkerRoleURNReferenceInAffiliationConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.NeighborhoodURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.WorkerRoleURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.WorkerURNReferenceInAffiliationConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.WorkerURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.NeighborhoodURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.WorkerRoleURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.WorkerURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.groups.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;

public class AffiliationDto {
    @NotNull(groups = Null.class)
    @WorkerRoleURNFormConstraint(groups = Form.class)
    @WorkerRoleURNReferenceConstraint(groups = Reference.class)
    @WorkerRoleURNReferenceInAffiliationConstraint(groups = Authorization.class)
    private String workerRole;

    @NotNull(groups = Null.class)
    @WorkerURNFormConstraint(groups = Form.class)
    @WorkerURNReferenceConstraint(groups = Reference.class)
    @WorkerURNReferenceInAffiliationConstraint(groups = Authorization.class)
    private String worker;

    @NotNull(groups = Null.class)
    @NeighborhoodURNFormConstraint(groups = Form.class)
    @NeighborhoodURNReferenceConstraint(groups = Authorization.class)
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

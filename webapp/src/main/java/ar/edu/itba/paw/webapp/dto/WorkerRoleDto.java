package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.WorkerRole;

import javax.ws.rs.core.UriInfo;

public class WorkerRoleDto {

    private WorkerRole role;

    private Links _links;

    public static WorkerRoleDto fromWorkerRole(WorkerRole workerRole, UriInfo uriInfo) {
        final WorkerRoleDto dto = new WorkerRoleDto();

        dto.role = workerRole;

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("worker-roles")
                .path(String.valueOf(workerRole.getId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public WorkerRole getRole() {
        return role;
    }

    public void setRole(WorkerRole role) {
        this.role = role;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}

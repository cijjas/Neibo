package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.enums.WorkerRole;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class WorkerRoleDto {

    private WorkerRole workerRole;

    private URI self;

    public static WorkerRoleDto fromWorkerRole(WorkerRole workerRole, UriInfo uriInfo){
        final WorkerRoleDto dto = new WorkerRoleDto();

        dto.workerRole = workerRole;

        dto.self = uriInfo.getBaseUriBuilder()
                .path("worker-roles")
                .path(String.valueOf(workerRole.getId()))
                .build();

        return dto;
    }

    public WorkerRole getWorkerRole() {
        return workerRole;
    }

    public void setWorkerRole(WorkerRole workerRole) {
        this.workerRole = workerRole;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}

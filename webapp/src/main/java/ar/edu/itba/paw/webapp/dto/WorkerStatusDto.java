package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.enums.WorkerStatus;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class WorkerStatusDto {

    private WorkerStatus workerStatus;

    private URI self;

    public static WorkerStatusDto fromWorkerStatus(WorkerStatus workerStatus, UriInfo uriInfo){
        final WorkerStatusDto dto = new WorkerStatusDto();

        dto.workerStatus = workerStatus;

        dto.self = uriInfo.getBaseUriBuilder()
                .path("worker-statuses")
                .path(String.valueOf(workerStatus.getId()))
                .build();

        return dto;
    }

    public WorkerStatus getWorkerStatus() {
        return workerStatus;
    }

    public void setWorkerStatus(WorkerStatus workerStatus) {
        this.workerStatus = workerStatus;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}

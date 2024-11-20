package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.WorkerStatus;

import javax.ws.rs.core.UriInfo;

public class WorkerStatusDto {

    private WorkerStatus workerStatus;

    private Links _links;

    public static WorkerStatusDto fromWorkerStatus(WorkerStatus workerStatus, UriInfo uriInfo) {
        final WorkerStatusDto dto = new WorkerStatusDto();

        dto.workerStatus = workerStatus;

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("worker-statuses")
                .path(String.valueOf(workerStatus.getId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public WorkerStatus getWorkerStatus() {
        return workerStatus;
    }

    public void setWorkerStatus(WorkerStatus workerStatus) {
        this.workerStatus = workerStatus;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}

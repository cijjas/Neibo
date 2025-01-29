package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.WorkerStatus;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class WorkerStatusDto {

    private WorkerStatus status;

    private Links _links;

    public static WorkerStatusDto fromWorkerStatus(WorkerStatus workerStatus, UriInfo uriInfo) {
        final WorkerStatusDto dto = new WorkerStatusDto();

        dto.status = workerStatus;

        Links links = new Links();

        String workerStatusId = String.valueOf(workerStatus.getId());

        UriBuilder workerStatusUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.WORKER_STATUSES).path(workerStatusId);

        links.setSelf(workerStatusUri.build());

        dto.set_links(links);
        return dto;
    }

    public WorkerStatus getStatus() {
        return status;
    }

    public void setStatus(WorkerStatus status) {
        this.status = status;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}

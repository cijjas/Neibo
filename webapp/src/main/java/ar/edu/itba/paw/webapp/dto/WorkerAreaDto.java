package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Availability;
import ar.edu.itba.paw.models.Entities.WorkerArea;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class WorkerAreaDto {
    private URI self;
    private URI worker; // localhost:8080/amenities/{id}
    private URI neighborhood; // localhost:8080/shifts/{id}

    public static WorkerAreaDto fromWorkerArea(WorkerArea workerArea, UriInfo uriInfo){
        final WorkerAreaDto dto = new WorkerAreaDto();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("affiliations")
                .queryParam("neighborhoodId", workerArea.getNeighborhood().getNeighborhoodId())
                .queryParam("workerId", workerArea.getWorker().getWorkerId())
                .build();
        dto.neighborhood = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(workerArea.getNeighborhood().getNeighborhoodId()))
                .build();
        dto.worker = uriInfo.getBaseUriBuilder()
                .path("workers")
                .path(String.valueOf(workerArea.getWorker().getWorkerId()))
                .build();

        return dto;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getWorker() {
        return worker;
    }

    public void setWorker(URI worker) {
        this.worker = worker;
    }

    public URI getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(URI neighborhood) {
        this.neighborhood = neighborhood;
    }
}

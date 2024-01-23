package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Affiliation;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class AffiliationDto {
    private URI self;
    private URI worker; // localhost:8080/amenities/{id}
    private URI neighborhood; // localhost:8080/shifts/{id}

    public static AffiliationDto fromAffiliation(Affiliation affiliation, UriInfo uriInfo){
        final AffiliationDto dto = new AffiliationDto();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("affiliations")
                .queryParam("inNeighborhood", affiliation.getNeighborhood().getNeighborhoodId())
                .queryParam("forWorker", affiliation.getWorker().getWorkerId())
                .build();
        dto.neighborhood = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(affiliation.getNeighborhood().getNeighborhoodId()))
                .build();
        dto.worker = uriInfo.getBaseUriBuilder()
                .path("workers")
                .path(String.valueOf(affiliation.getWorker().getWorkerId()))
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

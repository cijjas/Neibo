package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Worker;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class WorkerDto {

    private String phoneNumber;
    private String businessName;
    private String address;
    private String bio;
    private URI self;
    private URI user;
    private URI backgroundPicture;
    private URI reviews;
    private URI professions;
    private URI workerNeighborhoods;

    public static WorkerDto fromWorker(Worker worker, UriInfo uriInfo){
        final WorkerDto dto = new WorkerDto();

        dto.phoneNumber = worker.getPhoneNumber();
        dto.businessName = worker.getBusinessName();
        dto.address = worker.getAddress();
        dto.bio = worker.getBio();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("workers")
                .path(String.valueOf(worker.getWorkerId()))
                .build();

        dto.user = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(worker.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(worker.getUser().getUserId()))
                .build();

        dto.backgroundPicture = uriInfo.getBaseUriBuilder()
                .path("images")
                .path(String.valueOf(worker.getBackgroundPictureId()))
                .build();

        dto.reviews = uriInfo.getBaseUriBuilder()
                .path("workers")
                .path(String.valueOf(worker.getWorkerId()))
                .path("reviews")
                .build();

        dto.professions = uriInfo.getBaseUriBuilder()
                .path("professions")
                .queryParam("worker", String.valueOf(worker.getWorkerId()))
                .build();

        dto.workerNeighborhoods = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .queryParam("worker", String.valueOf(worker.getWorkerId()))
                .build();

        return dto;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

}

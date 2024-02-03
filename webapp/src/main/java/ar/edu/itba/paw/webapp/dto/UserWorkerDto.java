package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.User;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class UserWorkerDto {
    private String mail;
    private String name;
    private String surname;
    private Boolean darkMode;
    private String phoneNumber;
    private Integer identification;
    private URI self;
    private URI neighborhood;
    private URI profilePicture;
    private URI posts;

    public static UserWorkerDto fromUserWorker(final User user, final UriInfo uriInfo){
        final UserWorkerDto dto = new UserWorkerDto();

        dto.mail = user.getMail();
        dto.name = user.getName();
        dto.surname = user.getSurname();
        dto.darkMode = user.getDarkMode();
        dto.phoneNumber = user.getPhoneNumber();
        dto.identification = user.getIdentification();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(user.getUserId()))
                .build();

        dto.neighborhood = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                .build();
        if ( user.getProfilePicture() != null ){
            dto.profilePicture = uriInfo.getBaseUriBuilder()
                    .path("images")
                    .path(String.valueOf(user.getProfilePicture().getImageId()))
                    .build();
        }
        dto.posts = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                .path("posts")
                .queryParam("postedBy", String.valueOf(user.getUserId()))
                .build();
        return dto;
    }

    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public Boolean isDarkMode() { return darkMode; }
    public void setDarkMode(Boolean darkMode) { this.darkMode = darkMode; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Boolean getDarkMode() {
        return darkMode;
    }

    public Integer getIdentification() {
        return identification;
    }

    public void setIdentification(Integer identification) {
        this.identification = identification;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(URI neighborhood) {
        this.neighborhood = neighborhood;
    }

    public URI getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(URI profilePicture) {
        this.profilePicture = profilePicture;
    }

    public URI getPosts() {
        return posts;
    }

    public void setPosts(URI posts) {
        this.posts = posts;
    }

}

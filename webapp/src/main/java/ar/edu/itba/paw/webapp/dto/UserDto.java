package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.User;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class UserDto {
    private String mail;
    private String name;
    private String surname;
    private String password;
    private Boolean darkMode;
    private String phoneNumber;
    private URI self;
    private URI neighborhood;
    private URI profilePicture;
    private URI posts;
    private URI bookings;
    private URI subscribedPosts;
    private URI likedPosts;
    private URI purchases;
    private URI sales;
    private URI eventsSubscribed;

    public static UserDto fromUser(final User user, final UriInfo uriInfo){
        final UserDto dto = new UserDto();

        dto.mail = user.getMail();
        dto.name = user.getName();
        dto.surname = user.getSurname();
        dto.password = user.getPassword();
        dto.darkMode = user.getDarkMode();
        dto.phoneNumber = user.getPhoneNumber();

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
        dto.profilePicture = uriInfo.getBaseUriBuilder()
                .path("images")
                .path(String.valueOf(user.getProfilePicture().getImageId()))
                .build();
        dto.posts = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                .path("posts")
                .queryParam("postedBy", String.valueOf(user.getUserId()))
                .build();
        dto.bookings = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(user.getUserId()))
                .path("bookings")
                .build();
        dto.subscribedPosts = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                .path("posts")
                .queryParam("subscribedBy", String.valueOf(user.getUserId()))
                .build();
        dto.likedPosts = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                .path("posts")
                .queryParam("likedBy", String.valueOf(user.getUserId()))
                .build();
        dto.purchases = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(user.getUserId()))
                .path("purchases")
                .build();
        dto.sales = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(user.getUserId()))
                .path("sales")
                .build();
        dto.eventsSubscribed = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                .path("events")
                .queryParam("subscribedBy", String.valueOf(user.getUserId()))
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
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Boolean isDarkMode() { return darkMode; }
    public void setDarkMode(Boolean darkMode) { this.darkMode = darkMode; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

}

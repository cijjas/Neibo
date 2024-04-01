package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.User;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class UserDto {
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
    private URI bookings;
    private URI likedPosts;
    private URI purchases;
    private URI sales;

    public static UserDto fromUser(final User user, final UriInfo uriInfo){
        final UserDto dto = new UserDto();

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

        //If not a worker, also add the following URIs:
        if(user.getNeighborhood().getNeighborhoodId() != 0){
            dto.bookings = uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                    .path("bookings")
                    .queryParam("bookedBy", String.valueOf(user.getUserId()))
                    .build();
            dto.likedPosts = uriInfo.getBaseUriBuilder()
                    .path("likes")
                    .queryParam("likedBy", String.valueOf(user.getUserId()))
                    .build();
            dto.purchases = uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                    .path("users")
                    .path(String.valueOf(user.getUserId()))
                    .path("transactions")
                    .queryParam("withType", "PURCHASE")
                    .build();
            dto.sales = uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                    .path("users")
                    .path(String.valueOf(user.getUserId()))
                    .path("transactions")
                    .queryParam("withType", "SALE")
                    .build();
        }
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

    public URI getBookings() {
        return bookings;
    }

    public void setBookings(URI bookings) {
        this.bookings = bookings;
    }

    public URI getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(URI likedPosts) {
        this.likedPosts = likedPosts;
    }

    public URI getPurchases() {
        return purchases;
    }

    public void setPurchases(URI purchases) {
        this.purchases = purchases;
    }

    public URI getSales() {
        return sales;
    }

    public void setSales(URI sales) {
        this.sales = sales;
    }
}

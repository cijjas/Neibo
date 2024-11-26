package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.RequestStatus;
import ar.edu.itba.paw.enums.TransactionType;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.webapp.validation.constraints.form.ImageURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.LanguageURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserRoleURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.EmailConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.Specific;
import ar.edu.itba.paw.webapp.validation.groups.URN;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class UserDto {
    @NotNull(groups = Null.class)
    @Size(min = 1, max = 64, groups = Basic.class)
    @Pattern(regexp = "^[a-zA-Z ]*", groups = Basic.class)
    private String name;

    @NotNull(groups = Null.class)
    @Pattern(regexp = "^[a-zA-Z ]*", groups = Basic.class)
    @Size(min = 1, max = 64, groups = Basic.class)
    private String surname;

    @NotNull(groups = Null.class)
    @Size(min = 6, max = 128, groups = Basic.class)
    @Email(groups = Basic.class)
    @EmailConstraint(groups = Specific.class)
    private String mail;

    @NotNull(groups = Null.class)
    @Size(min = 1, max = 50, groups = Basic.class)
    private String password;

    @NotNull(groups = Null.class)
    private Integer identification;

    @LanguageURNConstraint(groups = URN.class)
    private String language;

    @UserRoleURNConstraint(groups = URN.class)
    private String userRole;

    @Size(min = 1, max = 50, groups = Basic.class)
    @Pattern(regexp = "^[0-9]*", groups = Basic.class)
    private String phoneNumber;

    @ImageURNConstraint(groups = URN.class)
    private String profilePicture;

    private Boolean darkMode;

    private Date creationDate;

    private Links _links;

    public static UserDto fromUser(final User user, final UriInfo uriInfo) {
        final UserDto dto = new UserDto();

        dto.mail = user.getMail();
        dto.name = user.getName();
        dto.surname = user.getSurname();
        dto.darkMode = user.getDarkMode();
        dto.phoneNumber = user.getPhoneNumber();
        dto.identification = user.getIdentification();
        dto.creationDate = user.getCreationDate();

        Links links = new Links();
        URI self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(user.getUserId()))
                .build();
        links.setSelf(self);
        links.setNeighborhood(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                .build());
        links.setUserRole(uriInfo.getBaseUriBuilder()
                .path("languages")
                .path(String.valueOf(user.getLanguage().getId()))
                .build());
        links.setUserRole(uriInfo.getBaseUriBuilder()
                .path("user-roles")
                .path(String.valueOf(user.getRole().getId()))
                .build());
        if (user.getProfilePicture() != null) {
            links.setProfilePicture(uriInfo.getBaseUriBuilder()
                    .path("images")
                    .path(String.valueOf(user.getProfilePicture().getImageId()))
                    .build());
        }
        links.setPosts(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                .path("posts")
                .queryParam("postedBy", self)
                .build());
        // If not a worker, also add the following URIs:
        if (user.getNeighborhood().getNeighborhoodId() != 0) {
            links.setBookings(uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                    .path("bookings")
                    .queryParam("bookedBy", self)
                    .build());
            links.setLikedPosts(uriInfo.getBaseUriBuilder()
                    .path("likes")
                    .queryParam("likedBy", self)
                    .build());
            links.setPurchases(uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                    .path("requests")
                    .queryParam("requestedBy", self)
                    .queryParam("withType", uriInfo.getBaseUriBuilder()
                            .path("transaction-types")
                            .path(String.valueOf(TransactionType.PURCHASE.getId())))
                    .queryParam("withStatus", uriInfo.getBaseUriBuilder()
                            .path("request-statuses")
                            .path(String.valueOf(RequestStatus.ACCEPTED.getId())))
                    .build());
            links.setRequests(uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                    .path("requests")
                    .queryParam("requestedBy", self)
                    .queryParam("withType", uriInfo.getBaseUriBuilder()
                            .path("transaction-types")
                            .path(String.valueOf(TransactionType.PURCHASE)))
                    .queryParam("withStatus", uriInfo.getBaseUriBuilder()
                            .path("request-statuses")
                            .path(String.valueOf(RequestStatus.REQUESTED.getId())))
                    .build());
            links.setSales(uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(user.getNeighborhood().getNeighborhoodId()))
                    .path("requests")
                    .queryParam("requestedBy", self)
                    .queryParam("withType", uriInfo.getBaseUriBuilder()
                            .path("transaction-types")
                            .path(String.valueOf(TransactionType.SALE)))
                    .queryParam("withStatus", uriInfo.getBaseUriBuilder()
                            .path("request-statuses")
                            .path(String.valueOf(RequestStatus.ACCEPTED.getId())))
                    .build());
        }
        dto.set_links(links);
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Boolean isDarkMode() {
        return darkMode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getDarkMode() {
        return darkMode;
    }

    public void setDarkMode(Boolean darkMode) {
        this.darkMode = darkMode;
    }

    public Integer getIdentification() {
        return identification;
    }

    public void setIdentification(Integer identification) {
        this.identification = identification;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}

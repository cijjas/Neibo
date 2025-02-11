package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.BaseNeighborhood;
import ar.edu.itba.paw.enums.RequestStatus;
import ar.edu.itba.paw.enums.TransactionType;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.EmailConstraint;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;
import ar.edu.itba.paw.webapp.validation.groups.Specific;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class UserDto {

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = URIValidator.NEIGHBORHOOD_URI_REGEX)
    private String neighborhood;

    @NotNull(groups = OnCreate.class)
    @Size(max = 64)
    @Pattern(regexp = "^[a-zA-Z ]*")
    private String name;

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = "^[a-zA-Z ]*")
    @Size(max = 64)
    private String surname;

    @NotNull(groups = OnCreate.class)
    @Size(max = 128)
    @Email
    @EmailConstraint(groups = Specific.class)
    private String mail;

    @NotNull(groups = OnCreate.class)
    @Size(max = 128)
    private String password;

    @NotNull(groups = OnCreate.class)
    private Integer identification;

    @Pattern(regexp = URIValidator.LANGUAGE_URI_REGEX)
    private String language;

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = URIValidator.USER_ROLE_URI_REGEX)
    private String userRole;

    @Size(max = 255)
    @Pattern(regexp = "^[0-9+\\- ]*$")
    private String phoneNumber;

    @Pattern(regexp = URIValidator.IMAGE_URI_REGEX)
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

        Long neighborhoodIdLong = user.getNeighborhood().getNeighborhoodId();
        String neighborhoodId = String.valueOf(neighborhoodIdLong);
        String userId = String.valueOf(user.getUserId());
        String languageId = String.valueOf(user.getLanguage().getId());
        String userRoleId = String.valueOf(user.getRole().getId());

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder userUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.USERS).path(userId);
        UriBuilder postsUri = neighborhoodUri.clone().path(Endpoint.POSTS).queryParam(QueryParameter.POSTED_BY, userUri.build());
        UriBuilder languageUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.LANGUAGES).path(languageId);
        UriBuilder userRoleUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.USER_ROLES).path(userRoleId);

        links.setSelf(userUri.build());
        links.setNeighborhood(neighborhoodUri.build());
        links.setLanguage(languageUri.build());
        links.setUserRole(userRoleUri.build());
        links.setPosts(postsUri.build());
        if (user.getProfilePicture() != null) {
            String imageId = String.valueOf(user.getProfilePicture().getImageId());
            UriBuilder imageUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.IMAGES).path(imageId);
            links.setUserImage(imageUri.build());
        }

        if (neighborhoodIdLong == BaseNeighborhood.WORKERS.getId()) {
            links.setWorker(uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.WORKERS).path(String.valueOf(user.getUserId())).build());
        }

        if (!BaseNeighborhood.isABaseNeighborhood(neighborhoodIdLong)) {
            String purchaseTransactionTypeId = String.valueOf(TransactionType.PURCHASE.getId());
            String saleTransactionTypeId = String.valueOf(TransactionType.SALE.getId());
            String acceptedRequestStatusId = String.valueOf(RequestStatus.ACCEPTED.getId());
            String requestedRequestStatusId = String.valueOf(RequestStatus.REQUESTED.getId());

            UriBuilder purchaseTransactionTypeUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.TRANSACTION_TYPES).path(purchaseTransactionTypeId);
            UriBuilder saleTransactionTypeUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.TRANSACTION_TYPES).path(saleTransactionTypeId);
            UriBuilder acceptedRequestStatusUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.REQUEST_STATUSES).path(acceptedRequestStatusId);
            UriBuilder requestedRequestStatusUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.REQUEST_STATUSES).path(requestedRequestStatusId);
            UriBuilder bookingsUri = neighborhoodUri.clone().path(Endpoint.BOOKINGS).queryParam(QueryParameter.BOOKED_BY, userUri.build());
            UriBuilder likesUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(neighborhoodId).path(Endpoint.LIKES).queryParam(QueryParameter.LIKED_BY, userUri.build());
            UriBuilder purchasesUri = neighborhoodUri.clone().path(Endpoint.REQUESTS)
                    .queryParam(QueryParameter.WITH_TYPE, purchaseTransactionTypeUri.build()).queryParam(QueryParameter.WITH_STATUS, acceptedRequestStatusUri.build());
            UriBuilder requestsUri = neighborhoodUri.clone().path(Endpoint.REQUESTS)
                    .queryParam(QueryParameter.WITH_TYPE, purchaseTransactionTypeUri.build()).queryParam(QueryParameter.WITH_STATUS, requestedRequestStatusUri.build());
            UriBuilder salesUri = neighborhoodUri.clone().path(Endpoint.REQUESTS)
                    .queryParam(QueryParameter.WITH_TYPE, saleTransactionTypeUri.build()).queryParam(QueryParameter.WITH_STATUS, acceptedRequestStatusUri.build());

            links.setBookings(bookingsUri.build());
            links.setLikedPosts(likesUri.build());
            links.setPurchases(purchasesUri.build());
            links.setRequests(requestsUri.build());
            links.setSales(salesUri.build());
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

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }
}

package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.webapp.controller.QueryParameters;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class NeighborhoodDto {

    @NotNull(groups = Null.class)
    @Size(min = 1, max = 20, groups = Basic.class)
    private String name;

    private Links _links;

    public static NeighborhoodDto fromNeighborhood(Neighborhood neighborhood, UriInfo uriInfo) {
        final NeighborhoodDto dto = new NeighborhoodDto();

        dto.name = neighborhood.getName();

        Links links = new Links();
        long neighborhoodId = neighborhood.getNeighborhoodId();

        // Templating Test
        links.setPosts2("http://localhost:8080/neighborhoods/1/posts{?postedBy,inChannel,withTags*,withStatus,page,size}");

        // First layer - Should eventually be moved into Root Dto
        UriBuilder affiliationsUri = uriInfo.getBaseUriBuilder().path(Endpoint.AFFILIATIONS.toString());
        UriBuilder userRolesUri = uriInfo.getBaseUriBuilder().path(Endpoint.USER_ROLES.toString());
        UriBuilder neighborhoodsUri = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS.toString());
        UriBuilder workersUri = uriInfo.getBaseUriBuilder().path(Endpoint.WORKERS.toString());
        UriBuilder imagesUri = uriInfo.getBaseUriBuilder().path(Endpoint.IMAGES.toString());

        UriBuilder departmentsUri = uriInfo.getBaseUriBuilder().path(Endpoint.DEPARTMENTS.toString());
        UriBuilder professionsUri = uriInfo.getBaseUriBuilder().path(Endpoint.PROFESSIONS.toString());
        UriBuilder shiftsUri = uriInfo.getBaseUriBuilder().path(Endpoint.SHIFTS.toString());

        UriBuilder languagesUri = uriInfo.getBaseUriBuilder().path(Endpoint.LANGUAGES.toString());
        UriBuilder postStatusesUri = uriInfo.getBaseUriBuilder().path(Endpoint.POST_STATUSES.toString());
        UriBuilder productStatusesUri = uriInfo.getBaseUriBuilder().path(Endpoint.PRODUCT_STATUSES.toString());
        UriBuilder requestStatusesUri = uriInfo.getBaseUriBuilder().path(Endpoint.REQUEST_STATUSES.toString());
        UriBuilder transactionTypesUri = uriInfo.getBaseUriBuilder().path(Endpoint.TRANSACTION_TYPES.toString());
        UriBuilder workerRolesUri = uriInfo.getBaseUriBuilder().path(Endpoint.WORKER_ROLES.toString());

        // Setting First Layer
        links.setAffiliations(affiliationsUri.build());
        links.setNeighborhoods(neighborhoodsUri.build());
        links.setWorkers(workersUri.build());
        links.setImages(imagesUri.build());

        links.setDepartments(departmentsUri.build());
        links.setProfessions(professionsUri.build());
        links.setShifts(shiftsUri.build());

        links.setLanguageEnglish(languagesUri.clone().path(String.valueOf(Language.ENGLISH)).build());
        links.setLanguageSpanish(languagesUri.clone().path(String.valueOf(Language.SPANISH)).build());

        links.setPostStatuses(postStatusesUri.build()); // Should be removed as its redundant, already extensively defined
        links.setHotPostStatus(postStatusesUri.clone().path(String.valueOf(PostStatus.HOT.getId())).build());
        links.setTrendingPostStatus(postStatusesUri.clone().path(String.valueOf(PostStatus.TRENDING.getId())).build());
        links.setNonePostStatus(postStatusesUri.clone().path(String.valueOf(PostStatus.NONE.getId())).build());

        links.setAcceptedRequestStatus(requestStatusesUri.clone().path(String.valueOf(RequestStatus.ACCEPTED.getId())).build());
        links.setDeclinedRequestStatus(requestStatusesUri.clone().path(String.valueOf(RequestStatus.DECLINED.getId())).build());
        links.setRequestedRequestStatus(requestStatusesUri.clone().path(String.valueOf(RequestStatus.REQUESTED.getId())).build());

        links.setPurchaseTransactionType(transactionTypesUri.clone().path(String.valueOf(TransactionType.PURCHASE.getId())).build());
        links.setSaleTransactionType(transactionTypesUri.clone().path(String.valueOf(TransactionType.SALE.getId())).build());

        links.setBoughtProductStatus(productStatusesUri.clone().path(String.valueOf(ProductStatus.BOUGHT.getId())).build());
        links.setSellingProductStatus(productStatusesUri.clone().path(String.valueOf(ProductStatus.SELLING.getId())).build());
        links.setSoldProductStatus(productStatusesUri.clone().path(String.valueOf(ProductStatus.SOLD.getId())).build());

        links.setAdministratorUserRole(userRolesUri.clone().path(String.valueOf(UserRole.ADMINISTRATOR.getId())).build());
        links.setNeighborUserRole(userRolesUri.clone().path(String.valueOf(UserRole.NEIGHBOR.getId())).build());
        links.setUnverifiedNeighborUserRole(userRolesUri.clone().path(String.valueOf(UserRole.UNVERIFIED_NEIGHBOR.getId())).build());
        links.setRejectedUserRole(userRolesUri.clone().path(String.valueOf(UserRole.REJECTED.getId())).build());
        links.setWorkerUserRole(userRolesUri.clone().path(String.valueOf(UserRole.WORKER.getId())).build());
        links.setSuperAdministratorUserRole(userRolesUri.clone().path(String.valueOf(UserRole.SUPER_ADMINISTRATOR.getId())).build());

        links.setVerifiedWorkerRole(workerRolesUri.clone().path(String.valueOf(WorkerRole.VERIFIED_WORKER.getId())).build());
        links.setUnverifiedWorkerRole(workerRolesUri.clone().path(String.valueOf(WorkerRole.UNVERIFIED_WORKER.getId())).build());
        links.setRejectedWorkerRole(workerRolesUri.clone().path(String.valueOf(WorkerRole.REJECTED.getId())).build());

        // Second layer
        UriBuilder self = neighborhoodsUri.clone().path(String.valueOf(neighborhoodId));
        UriBuilder channelsUri = self.clone().path(Endpoint.CHANNELS.toString());
        UriBuilder eventsUri = self.clone().path(Endpoint.EVENTS.toString());
        UriBuilder postsUri = self.clone().path(Endpoint.POSTS.toString());
        UriBuilder postsCountUri = self.clone().path(Endpoint.POSTS_COUNT.toString());
        UriBuilder usersUri = self.clone().path(Endpoint.USERS.toString());

        // Setting Second Layer
        links.setSelf(self.build());
        links.setChannels(channelsUri.build());
        links.setEvents(eventsUri.build());
        links.setPosts(postsUri.build());
        links.setPostsCount(postsCountUri.build());
        links.setUsers(usersUri.build());

//        if (neighborhoodId == BaseNeighborhood.WORKERS.getId()){
//        }


        if (!BaseNeighborhood.isABaseNeighborhood(neighborhoodId)) {
            UriBuilder amenitiesUri = self.clone().path(Endpoint.AMENITIES.toString());
            UriBuilder attendanceUri = neighborhoodsUri.clone().path(String.valueOf(neighborhoodId)).path(Endpoint.ATTENDANCE.toString());
            UriBuilder contactsUri = neighborhoodsUri.clone().path(String.valueOf(neighborhoodId)).path(Endpoint.CONTACTS.toString());
            UriBuilder bookingsUri = neighborhoodsUri.clone().path(String.valueOf(neighborhoodId)).path(Endpoint.BOOKINGS.toString());
            UriBuilder likesUri = neighborhoodsUri.clone().path(String.valueOf(neighborhoodId)).path(Endpoint.LIKES.toString());
            UriBuilder productsUri = neighborhoodsUri.clone().path(String.valueOf(neighborhoodId)).path(Endpoint.PRODUCTS.toString());
            UriBuilder requestsUri = neighborhoodsUri.clone().path(String.valueOf(neighborhoodId)).path(Endpoint.REQUESTS.toString());
            UriBuilder resourcesUri = neighborhoodsUri.clone().path(String.valueOf(neighborhoodId)).path(Endpoint.RESOURCES.toString());
            UriBuilder tagsUri = neighborhoodsUri.clone().path(String.valueOf(neighborhoodId)).path(Endpoint.TAGS.toString());

            UriBuilder feedChannelUri = channelsUri.clone().path(String.valueOf(BaseChannel.FEED.getId()));
            UriBuilder complaintsChannelUri = channelsUri.clone().path(String.valueOf(BaseChannel.COMPLAINTS.getId()));
            UriBuilder announcementsChannelUri = channelsUri.clone().path(String.valueOf(BaseChannel.ANNOUNCEMENTS.getId()));

            links.setAmenities(amenitiesUri.build());
            links.setAttendance(attendanceUri.build());
            links.setContacts(contactsUri.build());
            links.setBookings(bookingsUri.build());
            links.setLikes(likesUri.build());
            links.setProducts(productsUri.build());
            links.setRequests(requestsUri.build());
            links.setResources(resourcesUri.build());
            links.setTags(tagsUri.build());

            links.setFeedChannel(feedChannelUri.build());
            links.setComplaintsChannel(complaintsChannelUri.build());
            links.setAnnouncementsChannel(announcementsChannelUri.build());

            links.setFeed(postsUri.clone().queryParam(QueryParameters.IN_CHANNEL, feedChannelUri.clone().build()).build());
            links.setComplaints(postsUri.clone().queryParam(QueryParameters.IN_CHANNEL, complaintsChannelUri.clone().build()).build());
            links.setAnnouncements(postsUri.clone().queryParam(QueryParameters.IN_CHANNEL, announcementsChannelUri.clone().build()).build());

            links.setWorkers(workersUri.queryParam(QueryParameters.IN_NEIGHBORHOOD, links.getSelf()).build());
        }

        dto.set_links(links);
        return dto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}

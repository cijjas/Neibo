package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.BaseChannel;
import ar.edu.itba.paw.enums.BaseNeighborhood;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class NeighborhoodDto {

    @NotNull(groups = Null.class)
    @Size(min = 1, max = 20, groups = Basic.class)
    private String name;

    private Links _links;

    public static NeighborhoodDto fromNeighborhood(Neighborhood neighborhood, UriInfo uriInfo) {
        final NeighborhoodDto dto = new NeighborhoodDto();

        dto.name = neighborhood.getName();

        Links links = new Links();

        Long neighborhoodIdLong = neighborhood.getNeighborhoodId();
        String neighborhoodId = String.valueOf(neighborhoodIdLong);

        UriBuilder neighborhoodsUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS);
        UriBuilder self = neighborhoodsUri.clone().path(neighborhoodId);
        UriBuilder channelsUri = self.clone().path(Endpoint.CHANNELS);
        UriBuilder eventsUri = self.clone().path(Endpoint.EVENTS);
        UriBuilder postsUri = self.clone().path(Endpoint.POSTS);
        UriBuilder postsCountUri = self.clone().path(Endpoint.POSTS).path(Endpoint.COUNT);
        UriBuilder usersUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.USERS).queryParam(QueryParameter.IN_NEIGHBORHOOD, self.build());

        links.setSelf(self.build());
        links.setChannels(channelsUri.build());
        links.setEvents(eventsUri.build());
        links.setPosts(postsUri.build());
        links.setPostsCount(postsCountUri.build());
        links.setUsers(usersUri.build());

        if (!BaseNeighborhood.isABaseNeighborhood(neighborhoodIdLong)) {
            UriBuilder amenitiesUri = self.clone().path(Endpoint.AMENITIES);
            UriBuilder attendanceUri = self.clone().path(Endpoint.ATTENDANCE);
            UriBuilder contactsUri = self.clone().path(Endpoint.CONTACTS);
            UriBuilder bookingsUri = self.clone().path(Endpoint.BOOKINGS);
            UriBuilder likesUri = self.clone().path(Endpoint.LIKES);
            UriBuilder productsUri = self.clone().path(Endpoint.PRODUCTS);
            UriBuilder requestsUri = self.clone().path(Endpoint.REQUESTS);
            UriBuilder resourcesUri = self.clone().path(Endpoint.RESOURCES);
            UriBuilder tagsUri = self.clone().path(Endpoint.TAGS);
            UriBuilder workersUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.WORKERS);

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

            links.setFeed(postsUri.clone().queryParam(QueryParameter.IN_CHANNEL, feedChannelUri.clone().build()).build());
            links.setComplaints(postsUri.clone().queryParam(QueryParameter.IN_CHANNEL, complaintsChannelUri.clone().build()).build());
            links.setAnnouncements(postsUri.clone().queryParam(QueryParameter.IN_CHANNEL, announcementsChannelUri.clone().build()).build());

            links.setWorkers(workersUri.queryParam(QueryParameter.IN_NEIGHBORHOOD, links.getSelf()).build());
        } else if (neighborhoodIdLong == BaseNeighborhood.WORKERS.getId()){
            UriBuilder workersChannelUri = channelsUri.clone().path(String.valueOf(BaseChannel.WORKERS.getId()));
            links.setWorkerChannel(workersChannelUri.build());

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

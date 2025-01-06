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

        String neighborhoodId = String.valueOf(neighborhood.getNeighborhoodId());

        // Templating Test
        links.setPosts2("http://localhost:8080/neighborhoods/1/posts{?postedBy,inChannel,withTags*,withStatus,page,size}");

        UriBuilder neighborhoodsUri = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS.toString());
        UriBuilder self = neighborhoodsUri.clone().path(neighborhoodId);
        UriBuilder channelsUri = self.clone().path(Endpoint.CHANNELS.toString());
        UriBuilder eventsUri = self.clone().path(Endpoint.EVENTS.toString());
        UriBuilder postsUri = self.clone().path(Endpoint.POSTS.toString());
        UriBuilder postsCountUri = self.clone().path(Endpoint.POSTS.toString()).path(Endpoint.COUNT.toString());
        UriBuilder usersUri = self.clone().path(Endpoint.USERS.toString());

        links.setSelf(self.build());
        links.setChannels(channelsUri.build());
        links.setEvents(eventsUri.build());
        links.setPosts(postsUri.build());
        links.setPostsCount(postsCountUri.build());
        links.setUsers(usersUri.build());

        if (!BaseNeighborhood.isABaseNeighborhood(neighborhood.getNeighborhoodId())) {
            UriBuilder amenitiesUri = self.clone().path(Endpoint.AMENITIES.toString());
            UriBuilder attendanceUri = self.clone().path(Endpoint.ATTENDANCE.toString());
            UriBuilder contactsUri = self.clone().path(Endpoint.CONTACTS.toString());
            UriBuilder bookingsUri = self.clone().path(Endpoint.BOOKINGS.toString());
            UriBuilder likesUri = self.clone().path(Endpoint.LIKES.toString()); // THIS SHOULD BE USED INSTEAD, this includes neighborhoods!
            UriBuilder wrongLikesUri = uriInfo.getBaseUriBuilder().path(Endpoint.LIKES.toString()); // THIS SHOULD BE USED INSTEAD
            UriBuilder productsUri = self.clone().path(Endpoint.PRODUCTS.toString());
            UriBuilder requestsUri = self.clone().path(Endpoint.REQUESTS.toString());
            UriBuilder resourcesUri = self.clone().path(Endpoint.RESOURCES.toString());
            UriBuilder tagsUri = self.clone().path(Endpoint.TAGS.toString());
            UriBuilder workersUri = uriInfo.getBaseUriBuilder().path(Endpoint.WORKERS.toString());

            UriBuilder feedChannelUri = channelsUri.clone().path(String.valueOf(BaseChannel.FEED.getId()));
            UriBuilder complaintsChannelUri = channelsUri.clone().path(String.valueOf(BaseChannel.COMPLAINTS.getId()));
            UriBuilder announcementsChannelUri = channelsUri.clone().path(String.valueOf(BaseChannel.ANNOUNCEMENTS.getId()));

            links.setAmenities(amenitiesUri.build());
            links.setAttendance(attendanceUri.build());
            links.setContacts(contactsUri.build());
            links.setBookings(bookingsUri.build());
            links.setLikes(wrongLikesUri.build());
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

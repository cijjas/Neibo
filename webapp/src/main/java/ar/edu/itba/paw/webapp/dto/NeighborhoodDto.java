package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.BaseChannel;
import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
        URI self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhood.getNeighborhoodId()))
                .build();
        links.setSelf(self);
        links.setUsers(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhood.getNeighborhoodId()))
                .path("users")
                .build());
        links.setWorkers(uriInfo.getBaseUriBuilder()
                .path("workers")
                .queryParam("inNeighborhoods", self)
                .build());
        links.setChannels(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhood.getNeighborhoodId()))
                .path("channels")
                .build());

        if (neighborhood.getNeighborhoodId() > 0) {
            links.setContacts(uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(neighborhood.getNeighborhoodId()))
                    .path("contacts")
                    .build());
            links.setPosts(uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(neighborhood.getNeighborhoodId()))
                    .path("posts")
                    .build());
            links.setAnnouncements(uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(neighborhood.getNeighborhoodId()))
                    .path("posts")
                            .queryParam("inChannel",
                                    uriInfo.getBaseUriBuilder()
                                        .path("neighborhoods")
                                        .path(String.valueOf(neighborhood.getNeighborhoodId()))
                                            .path("channels")
                                            .path(String.valueOf(BaseChannel.ANNOUNCEMENTS.getId())))
                    .build());
            links.setComplaints(uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(neighborhood.getNeighborhoodId()))
                    .path("posts")
                    .queryParam("inChannel",
                            uriInfo.getBaseUriBuilder()
                                    .path("neighborhoods")
                                    .path(String.valueOf(neighborhood.getNeighborhoodId()))
                                    .path("channels")
                                    .path(String.valueOf(BaseChannel.COMPLAINTS.getId())))
                    .build());
            links.setFeed(uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(neighborhood.getNeighborhoodId()))
                    .path("posts")
                    .queryParam("inChannel",
                            uriInfo.getBaseUriBuilder()
                                    .path("neighborhoods")
                                    .path(String.valueOf(neighborhood.getNeighborhoodId()))
                                    .path("channels")
                                    .path(String.valueOf(BaseChannel.FEED.getId())))
                    .build());
            links.setFeedChannel(
                    uriInfo.getBaseUriBuilder()
                            .path("neighborhoods")
                            .path(String.valueOf(neighborhood.getNeighborhoodId()))
                            .path("channels")
                            .path(String.valueOf(BaseChannel.FEED.getId()))
                    .build()
            );
            links.setComplaintsChannel(
                    uriInfo.getBaseUriBuilder()
                            .path("neighborhoods")
                            .path(String.valueOf(neighborhood.getNeighborhoodId()))
                            .path("channels")
                            .path(String.valueOf(BaseChannel.COMPLAINTS.getId()))
                            .build()
            );
            links.setAnnouncementsChannel(
                    uriInfo.getBaseUriBuilder()
                            .path("neighborhoods")
                            .path(String.valueOf(neighborhood.getNeighborhoodId()))
                            .path("channels")
                            .path(String.valueOf(BaseChannel.ANNOUNCEMENTS.getId()))
                            .build()
            );
            links.setHotPostStatus(
                    uriInfo.getBaseUriBuilder()
                            .path("post-statuses")
                            .path(String.valueOf(PostStatus.HOT.getId()))
                            .build()
            );
            links.setTrendingPostStatus(
                    uriInfo.getBaseUriBuilder()
                            .path("post-statuses")
                            .path(String.valueOf(PostStatus.TRENDING.getId()))
                            .build()
            );
            links.setNonePostStatus(
                    uriInfo.getBaseUriBuilder()
                            .path("post-statuses")
                            .path(String.valueOf(PostStatus.NONE.getId()))
                            .build()
            );
            links.setEvents(uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(neighborhood.getNeighborhoodId()))
                    .path("events")
                    .build());
            links.setResources(uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(neighborhood.getNeighborhoodId()))
                    .path("resources")
                    .build());
            links.setTags(uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(neighborhood.getNeighborhoodId()))
                    .path("tags")
                    .build());
            links.setImages(uriInfo.getBaseUriBuilder()
                    .path("images")
                    .build());
            links.setLikes(uriInfo.getBaseUriBuilder()
                    .path("likes")
                    .build());
            links.setLanguageEnglish(
                    uriInfo.getBaseUriBuilder()
                    .path("languages")
                    .path(String.valueOf(Language.ENGLISH.getId()))
                    .build()
            );
            links.setLanguageSpanish(
                    uriInfo.getBaseUriBuilder()
                            .path("languages")
                            .path(String.valueOf(Language.SPANISH.getId()))
                            .build()
            );
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

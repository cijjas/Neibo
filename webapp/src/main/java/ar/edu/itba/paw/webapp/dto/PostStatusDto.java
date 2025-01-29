package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class PostStatusDto {

    private PostStatus status;

    private Links _links;

    public static PostStatusDto fromPostStatus(PostStatus postStatus, UriInfo uriInfo) {
        final PostStatusDto dto = new PostStatusDto();

        dto.status = postStatus;

        Links links = new Links();

        String postStatusId = String.valueOf(postStatus.getId());

        UriBuilder postStatusUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.POST_STATUSES).path(postStatusId);

        links.setSelf(postStatusUri.build());

        dto.set_links(links);
        return dto;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}

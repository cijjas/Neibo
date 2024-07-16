package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.PostStatus;

import javax.ws.rs.core.UriInfo;

public class PostStatusDto {

    private PostStatus postStatus;
    private Links _links;

    public static PostStatusDto fromPostStatus(PostStatus postStatus, UriInfo uriInfo) {
        final PostStatusDto dto = new PostStatusDto();

        dto.postStatus = postStatus;

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("post-statuses")
                .path(String.valueOf(postStatus.getId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public PostStatus getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}

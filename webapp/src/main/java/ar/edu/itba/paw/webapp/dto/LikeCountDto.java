package ar.edu.itba.paw.webapp.dto;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class LikeCountDto {

    private int likeCount;
    private Links _links;

    public static LikeCountDto fromLikeCount(int likeCount, String postURN, String userURN, UriInfo uriInfo) {
        final LikeCountDto dto = new LikeCountDto();

        dto.likeCount = likeCount;

        Links links = new Links();

        UriBuilder uriBuilder = uriInfo.getBaseUriBuilder()
                .path("likes")
                .path("count");

        if (postURN != null && userURN != null) {
            uriBuilder
                    .queryParam("postId", postURN)
                    .queryParam("userId", userURN);
        } else if (postURN != null) {
            uriBuilder
                    .queryParam("postId", postURN);
        } else if (userURN != null) {
            uriBuilder
                    .queryParam("userId", userURN);
        }

        links.setSelf(uriBuilder.build());

        dto.set_links(links);
        return dto;
    }


    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}

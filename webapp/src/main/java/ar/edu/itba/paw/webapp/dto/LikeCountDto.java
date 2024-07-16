package ar.edu.itba.paw.webapp.dto;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class LikeCountDto {

    private int likeCount;
    private Links _links;

    public static LikeCountDto fromLikeCount(int likeCount, Long postId, Long userId, long neighborhoodId, UriInfo uriInfo) {
        final LikeCountDto dto = new LikeCountDto();

        dto.likeCount = likeCount;

        Links links = new Links();

        UriBuilder uriBuilder = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhoodId))
                .path("likes")
                .path("count");

        if (postId != null && userId != null) {
            uriBuilder
                    .queryParam("postId", uriInfo.getBaseUriBuilder()
                            .path("neighborhoods")
                            .path(String.valueOf(neighborhoodId))
                            .path("posts")
                            .path(String.valueOf(postId))
                            .build())
                    .queryParam("userId", uriInfo.getBaseUriBuilder()
                            .path("neighborhoods")
                            .path(String.valueOf(neighborhoodId))
                            .path("users")
                            .path(String.valueOf(userId))
                            .build());
        } else if (postId != null) {
            uriBuilder
                    .queryParam("postId", uriInfo.getBaseUriBuilder()
                            .path("neighborhoods")
                            .path(String.valueOf(neighborhoodId))
                            .path("posts")
                            .path(String.valueOf(postId))
                            .build());
        } else if (userId != null) {
            uriBuilder
                    .queryParam("userId", "userId", uriInfo.getBaseUriBuilder()
                            .path("neighborhoods")
                            .path(String.valueOf(neighborhoodId))
                            .path("users")
                            .path(String.valueOf(userId))
                            .build());
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

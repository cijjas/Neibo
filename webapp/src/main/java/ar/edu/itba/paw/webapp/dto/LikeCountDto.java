package ar.edu.itba.paw.webapp.dto;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class LikeCountDto {

    private int likeCount;

    private URI self;

    public static LikeCountDto fromLikeCount(int likeCount, Long postId, Long userId, long neighborhoodId, UriInfo uriInfo) {
        final LikeCountDto dto = new LikeCountDto();

        dto.likeCount = likeCount;

        UriBuilder uriBuilder = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhoodId))
                .path("likes")
                .path("count");

        if (postId != null && userId != null) {
            ((UriBuilder) uriBuilder).queryParam("postId", postId)
                    .queryParam("userId", userId);
        } else if (postId != null) {
            uriBuilder.queryParam("postId", postId);
        } else if (userId != null) {
            uriBuilder.queryParam("userId", userId);
        }

        dto.self = uriBuilder.build();

        return dto;
    }


    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}

package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class LikeCountDto {

    private int count;

    private Links _links;

    public static LikeCountDto fromLikeCount(int likeCount, long neighborhoodIdLong, String postURI, String userURI, UriInfo uriInfo) {
        final LikeCountDto dto = new LikeCountDto();

        dto.count = likeCount;

        Links links = new Links();

        String neighborhoodId = String.valueOf(neighborhoodIdLong);

        UriBuilder self = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS).path(neighborhoodId).path(Endpoint.LIKES).path(Endpoint.COUNT);

        if (postURI != null && !postURI.isEmpty())
            self.queryParam(QueryParameter.ON_POST, postURI);
        if (userURI != null && !userURI.isEmpty())
            self.queryParam(QueryParameter.LIKED_BY, userURI);

        links.setSelf(self.build());

        dto.set_links(links);
        return dto;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}

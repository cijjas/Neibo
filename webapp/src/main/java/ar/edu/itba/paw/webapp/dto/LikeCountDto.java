package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.Endpoint;
import ar.edu.itba.paw.webapp.controller.QueryParameters;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class LikeCountDto {

    private int count;

    private Links _links;

    public static LikeCountDto fromLikeCount(int likeCount, String postURN, String userURN, UriInfo uriInfo) {
        final LikeCountDto dto = new LikeCountDto();

        dto.count = likeCount;

        Links links = new Links();

        UriBuilder self = uriInfo.getBaseUriBuilder().path(Endpoint.LIKES.toString()).path(Endpoint.COUNT.toString());

        if (postURN != null)
            self.queryParam(QueryParameters.ON_POST, postURN);
        if (userURN != null)
            self.queryParam(QueryParameters.LIKED_BY, userURN);

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

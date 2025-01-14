package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class LikeCountDto {

    private int count;

    private Links _links;

    public static LikeCountDto fromLikeCount(int likeCount, String postURN, String userURN, UriInfo uriInfo) {
        final LikeCountDto dto = new LikeCountDto();

        dto.count = likeCount;

        Links links = new Links();

        UriBuilder self = uriInfo.getBaseUriBuilder().path(Endpoint.LIKES).path(Endpoint.COUNT);

        if (postURN != null)
            self.queryParam(QueryParameter.ON_POST, postURN);
        if (userURN != null)
            self.queryParam(QueryParameter.LIKED_BY, userURN);

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

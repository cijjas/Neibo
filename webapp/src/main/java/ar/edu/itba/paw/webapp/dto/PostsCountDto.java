package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;

public class PostsCountDto {

    private int count;

    private Links _links;

    public static PostsCountDto fromPostsCount(int postCount, long neighborhoodIdLong, String userURI, String channelURI, List<String> tagURIList, String postStatusURI, UriInfo uriInfo) {
        final PostsCountDto dto = new PostsCountDto();

        dto.count = postCount;

        Links links = new Links();

        String neighborhoodId = String.valueOf(neighborhoodIdLong);

        UriBuilder self = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS).path(neighborhoodId).path(Endpoint.POSTS).path(Endpoint.COUNT);

        if (userURI != null)
            self.queryParam(QueryParameter.POSTED_BY, userURI);
        if (channelURI != null)
            self.queryParam(QueryParameter.IN_CHANNEL, channelURI);
        if (tagURIList != null && !tagURIList.isEmpty())
            for (String tag : tagURIList)
                self.queryParam(QueryParameter.WITH_TAG, tag);
        if (postStatusURI != null)
            self.queryParam(QueryParameter.WITH_STATUS, postStatusURI);

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

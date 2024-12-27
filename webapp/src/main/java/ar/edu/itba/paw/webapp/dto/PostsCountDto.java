package ar.edu.itba.paw.webapp.dto;

import javax.ws.rs.core.UriInfo;

public class PostsCountDto {

    private int count;

    private Links _links;

    public static PostsCountDto fromPostsCount(int postCount, long neighborhoodId, UriInfo uriInfo) {
        final PostsCountDto dto = new PostsCountDto();

        dto.count = postCount;

        Links links = new Links();

        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhoodId))
                .path("posts")
                .path("count")
                .build());

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

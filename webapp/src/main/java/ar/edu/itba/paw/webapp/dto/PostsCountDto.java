package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.Endpoint;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;

public class PostsCountDto {

    private int count;

    private Links _links;

    public static PostsCountDto fromPostsCount(int postCount, long neighborhoodIdLong, String userURI, String channelUri, List<String> tagUriList, String postStatusUri, UriInfo uriInfo) {
        final PostsCountDto dto = new PostsCountDto();

        dto.count = postCount;

        Links links = new Links();

        String neighborhoodId = String.valueOf(neighborhoodIdLong);

        /*
        * This should be receiving the 4 query params that the original method is receiving
        * is .queryParam smart enough to not put the null, gotta check that out, would save some conditionals
        * Como se handlea la lista, please no me digas que tengo que hacer un for!
        * */
        // UriBuilder postsCountUri = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS.toString()).path(neighborhoodId).path(Endpoint.POSTS.toString()).path(Endpoint.COUNT);

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

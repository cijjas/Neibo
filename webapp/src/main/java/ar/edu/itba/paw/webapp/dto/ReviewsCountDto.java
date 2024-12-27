package ar.edu.itba.paw.webapp.dto;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class ReviewsCountDto {

    private int count;

    private Links _links;

    public static ReviewsCountDto fromReviewsCount(int reviewsCount, long workerId, UriInfo uriInfo) {
        final ReviewsCountDto dto = new ReviewsCountDto();

        dto.count = reviewsCount;

        Links links = new Links();

        UriBuilder uriBuilder = uriInfo.getBaseUriBuilder()
                .path("workers")
                .path(String.valueOf(workerId))
                .path("reviews")
                .path("count");

        links.setSelf(uriBuilder.build());

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

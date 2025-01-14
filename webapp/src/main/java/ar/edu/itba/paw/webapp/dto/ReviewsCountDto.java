package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.controller.constants.Endpoint;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class ReviewsCountDto {

    private int count;

    private Links _links;

    public static ReviewsCountDto fromReviewsCount(int reviewsCount, long workerIdLong, UriInfo uriInfo) {
        final ReviewsCountDto dto = new ReviewsCountDto();

        dto.count = reviewsCount;

        Links links = new Links();

        String workerId = String.valueOf(workerIdLong);

        UriBuilder workerReviewCountUri = uriInfo.getBaseUriBuilder().path(Endpoint.WORKERS).path(workerId).path(Endpoint.REVIEWS).path(Endpoint.COUNT);

        links.setSelf(workerReviewCountUri.build());

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

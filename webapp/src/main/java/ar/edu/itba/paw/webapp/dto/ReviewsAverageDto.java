package ar.edu.itba.paw.webapp.dto;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class ReviewsAverageDto {

    private float average;

    private Links _links;

    public static ReviewsAverageDto fromReviewAverage(float average, long workerId, UriInfo uriInfo) {
        final ReviewsAverageDto dto = new ReviewsAverageDto();

        dto.average = average;

        Links links = new Links();

        UriBuilder uriBuilder = uriInfo.getBaseUriBuilder()
                .path("workers")
                .path(String.valueOf(workerId))
                .path("reviews")
                .path("average");

        links.setSelf(uriBuilder.build());

        dto.set_links(links);
        return dto;
    }

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}

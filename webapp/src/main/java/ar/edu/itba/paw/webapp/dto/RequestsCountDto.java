package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.Endpoint;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class RequestsCountDto {

    private int count;

    private Links _links;

    public static RequestsCountDto fromRequestsCount(int requestsCount, long neighborhoodIdLong, UriInfo uriInfo) {
        final RequestsCountDto dto = new RequestsCountDto();

        dto.count = requestsCount;

        Links links = new Links();
/*
* Also missing the many query params needed to define a count
*
* */
        String neighborhoodId = String.valueOf(neighborhoodIdLong);

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS.toString()).path(neighborhoodId);

        UriBuilder uriBuilder = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhoodId))
                .path("requests")
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

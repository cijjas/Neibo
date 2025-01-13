package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.Endpoint;
import ar.edu.itba.paw.webapp.controller.QueryParameters;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class RequestsCountDto {

    private int count;

    private Links _links;

    public static RequestsCountDto fromRequestsCount(int requestsCount, long neighborhoodIdLong, String userURI, String productURI, String requestStatusURI, String  requestTypeURI, UriInfo uriInfo) {
        final RequestsCountDto dto = new RequestsCountDto();

        dto.count = requestsCount;

        Links links = new Links();

        String neighborhoodId = String.valueOf(neighborhoodIdLong);

        UriBuilder self = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS.toString()).path(neighborhoodId).path(Endpoint.REQUESTS.toString()).path(Endpoint.COUNT.toString());

        if (userURI != null)
            self.queryParam(QueryParameters.REQUESTED_BY, userURI);
        if (productURI != null)
            self.queryParam(QueryParameters.FOR_PRODUCT, productURI);
        if (requestStatusURI != null)
            self.queryParam(QueryParameters.WITH_STATUS, requestStatusURI);
        if (requestTypeURI != null)
            self.queryParam(QueryParameters.WITH_TYPE, requestTypeURI);

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

package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class RequestsCountDto {

    private int count;

    private Links _links;

    public static RequestsCountDto fromRequestsCount(int requestsCount, long neighborhoodIdLong, String userURI, String productURI, String requestStatusURI, String requestTypeURI, UriInfo uriInfo) {
        final RequestsCountDto dto = new RequestsCountDto();

        dto.count = requestsCount;

        Links links = new Links();

        String neighborhoodId = String.valueOf(neighborhoodIdLong);

        UriBuilder self = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS).path(neighborhoodId).path(Endpoint.REQUESTS).path(Endpoint.COUNT);

        if (userURI != null)
            self.queryParam(QueryParameter.REQUESTED_BY, userURI);
        if (productURI != null)
            self.queryParam(QueryParameter.FOR_PRODUCT, productURI);
        if (requestStatusURI != null)
            self.queryParam(QueryParameter.WITH_STATUS, requestStatusURI);
        if (requestTypeURI != null)
            self.queryParam(QueryParameter.WITH_TYPE, requestTypeURI);

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

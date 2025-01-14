package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.List;

@Component
public class ControllerUtils {


    public static Link[] createPaginationLinks(UriBuilder baseUriBuilder, int totalPages, int page, int size) {
        List<Link> links = new ArrayList<>();

        // Self link
        links.add(Link.fromUri(baseUriBuilder.clone()
                        .queryParam(QueryParameter.PAGE, page)
                        .queryParam(QueryParameter.SIZE, size)
                        .build())
                .rel("self").build());

        // First page link
        links.add(Link.fromUri(baseUriBuilder.clone()
                        .queryParam(QueryParameter.PAGE, 1)
                        .queryParam(QueryParameter.SIZE, size)
                        .build())
                .rel("first").build());

        // Last page link
        links.add(Link.fromUri(baseUriBuilder.clone()
                        .queryParam(QueryParameter.PAGE, totalPages)
                        .queryParam(QueryParameter.SIZE, size)
                        .build())
                .rel("last").build());

        // Previous page link
        if (page > 1) {
            links.add(Link.fromUri(baseUriBuilder.clone()
                            .queryParam(QueryParameter.PAGE, page - 1)
                            .queryParam(QueryParameter.SIZE, size)
                            .build())
                    .rel("prev").build());
        }

        // Next page link
        if (page < totalPages) {
            links.add(Link.fromUri(baseUriBuilder.clone()
                            .queryParam(QueryParameter.PAGE, page + 1)
                            .queryParam(QueryParameter.SIZE, size)
                            .build())
                    .rel("next").build());
        }

        return links.toArray(new Link[0]);
    }
}

package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Link;
import java.util.ArrayList;
import java.util.List;

@Component
public class ControllerUtils {
    public static int MAX_AGE_SECONDS = 10800;
    @Autowired
    private UserService us;

    public static Link[] createPaginationLinks(String baseUri, int totalPages, int page, int size) {
        List<Link> links = new ArrayList<>();

        // Self link
        links.add(Link.fromUri(baseUri + "?page=" + page + "&size=" + size)
                .rel("self").build());

        // First page link
        links.add(Link.fromUri(baseUri + "?page=1&size=" + size)
                .rel("first").build());

        // Last page link
        links.add(Link.fromUri(baseUri + "?page=" + totalPages + "&size=" + size)
                .rel("last").build());

        // Previous page link
        if (page > 1) {
            links.add(Link.fromUri(baseUri + "?page=" + (page - 1) + "&size=" + size)
                    .rel("prev").build());
        }

        // Next page link
        if (page < totalPages) {
            links.add(Link.fromUri(baseUri + "?page=" + (page + 1) + "&size=" + size)
                    .rel("next").build());
        }

        return links.toArray(new Link[0]);
    }
}

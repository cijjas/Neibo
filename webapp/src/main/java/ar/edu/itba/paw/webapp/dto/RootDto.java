package ar.edu.itba.paw.webapp.dto;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class RootDto {
    private RootLinks _links;


    public static RootDto createRootDto(UriInfo uriInfo) {
        final RootDto dto = new RootDto();


        RootLinks rootLinks= new RootLinks();

        String self = uriInfo.getBaseUriBuilder().build().toString();

        rootLinks.setSelf(self);



        dto.set_links(rootLinks);
        return dto;
    }

    public RootLinks get_links() {
        return _links;
    }

    public void set_links(RootLinks _links) {
        this._links = _links;
    }
}

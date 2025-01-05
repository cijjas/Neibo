package ar.edu.itba.paw.webapp.dto;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class RootDto {
    private RootLinks _links;


    public static RootDto createRootDto(UriInfo uriInfo) {
        final RootDto dto = new RootDto();

        RootLinks rootLinks= new RootLinks();

        // This should be filled up with the first layer URLs

        String self = uriInfo.getBaseUriBuilder().build().toString();
        UriBuilder builder = uriInfo.getBaseUriBuilder().path("neighborhoods").path("0").path("posts");
        String uriTemplate = builder + "{?postedBy,inChannel,withTags,withStatus,page,size}";
        rootLinks.setWorkerPosts(uriTemplate);
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

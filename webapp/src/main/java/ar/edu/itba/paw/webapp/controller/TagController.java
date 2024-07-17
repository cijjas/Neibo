package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.models.Entities.Tag;
import ar.edu.itba.paw.webapp.dto.ResourceDto;
import ar.edu.itba.paw.webapp.dto.TagDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.text.html.parser.Entity;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.*;

/*
 * # Summary
 *   - A Post can have many Tags and a Tag can be utilized in many Posts
 *
 * # Use cases
 *   - A User creates a Tag through the Post creation
 *   - A User/Admin filters the Posts through a Tag
 */

@Path("neighborhoods/{neighborhoodId}/tags")
@Component
public class TagController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagController.class);

    @Autowired
    private TagService ts;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response listTags(
            @QueryParam("postId") final String postURN,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/tags'", neighborhoodId);

        // Content
        List<Tag> tags = ts.getTags(postURN, neighborhoodId, page, size);
        String tagsHashCode = String.valueOf(tags.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(tagsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (tags.isEmpty())
            return Response.noContent()
                    .tag(tagsHashCode)
                    .build();

        List<TagDto> tagsDto = tags.stream()
                .map(t -> TagDto.fromTag(t, neighborhoodId, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = ControllerUtils.createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/tags",
                ts.calculateTagPages(postURN, neighborhoodId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<TagDto>>(tagsDto){})
                .cacheControl(cacheControl)
                .tag(tagsHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findTags(
            @PathParam("id") final long tagId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/tags/{}'", neighborhoodId, tagId);

        // Content
        Tag tag = ts.findTag(tagId, neighborhoodId).orElseThrow(() -> new NotFoundException("Tag not found"));
        String tagHashCode = String.valueOf(tag.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(tagHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(TagDto.fromTag(tag, neighborhoodId, uriInfo))
                .cacheControl(cacheControl)
                .tag(tagHashCode)
                .build();
    }
}

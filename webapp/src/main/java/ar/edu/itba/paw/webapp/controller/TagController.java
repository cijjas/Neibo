package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.models.Entities.Tag;
import ar.edu.itba.paw.webapp.dto.TagDto;
import ar.edu.itba.paw.webapp.validation.constraints.urn.PostURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractOptionalSecondId;


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
@Validated
@Produces(value = {MediaType.APPLICATION_JSON})
public class TagController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final TagService ts;

    @Autowired
    public TagController(TagService ts) {
        this.ts = ts;
    }

    @GET
    public Response listTags(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint long neighborhoodId,
            @QueryParam("onPost") @PostURNConstraint String post,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/tags'", neighborhoodId);

        // ID Extraction
        Long postId = extractOptionalSecondId(post);

        // Content
        List<Tag> tags = ts.getTags(neighborhoodId, postId, page, size);
        String tagsHashCode = String.valueOf(tags.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
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
                ts.calculateTagPages(neighborhoodId, postId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<TagDto>>(tagsDto) {
                })
                .cacheControl(cacheControl)
                .tag(tagsHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("/{tagId}")
    public Response findTag(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint long neighborhoodId,
            @PathParam("tagId") @GenericIdConstraint long tagId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/tags/{}'", neighborhoodId, tagId);

        // Content
        Tag tag = ts.findTag(neighborhoodId, tagId).orElseThrow(NotFoundException::new);
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

    @POST
    @Validated(CreateValidationSequence.class)
    public Response createTag(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint long neighborhoodId,
            @Valid @NotNull TagDto createForm
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/'");

        // Creation & HashCode Generation
        final Tag tag = ts.createTag(neighborhoodId, createForm.getName());
        String tagHashCode = String.valueOf(tag.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(tag.getTagId())).build();

        // Cache Control
        CacheControl cacheControl = new CacheControl();

        return Response.created(uri)
                .cacheControl(cacheControl)
                .tag(tagHashCode)
                .build();
    }

    @DELETE
    @Path("/{tagId}")
    @Secured("ROLE_SUPER_ADMINISTRATOR")
    public Response deleteTag(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint long neighborhoodId,
            @PathParam("tagId") @GenericIdConstraint long tagId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/tags/{}'", neighborhoodId, tagId);

        // Deletion Attempt
        if (ts.deleteTag(neighborhoodId, tagId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}

package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.models.Entities.Tag;
import ar.edu.itba.paw.webapp.controller.constants.*;
import ar.edu.itba.paw.webapp.dto.TagDto;
import ar.edu.itba.paw.webapp.dto.queryForms.TagParams;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractNullableSecondId;

/*
 * # Summary
 *   - A Post can have many Tags and a Tag can be utilized in many Posts
 *
 * # Use cases
 *   - A Neighbor/Admin can create a Tag
 *   - A Neighbor/Admin filters the Posts through a Tag
 */

@Path(Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.TAGS)
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON})
public class TagController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagController.class);
    private final TagService ts;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public TagController(TagService ts) {
        this.ts = ts;
    }

    @GET
    @PreAuthorize("@accessControlHelper.canListTags(#tagParams.post)")
    public Response listTags(
            @Valid @BeanParam TagParams tagParams
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // ID Extraction
        Long postId = extractNullableSecondId(tagParams.getPost());

        // Content
        List<Tag> tags = ts.getTags(tagParams.getNeighborhoodId(), postId, tagParams.getPage(), tagParams.getSize());
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
                .map(t -> TagDto.fromTag(t, tagParams.getNeighborhoodId(), uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = ControllerUtils.createPaginationLinks(
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(String.valueOf(tagParams.getNeighborhoodId())).path(Endpoint.TAGS),
                ts.calculateTagPages(tagParams.getNeighborhoodId(), postId, tagParams.getSize()),
                tagParams.getPage(),
                tagParams.getSize()
        );

        return Response.ok(new GenericEntity<List<TagDto>>(tagsDto) {
                })
                .cacheControl(cacheControl)
                .tag(tagsHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("{" + PathParameter.TAG_ID + "}")
    public Response findTag(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.TAG_ID) long tagId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

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
    @Validated(CreateSequence.class)
    public Response createTag(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @Valid @NotNull TagDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Creation & HashCode Generation
        final Tag tag = ts.createTag(neighborhoodId, createForm.getName());
        String tagHashCode = String.valueOf(tag.hashCode());

        // Resource URI
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(tag.getTagId())).build();

        // Cache Control
        CacheControl cacheControl = new CacheControl();

        return Response.created(uri)
                .cacheControl(cacheControl)
                .tag(tagHashCode)
                .build();
    }

    @DELETE
    @Path("{" + PathParameter.TAG_ID + "}")
    public Response deleteTag(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.TAG_ID) long tagId
    ) {
        LOGGER.info("DELETE request arrived at '{}'", uriInfo.getRequestUri());

        // Deletion Attempt
        if (ts.deleteTag(neighborhoodId, tagId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}

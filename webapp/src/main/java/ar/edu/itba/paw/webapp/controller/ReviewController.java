package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ReviewService;
import ar.edu.itba.paw.models.Entities.Review;
import ar.edu.itba.paw.webapp.dto.ReviewDto;
import ar.edu.itba.paw.webapp.form.ReviewForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.controller.ETagUtility.*;

/*
 * # Summary
 *   - A Worker has many Reviews
 *
 * # Use cases
 *   - A User can leave a Review for a certain Worker
 *   - A User/Admin/Worker can list the Reviews of the Workers and their overall score
 */

@Path("workers/{workerId}/reviews")
@Component
public class ReviewController extends GlobalControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    private ReviewService rs;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @PathParam("workerId")
    private Long workerId;

    private EntityTag entityLevelETag = ETagUtility.generateETag();


    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listReviews(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/workers/{}/reviews'", workerId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        final List<Review> reviews = rs.getReviews(workerId, page, size);
        if (reviews.isEmpty())
            return Response.noContent()
                    .tag(entityLevelETag)
                    .build();
        final List<ReviewDto> reviewsDto = reviews.stream()
                .map(r -> ReviewDto.fromReview(r, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "workers/" + workerId + "/reviews",
                rs.calculateReviewPages(workerId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<ReviewDto>>(reviewsDto){})
                .links(links)
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findReview(
            @PathParam("id") final long id,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag clientETag
    ) {
        LOGGER.info("GET request arrived at '/workers/{}/reviews/{}'", workerId, id);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(Long.toString(id));
        Response response = checkMutableETagPreconditions(clientETag, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Content
        ReviewDto reviewDto = ReviewDto.fromReview(rs.findReview(id, workerId).orElseThrow(NotFoundException::new), uriInfo);

        return Response.ok(reviewDto)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }

    @POST
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_NEIGHBOR"})
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createReview(
            @Valid @NotNull final ReviewForm form
    ) {
        LOGGER.info("POST request arrived at '/workers/{}/reviews'", workerId);

        // Cache Control
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .tag(entityLevelETag)
                    .build();

        // Creation & ETag Generation
        final Review review = rs.createReview(workerId, getRequestingUserId(), form.getRating(), form.getReview());
        entityLevelETag = ETagUtility.generateETag();
        EntityTag rowLevelETag = new EntityTag(review.getReviewId().toString());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(review.getReviewId())).build();

        return Response.created(uri)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(
            @PathParam("id") final long reviewId,
            @HeaderParam(HttpHeaders.IF_MATCH) EntityTag ifMatch
    ) {
        LOGGER.info("DELETE request arrived at '/workers/{}/reviews/{}'", workerId, reviewId);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(String.valueOf(reviewId));
        Response response = checkModificationETagPreconditions(ifMatch, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Deletion & ETag Generation Attempt
        if(rs.deleteReview(reviewId)) {
            entityLevelETag = ETagUtility.generateETag();
            return Response.noContent()
                    .tag(entityLevelETag)
                    .build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }
}

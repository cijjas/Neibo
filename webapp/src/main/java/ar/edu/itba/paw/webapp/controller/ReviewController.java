package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ReviewService;
import ar.edu.itba.paw.models.Entities.Review;
import ar.edu.itba.paw.webapp.auth.PathAccessControlHelper;
import ar.edu.itba.paw.webapp.dto.ReviewDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.WorkerIdConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractSecondId;

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
@Validated
@Produces(value = {MediaType.APPLICATION_JSON,})
public class ReviewController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final ReviewService rs;

    @Autowired
    public ReviewController(ReviewService rs) {
        this.rs = rs;
    }

    @GET
    public Response listReviews(
            @PathParam("workerId") @WorkerIdConstraint final long workerId,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/workers/{}/reviews'", workerId);

        // Content
        final List<Review> reviews = rs.getReviews(workerId, page, size);
        String reviewsHashCode = String.valueOf(reviews.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(reviewsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (reviews.isEmpty())
            return Response.noContent()
                    .tag(reviewsHashCode)
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

        return Response.ok(new GenericEntity<List<ReviewDto>>(reviewsDto) {
                })
                .links(links)
                .cacheControl(cacheControl)
                .tag(reviewsHashCode)
                .build();
    }

    @GET
    @Path("/{id}")
    public Response findReview(
            @PathParam("workerId") @WorkerIdConstraint final long workerId,
            @PathParam("id") @GenericIdConstraint final long id
    ) {
        LOGGER.info("GET request arrived at '/workers/{}/reviews/{}'", workerId, id);

        // Content
        Review review = rs.findReview(id, workerId).orElseThrow(NotFoundException::new);
        String reviewHashCode = String.valueOf(review.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(reviewHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(ReviewDto.fromReview(review, uriInfo))
                .cacheControl(cacheControl)
                .tag(reviewHashCode)
                .build();
    }

    @POST
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_NEIGHBOR", "ROLE_SUPER_ADMINISTRATOR"})
    @Validated(CreateValidationSequence.class)
    @PreAuthorize("@pathAccessControlHelper.canCreateReview(#workerId, #form.user)")
    public Response createReview(
            @PathParam("workerId") @WorkerIdConstraint final long workerId,
            @Valid ReviewDto form
    ) {
        LOGGER.info("POST request arrived at '/workers/{}/reviews'", workerId);

        // Creation & HashCode Generation
        final Review review = rs.createReview(workerId, extractSecondId(form.getUser()), form.getRating(), form.getReview());
        String reviewHashCode = String.valueOf(review.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(review.getReviewId())).build();

        return Response.created(uri)
                .tag(reviewHashCode)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Secured("ROLE_SUPER_ADMINISTRATOR")
    public Response deleteById(
            @PathParam("workerId") @WorkerIdConstraint final long workerId,
            @PathParam("id") @GenericIdConstraint final long reviewId
    ) {
        LOGGER.info("DELETE request arrived at '/workers/{}/reviews/{}'", workerId, reviewId);

        // Deletion Attempt
        if (rs.deleteReview(reviewId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}

package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ReviewService;
import ar.edu.itba.paw.models.Entities.Review;
import ar.edu.itba.paw.webapp.dto.ReviewDto;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractSecondId;

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
public class ReviewController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    private ReviewService rs;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @PathParam("workerId")
    private Long workerId;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response listReviews(
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

        return Response.ok(new GenericEntity<List<ReviewDto>>(reviewsDto) {})
                .links(links)
                .cacheControl(cacheControl)
                .tag(reviewsHashCode)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response findReview(
            @PathParam("id") final long id
    ) {
        LOGGER.info("GET request arrived at '/workers/{}/reviews/{}'", workerId, id);

        // Content
        Review review = rs.findReview(id, workerId).orElseThrow(() -> new NotFoundException("Review not found"));
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
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Validated(CreateValidationSequence.class)
    public Response createReview(
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
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Secured("ROLE_SUPER_ADMINISTRATOR")
    public Response deleteById(
            @PathParam("id") final long reviewId
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

package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ReviewService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Review;
import ar.edu.itba.paw.webapp.dto.ReviewDto;
import ar.edu.itba.paw.webapp.form.ReviewForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
@Path("workers/{workerId}/reviews")
@Component
public class ReviewController extends GlobalControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);

    private final ReviewService rs;

    @Context
    private UriInfo uriInfo;

    @PathParam("workerId")
    private Long workerId;

    @Autowired
    public ReviewController(final UserService us, final ReviewService rs) {
        super(us);
        this.rs = rs;
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listReviews(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size) {
        LOGGER.info("GET request arrived at workers/{}/reviews", workerId);
        final List<Review> reviews = rs.getReviews(workerId, page, size);
        final List<ReviewDto> reviewsDto = reviews.stream()
                .map(r -> ReviewDto.fromReview(r, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "workers/" + workerId + "/reviews";
        int totalReviewPages = rs.calculateReviewPages(workerId, size);
        Link[] links = createPaginationLinks(baseUri, page, size, totalReviewPages);

        return Response.ok(new GenericEntity<List<ReviewDto>>(reviewsDto){})
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findReview(@PathParam("id") final long id) {
        LOGGER.info("GET request arrived at workers/{}/reviews/{}", workerId, id);
        return Response.ok(ReviewDto.fromReview(rs.findReviewById(id)
                .orElseThrow(() -> new NotFoundException("Review Not Found")), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createReview(@Valid final ReviewForm form) {
        LOGGER.info("POST request arrived at workers/{}/reviews", workerId);
        final Review review = rs.createReview(workerId, getLoggedUser().getUserId(), form.getRating(), form.getReview());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(review.getReviewId())).build();
        return Response.created(uri).build();
    }

}

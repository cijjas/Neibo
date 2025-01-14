package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.BookingService;
import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.dto.BookingDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.AmenityURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
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

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.*;

/*
 * # Summary
 *   - A Booking is the relationship between a User and an Availability, it also adds a date attribute to the relationship
 *
 * # Use cases
 *   - A User/Admin can create/delete a Booking
 *   - A User/Admin can list his Bookings
 *
 * # Issues
 *   - The form is taking Shifts URNs, I think it should be taking Availability URNs
 *   - Before the Shifts had to be dynamically created, not anymore, so I think the logic is simplified
 *   - Deletion Form could also take a list instead of unique values
 */

@Path(Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.BOOKINGS)
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON,})
public class BookingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bs;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public BookingController(BookingService bs) {
        this.bs = bs;
    }

    @GET
    public Response listBookings(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint Long neighborhoodId,
            @QueryParam(QueryParameter.BOOKED_BY) @UserURNConstraint String user,
            @QueryParam(QueryParameter.FOR_AMENITY) @AmenityURNConstraint String amenity,
            @QueryParam(QueryParameter.PAGE) @DefaultValue(Constant.DEFAULT_PAGE) int page,
            @QueryParam(QueryParameter.SIZE) @DefaultValue(Constant.DEFAULT_SIZE) int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/bookings'", neighborhoodId);

        // ID Extraction
        Long userId = extractOptionalSecondId(user);
        Long amenityId = extractOptionalSecondId(amenity);

        // Content
        final List<Booking> bookings = bs.getBookings(neighborhoodId, userId, amenityId, page, size);
        String bookingsHashCode = String.valueOf(bookings.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(bookingsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (bookings.isEmpty())
            return Response.noContent()
                    .tag(bookingsHashCode)
                    .build();

        final List<BookingDto> bookingsDto = bookings.stream()
                .map(b -> BookingDto.fromBooking(b, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS).path(String.valueOf(neighborhoodId)).path(Endpoint.BOOKINGS),
                bs.calculateBookingPages(neighborhoodId, amenityId, userId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<BookingDto>>(bookingsDto) {
                })
                .links(links)
                .cacheControl(cacheControl)
                .tag(bookingsHashCode)
                .build();
    }

    @GET
    @Path("{" + PathParameter.BOOKING_ID + "}")
    public Response findBooking(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint Long neighborhoodId,
            @PathParam(PathParameter.BOOKING_ID) @GenericIdConstraint long bookingId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/bookings/{}'", neighborhoodId, bookingId);

        Booking booking = bs.findBooking(neighborhoodId, bookingId).orElseThrow(NotFoundException::new);
        String bookingHashCode = String.valueOf(booking.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(bookingHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        BookingDto bookingDto = BookingDto.fromBooking(booking, uriInfo);

        return Response.ok(bookingDto)
                .tag(bookingHashCode)
                .cacheControl(cacheControl)
                .build();
    }

    @POST
    @Validated(CreateValidationSequence.class)
    public Response createBooking(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint Long neighborhoodId,
            @Valid @NotNull BookingDto createForm
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/bookings'", neighborhoodId);

        // Creation & HashCode Generation
        final Booking booking = bs.createBooking(extractFirstId(createForm.getShift()), extractSecondId(createForm.getUser()), extractSecondId(createForm.getAmenity()), extractDate(createForm.getBookingDate()));
        String bookingHashCode = String.valueOf(booking.hashCode());

        // Resource URN
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(booking.getBookingId())).build();

        return Response.created(uri)
                .tag(bookingHashCode)
                .build();
    }

    @DELETE
    @Path("{" + PathParameter.BOOKING_ID + "}")
    @PreAuthorize("@pathAccessControlHelper.canDeleteBooking(#bookingId, #neighborhoodId)")
    public Response deleteBooking(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint Long neighborhoodId,
            @PathParam(PathParameter.BOOKING_ID) @GenericIdConstraint long bookingId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/bookings/{}'", neighborhoodId, bookingId);

        // Deletion Attempt
        if (bs.deleteBooking(neighborhoodId, bookingId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}

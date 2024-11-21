package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.BookingService;
import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.webapp.dto.BookingDto;
import ar.edu.itba.paw.webapp.validation.constraints.form.AmenityURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.AmenityURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.UserURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import static ar.edu.itba.paw.webapp.validation.ValidationUtils.*;

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

@Path("neighborhoods/{neighborhoodId}/bookings")
@Component
@Validated
public class BookingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bs;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response listBookings(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("bookedBy") @UserURNFormConstraint @UserURNReferenceConstraint final String user,
            @QueryParam("forAmenity") @AmenityURNFormConstraint @AmenityURNReferenceConstraint final String amenity
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/bookings'", neighborhoodId);

        // ID Extraction
        Long userId = extractOptionalSecondId(user);
        Long amenityId = extractOptionalSecondId(amenity);

        // Content
        final List<Booking> bookings = bs.getBookings(userId, amenityId, neighborhoodId, page, size);
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
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/bookings",
                bs.calculateBookingPages(userId, amenityId, neighborhoodId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<BookingDto>>(bookingsDto) {})
                .links(links)
                .cacheControl(cacheControl)
                .tag(bookingsHashCode)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response findBooking(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("id") @GenericIdConstraint final long bookingId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/bookings/{}'", neighborhoodId, bookingId);

        Booking booking = bs.findBooking(bookingId, neighborhoodId).orElseThrow(() -> new NotFoundException("Booking Not Found"));
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
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Validated(CreateValidationSequence.class)
    public Response createBooking(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @Valid BookingDto form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/bookings'", neighborhoodId);

        // Creation & HashCode Generation
        final Booking booking = bs.createBooking(extractSecondId(form.getUser()), extractSecondId(form.getAmenity()), extractFirstId(form.getShift()), form.getReservationDate());
        String bookingHashCode = String.valueOf(booking.hashCode());

        // Resource URN
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(booking.getBookingId())).build();

        return Response.created(uri)
                .tag(bookingHashCode)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canDeleteBooking(#bookingId, #neighborhoodId)")
    public Response deleteById(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("id") @GenericIdConstraint final long bookingId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/bookings/{}'", neighborhoodId, bookingId);

        // Deletion Attempt
        if (bs.deleteBooking(bookingId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}

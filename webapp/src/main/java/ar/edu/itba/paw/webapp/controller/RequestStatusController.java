package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.RequestStatus;
import ar.edu.itba.paw.webapp.dto.RequestStatusDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.MAX_AGE_SECONDS;


/*
 * # Summary
 *   - Every Request has a RequestStatus, indicating if it has been seen by the requester (REQUESTED),
 *     accepted by the product owner (ACCEPTED), or declined by the owner (DECLINED)
 */

@Path("request-statuses")
@Component
@Produces(value = {MediaType.APPLICATION_JSON,})
public class RequestStatusController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestStatusController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    public Response listRequestStatuses() {
        LOGGER.info("GET request arrived at '/request-statuses'");

        // Content
        RequestStatus[] requestStatuses = RequestStatus.values();
        String requestStatusesHashcode = String.valueOf(Arrays.hashCode(requestStatuses));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(requestStatusesHashcode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<RequestStatusDto> requestStatusDtos = Arrays.stream(requestStatuses)
                .map(rs -> RequestStatusDto.fromRequestStatus(rs, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<RequestStatusDto>>(requestStatusDtos) {
                })
                .cacheControl(cacheControl)
                .tag(requestStatusesHashcode)
                .build();
    }

    @GET
    @Path("/{id}")
    public Response findRequestStatus(
            @PathParam("id") @GenericIdConstraint final Long id
    ) {
        LOGGER.info("GET request arrived at '/request-status/{}'", id);

        // Content
        RequestStatus requestStatus = RequestStatus.fromId(id);
        String requestStatusHashCode = String.valueOf(requestStatus.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(requestStatusHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(RequestStatusDto.fromRequestStatus(requestStatus, uriInfo))
                .cacheControl(cacheControl)
                .tag(requestStatusHashCode)
                .build();
    }
}

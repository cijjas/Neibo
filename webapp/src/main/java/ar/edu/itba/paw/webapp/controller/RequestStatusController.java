package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.RequestStatus;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.dto.RequestStatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.constants.Constant.IMMUTABLE;
import static ar.edu.itba.paw.webapp.controller.constants.Constant.MAX_AGE_SECONDS;

/*
 * # Summary
 *   - Every Request has a RequestStatus, indicating if it has been seen by the requester (REQUESTED),
 *     accepted by the product owner (ACCEPTED), or declined by the owner (DECLINED)
 * # Use cases
 *   - A Neighbor/Admin can filter their requests through this criteria
 */

@Path(Endpoint.API + "/" + Endpoint.REQUEST_STATUSES)
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
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        RequestStatus[] requestStatuses = RequestStatus.values();
        String requestStatusesHashcode = String.valueOf(Arrays.hashCode(requestStatuses));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        cacheControl.getCacheExtension().put(IMMUTABLE, "");
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
    @Path("{" + PathParameter.REQUEST_STATUS_ID + "}")
    public Response findRequestStatus(
            @PathParam(PathParameter.REQUEST_STATUS_ID) long requestStatusId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        RequestStatus requestStatus = RequestStatus.fromId(requestStatusId).orElseThrow(NotFoundException::new);
        String requestStatusHashCode = String.valueOf(requestStatus.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        cacheControl.getCacheExtension().put(IMMUTABLE, "");
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(requestStatusHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(RequestStatusDto.fromRequestStatus(requestStatus, uriInfo))
                .cacheControl(cacheControl)
                .tag(requestStatusHashCode)
                .build();
    }
}

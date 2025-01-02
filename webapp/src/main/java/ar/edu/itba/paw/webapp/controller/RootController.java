package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.RootDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


@Path("")
@Component
@Produces(value = {MediaType.APPLICATION_JSON,})
public class RootController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RootController.class);

    @Context
    private UriInfo uriInfo;

    @GET
    public Response getTreeStructure() {
        LOGGER.info("GET request arrived at '/'");

        return Response.ok(RootDto.createRootDto(uriInfo))
                .build();
    }
}

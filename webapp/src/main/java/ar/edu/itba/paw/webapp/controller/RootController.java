package ar.edu.itba.paw.webapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("")
@Component
@Produces(value = {MediaType.APPLICATION_JSON,})
public class RootController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RootController.class);

    @GET
    public Response getTreeStructure() {
        LOGGER.info("GET request arrived at '/'");

        // Content
        // Create RootDto

        return Response.ok()
                .build();
    }
}

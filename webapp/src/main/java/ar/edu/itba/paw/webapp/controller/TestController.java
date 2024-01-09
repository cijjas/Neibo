package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.webapp.dto.LanguageDto;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("test")
@Component
public class TestController {

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("illegal-argument")
    @Produces(MediaType.APPLICATION_JSON)
    public Response IllegalArgument() {

        throw new IllegalArgumentException("This is a custom message");

        // return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Path("not-found")
    @Produces(MediaType.APPLICATION_JSON)
    public Response notFound() {

        throw new NotFoundException("This is a custom message");

        // return Response.status(Response.Status.BAD_REQUEST).build();
    }
}

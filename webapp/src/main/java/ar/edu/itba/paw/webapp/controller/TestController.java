package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.enums.ProductStatus;
import ar.edu.itba.paw.webapp.dto.LanguageDto;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.nio.file.AccessDeniedException;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("test")
@Component
public class TestController {

    @Context
    private UriInfo uriInfo;


    @GET
    @Path("query-param")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryParamTest(
            @QueryParam("productStatus1") @DefaultValue("") final ProductStatus productStatus1,
            @QueryParam("productStatus2") @DefaultValue("") final String productStatus2,
            @QueryParam("float") final int floaty,
            @QueryParam("long") final Long longy,
            @QueryParam("date") final Date date
    ) {

        System.out.println(productStatus1);
        System.out.println(ProductStatus.valueOf(productStatus2));
        System.out.println(floaty);
        System.out.println(longy);
        System.out.println(date);


        throw new IllegalArgumentException("This is a custom message");

        // return Response.status(Response.Status.BAD_REQUEST).build();
    }

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

    @GET
    @Path("internal-server-error")
    @Produces(MediaType.APPLICATION_JSON)
    public Response internalServerError() {

        throw new NullPointerException();

        // return Response.status(Response.Status.BAD_REQUEST).build();
    }
}

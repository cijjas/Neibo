package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ReviewService;
import ar.edu.itba.paw.interfaces.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("workers/{workerId}/reviews")
@Component
public class ReviewController {

    @Autowired
    private ReviewService rs;

    @Context
    private UriInfo uriInfo;

    @PathParam("workerId")
    private Long workerId;


}

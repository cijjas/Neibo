package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.Professions;
import ar.edu.itba.paw.enums.StandardTime;
import ar.edu.itba.paw.interfaces.persistence.DepartmentDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.interfaces.services.ProfessionWorkerService;
import ar.edu.itba.paw.models.Entities.Channel;
import ar.edu.itba.paw.models.Entities.Profession;
import ar.edu.itba.paw.webapp.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("professions")
@Component
public class ProfessionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionController.class);

    private final ProfessionWorkerService ps;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public ProfessionController(final ProfessionWorkerService professionWorkerService) {
        this.ps = professionWorkerService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProfessions(
            @QueryParam("workerId") final Long workerId
    ) {
        LOGGER.info("GET request arrived at '/professions'");
        List<Profession> professions = ps.getWorkerProfessions(workerId);
        if (professions.isEmpty())
            return Response.noContent().build();

        List<ProfessionDto> professionDto = professions.stream()
                .map(p -> ProfessionDto.fromProfession(p, uriInfo)).collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ProfessionDto>>(professionDto){})
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findProfession(@PathParam("id") final long id) {
        LOGGER.info("GET request arrived at '/professions/{}'", id);
        return Response.ok(ProfessionDto.fromProfession(Professions.fromId(id), uriInfo)).build();
    }

}

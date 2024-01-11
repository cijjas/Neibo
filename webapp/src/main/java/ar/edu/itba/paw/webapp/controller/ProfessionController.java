package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.Professions;
import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.interfaces.services.ProfessionWorkerService;
import ar.edu.itba.paw.webapp.dto.DepartmentDto;
import ar.edu.itba.paw.webapp.dto.LanguageDto;
import ar.edu.itba.paw.webapp.dto.ProfessionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("professions")
@Component
public class ProfessionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionController.class);

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProfessions() {
        LOGGER.info("Listing Professions");
        List<ProfessionDto> professionDto = Arrays.stream(Professions.values())
                .map(p -> ProfessionDto.fromProfession(p, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ProfessionDto>>(professionDto){}).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findProfession(@PathParam("id") final long id) {
        LOGGER.info("Finding Profession with id {}", id);
        Professions profession = Professions.fromId((int) id);

        if (profession != null) {
            ProfessionDto professionDto = ProfessionDto.fromProfession(profession, uriInfo);
            return Response.ok(professionDto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Profession not found").build();
        }
    }

}

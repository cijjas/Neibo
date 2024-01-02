package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.webapp.dto.LanguageDto;
import ar.edu.itba.paw.webapp.dto.RoleDto;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("languages")
@Component
public class LanguageController {

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listRoles() {
        List<LanguageDto> languagesDto = Arrays.stream(Language.values())
                .map(l -> LanguageDto.fromLanguage(l, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<LanguageDto>>(languagesDto){}).build();
    }
}
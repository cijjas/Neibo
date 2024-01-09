package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.models.Entities.Channel;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.ChannelDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("neighborhoods/{neighborhoodId}/channels")
@Component
public class ChannelController {

    @Autowired
    private ChannelService cs;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private String neighborhoodId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listChannels() {
        List<Channel> channels = cs.getChannels(Long.parseLong(neighborhoodId));

        List<ChannelDto> channelDto = channels.stream()
                .map(c -> ChannelDto.fromChannel(c, uriInfo, Long.parseLong(neighborhoodId))).collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ChannelDto>>(channelDto){})
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findChannel(@PathParam("id") long id) {
        return Response.ok(ChannelDto.fromChannel(cs.findChannelById(id)
                .orElseThrow(() -> new NotFoundException("Channel Not Found")), uriInfo, Long.parseLong(neighborhoodId))).build();
    }


}


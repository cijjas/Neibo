package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Channel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ChannelService {

    Channel createChannel(long neighborhoodId, final String name);

    Channel createChannel(String name);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Channel> findChannelById(long id);

    Optional<Channel> findChannelByName(String name);

    List<Channel> getChannels(long neighborhoodId);

    Map<String, Channel> getNeighborChannels(long neighborhoodId, long neighborId);

    Map<String, Channel> getAdminChannels(long neighborhoodId);

}

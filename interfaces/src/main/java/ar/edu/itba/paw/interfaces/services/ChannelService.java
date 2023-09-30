package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Post;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ChannelService {
    List<Channel> getChannels(long neighborhoodId);

    Channel createChannel(long neighborhoodId, final String name);

    Optional<Channel> findChannelById(long id);

    Map<String, Channel> getNeighborChannels(long neighborhoodId, long neighborId);

    Map<String, Channel> getAdminChannels(long neighborhoodId);

}

package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Channel;

import java.util.List;
import java.util.Optional;

public interface ChannelService {

    Channel createChannel(long neighborhoodId, final String name);

    boolean deleteChannel(long channelId, long neighborhoodId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Channel> findChannel(long channelId, long neighborhoodId);

    List<Channel> getChannels(long neighborhoodId);
    int calculateChannelPages(long neighborhoodId, int size);

}

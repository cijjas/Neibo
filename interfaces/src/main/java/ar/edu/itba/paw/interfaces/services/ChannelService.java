package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Channel;

import java.util.List;
import java.util.Optional;

public interface ChannelService {

    Channel createChannel(long neighborhoodId, String name);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Channel> findChannel(long neighborhoodId, long channelId);

    List<Channel> getChannels(long neighborhoodId, Boolean isBase, int page, int size);

    int countChannels(long neighborhoodId, Boolean isBase);
}

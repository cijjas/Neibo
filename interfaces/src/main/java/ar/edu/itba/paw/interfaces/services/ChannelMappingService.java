package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Channel;

public interface ChannelMappingService {

    // -----------------------------------------------------------------------------------------------------------------

    void createChannelMapping(final long channelId, final long neighborhoodId);
}

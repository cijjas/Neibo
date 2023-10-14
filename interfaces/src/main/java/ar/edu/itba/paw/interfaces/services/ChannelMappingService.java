package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Channel;

public interface ChannelMappingService {

    // -------------------------------------------- CHANNEL MAPPING INSERT ---------------------------------------------

    void createChannelMapping(final long channelId, final long neighborhoodId);
}

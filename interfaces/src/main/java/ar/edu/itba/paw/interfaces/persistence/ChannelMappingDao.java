package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.ChannelMapping;

public interface ChannelMappingDao {

    // -------------------------------------------- CHANNEL MAPPING INSERT ---------------------------------------------

    ChannelMapping createChannelMapping(final long channelId, final long neighborhoodId);
}

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Channel;

import java.util.List;
import java.util.Optional;

public interface ChannelMappingDao {

    // -------------------------------------------- CHANNEL MAPPING INSERT ---------------------------------------------

    void createChannelMappingDao(final long channelId, final long neighborhoodId);
}

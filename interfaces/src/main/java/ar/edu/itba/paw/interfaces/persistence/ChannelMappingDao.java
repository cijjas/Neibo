package ar.edu.itba.paw.interfaces.persistence;

public interface ChannelMappingDao {

    // -------------------------------------------- CHANNEL MAPPING INSERT ---------------------------------------------

    void createChannelMapping(final long channelId, final long neighborhoodId);
}

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.ChannelMapping;

import java.util.List;

public interface ChannelMappingDao {

    // -------------------------------------------- CHANNEL MAPPING INSERT ---------------------------------------------

    ChannelMapping createChannelMapping(final long channelId, final long neighborhoodId);

    // -------------------------------------------- CHANNEL MAPPING SELECT ---------------------------------------------

    List<ChannelMapping> getChannelMappings(Long channelId, Long neighborhoodId, int page, int size);

    int channelMappingsCount(Long channelId, Long neighborhoodId);

    // -------------------------------------------- CHANNEL MAPPING DELETE ---------------------------------------------
    boolean deleteChannelMapping(long channelId, long neighborhoodId);
}

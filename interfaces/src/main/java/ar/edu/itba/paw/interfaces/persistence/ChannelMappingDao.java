package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.ChannelMapping;

import java.util.List;
import java.util.Optional;

public interface ChannelMappingDao {

    // -------------------------------------------- CHANNEL MAPPING INSERT ---------------------------------------------

    ChannelMapping createChannelMapping(final long channelId, final long neighborhoodId);

    // -------------------------------------------- CHANNEL MAPPING SELECT ---------------------------------------------

    List<ChannelMapping> getChannelMappings(Long channelId, Long neighborhoodId, int page, int size);

    Optional<ChannelMapping> findChannelMapping(long channelId, long neighborhoodId);

    // -------------------------------------------- CHANNEL MAPPING DELETE ---------------------------------------------
    boolean deleteChannelMapping(long channelId, long neighborhoodId);
}

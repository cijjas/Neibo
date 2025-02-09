package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.ChannelMapping;

import java.util.List;
import java.util.Optional;

public interface ChannelMappingDao {

    // -------------------------------------------- CHANNEL MAPPING INSERT ---------------------------------------------

    ChannelMapping createChannelMapping(long neighborhoodId, long channelId);

    // -------------------------------------------- CHANNEL MAPPING SELECT ---------------------------------------------

    Optional<ChannelMapping> findChannelMapping(long neighborhoodId, long channelId);

    List<ChannelMapping> getChannelMappings(Long neighborhoodId, Long channelId, int page, int size);

    // -------------------------------------------- CHANNEL MAPPING DELETE ---------------------------------------------

    boolean deleteChannelMapping(long neighborhoodId, long channelId);
}

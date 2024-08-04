package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Channel;

import java.util.List;
import java.util.Optional;

public interface ChannelDao {

    // -------------------------------------------- CHANNELS INSERT ----------------------------------------------------

    Channel createChannel(final String name);

    // -------------------------------------------- CHANNELS SELECT ----------------------------------------------------

    Optional<Channel> findChannel(long channelId, long NeighborhoodId);

    Optional<Channel> findChannel(String name);

    List<Channel> getChannels(final long neighborhoodId, int page, int size);

    int countChannels(final long neighborhoodId);

    // -------------------------------------------- CHANNELS DELETE ----------------------------------------------------

    boolean deleteChannel(final long channelId);
}

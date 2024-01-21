package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Channel;

import java.util.List;
import java.util.Optional;

public interface ChannelDao {

    // -------------------------------------------- CHANNELS INSERT ----------------------------------------------------

    Channel createChannel(final String name);

    // -------------------------------------------- CHANNELS SELECT ----------------------------------------------------

    List<Channel> getChannels(final long neighborhoodId);

    Optional<Channel> findChannel(long channelId, long NeighborhoodId);

    Optional<Channel> findChannel(String name);
}

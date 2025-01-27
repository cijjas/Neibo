package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Channel;

import java.util.List;
import java.util.Optional;

public interface ChannelDao {

    // -------------------------------------------- CHANNELS INSERT ----------------------------------------------------

    Channel createChannel(String name);

    // -------------------------------------------- CHANNELS SELECT ----------------------------------------------------

    Optional<Channel> findChannel(long neighborhoodId, long channelId);

    Optional<Channel> findChannel(String name);

    List<Channel> getChannels(long neighborhoodId, Boolean isBase, int page, int size);

    int countChannels(long neighborhoodId, Boolean isBase);
}

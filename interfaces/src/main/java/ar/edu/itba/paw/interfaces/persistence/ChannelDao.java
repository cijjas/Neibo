package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Channel;

import java.util.List;
import java.util.Optional;

public interface ChannelDao {

    // -------------------------------------------- CHANNELS INSERT ----------------------------------------------------

    // Specifying neighborhood is not necessary as the channel may be repeated and most likely will, there is a junction table to define
    // to which neighborhood each channel belongs
    Channel createChannel(final String name);

    // -------------------------------------------- CHANNELS SELECT ----------------------------------------------------

    List<Channel> getChannels(final long neighborhoodId);

    Optional<Channel> findChannelById(long id);

    Optional<Channel> findChannelByName(String name);
}

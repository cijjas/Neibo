package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Channel;

import java.util.List;
import java.util.Optional;

public interface ChannelDao {

    // -------------------------------------------- CHANNELS INSERT ----------------------------------------------------

    Channel createChannel(final String name);

    // -------------------------------------------- CHANNELS SELECT ----------------------------------------------------

    List<Channel> getChannels();

    Optional<Channel> findChannelById(long id);
}

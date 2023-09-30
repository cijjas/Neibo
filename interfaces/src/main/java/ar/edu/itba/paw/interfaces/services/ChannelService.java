package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Post;

import java.util.List;
import java.util.Optional;

public interface ChannelService {

    // -----------------------------------------------------------------------------------------------------------------

    Channel createChannel(final String name);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Channel> findChannelById(long id);

    List<Channel> getChannels();
}

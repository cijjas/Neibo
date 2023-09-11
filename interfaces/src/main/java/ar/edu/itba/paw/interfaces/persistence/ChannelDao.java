package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Channel;

import java.util.List;

public interface ChannelDao {
    Channel create(final String name);
    List<Channel> getAllChannels();
}

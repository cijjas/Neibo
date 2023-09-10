package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Channel;

import java.util.List;

public interface ChannelDao {
    List<Channel> getAllChannels();
}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.models.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChannelServiceImpl implements ChannelService {
    private final ChannelDao channelDao;

    @Autowired
    public ChannelServiceImpl(final ChannelDao channelDao) {
        this.channelDao = channelDao;
    }

    @Override
    public List<Channel> getChannels() {
        return channelDao.getChannels();
    }

    @Override
    public Channel createChannel(String name) {
        return channelDao.createChannel(name);
    }

    @Override
    public Optional<Channel> findChannelById(long id) {
        return channelDao.findChannelById(id);
    }
}

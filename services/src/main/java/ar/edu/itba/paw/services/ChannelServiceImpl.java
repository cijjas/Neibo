package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.models.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}

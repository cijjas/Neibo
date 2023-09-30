package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.persistence.ChannelMappingDao;
import ar.edu.itba.paw.interfaces.services.ChannelMappingService;
import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.models.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelMappingServiceImpl implements ChannelMappingService {

    private final ChannelMappingDao channelMappingDao;

    @Autowired
    public ChannelMappingServiceImpl(final ChannelMappingDao channelMappingDao) {
        this.channelMappingDao = channelMappingDao;
    }
    @Override
    public void createChannelMappingDao(long channelId, long neighborhoodId) {
        channelMappingDao.createChannelMappingDao(channelId, neighborhoodId);
    }
}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.persistence.ChannelMappingDao;
import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.models.Entities.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChannelServiceImpl implements ChannelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelServiceImpl.class);
    private final ChannelDao channelDao;
    private final ChannelMappingDao channelMappingDao;

    @Autowired
    public ChannelServiceImpl(final ChannelDao channelDao, final ChannelMappingDao channelMappingDao) {
        this.channelDao = channelDao;
        this.channelMappingDao = channelMappingDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Channel createChannel(long neighborhoodId, String name) {
        LOGGER.info("Creating Channel {}", name);

        Channel channel = channelDao.createChannel(name);
        channelMappingDao.createChannelMapping(channel.getChannelId(), neighborhoodId);
        return channel;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Channel> findChannel(long channelId, long neighborhoodId) {

        ValidationUtils.checkChannelId(channelId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return channelDao.findChannel(channelId, neighborhoodId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Channel> getChannels(long neighborhoodId) {
        LOGGER.info("Getting Channels from Neighborhood {}", neighborhoodId);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return channelDao.getChannels(neighborhoodId);
    }
}

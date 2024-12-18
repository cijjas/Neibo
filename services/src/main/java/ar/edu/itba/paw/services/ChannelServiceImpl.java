package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
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
    public ChannelServiceImpl(ChannelDao channelDao, ChannelMappingDao channelMappingDao) {
        this.channelDao = channelDao;
        this.channelMappingDao = channelMappingDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Channel createChannel(long neighborhoodId, String name) {
        LOGGER.info("Creating Channel {}", name);

        Channel channel = channelDao.findChannel(name).orElseGet(() -> channelDao.createChannel(name));
        channelMappingDao.findChannelMapping(neighborhoodId, channel.getChannelId()).orElseGet(() -> channelMappingDao.createChannelMapping(neighborhoodId, channel.getChannelId()));

        return channel;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Channel> findChannel(long neighborhoodId, long channelId) {
        LOGGER.info("Finding Channel {} from Neighborhood {}", channelId, neighborhoodId);

        return channelDao.findChannel(neighborhoodId, channelId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Channel> getChannels(long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Channels for Neighborhood {}", neighborhoodId);

        return channelDao.getChannels(neighborhoodId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateChannelPages(long neighborhoodId, int size) {
        LOGGER.info("Calculating Channel Pages for Neighborhood {}", neighborhoodId);

        return PaginationUtils.calculatePages(channelDao.countChannels(neighborhoodId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteChannel(long neighborhoodId, long channelId) {
        LOGGER.info("Deleting Channel {}", channelId);

        channelDao.findChannel(neighborhoodId, channelId).orElseThrow(NotFoundException::new);
        channelMappingDao.deleteChannelMapping(neighborhoodId, channelId);

        // If this channel was only used in one Neighborhood then it an be safely deleted
        if(channelMappingDao.getChannelMappings(null, channelId, 1, 1).isEmpty())
            channelDao.deleteChannel(channelId);

        return true;
    }
}

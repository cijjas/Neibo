package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.persistence.ChannelMappingDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.models.Entities.Channel;
import ar.edu.itba.paw.models.Entities.ChannelMapping;
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
    private final NeighborhoodDao neighborhoodDao;

    @Autowired
    public ChannelServiceImpl(final ChannelDao channelDao, final ChannelMappingDao channelMappingDao,
                              final NeighborhoodDao neighborhoodDao) {
        this.channelDao = channelDao;
        this.channelMappingDao = channelMappingDao;
        this.neighborhoodDao = neighborhoodDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Channel createChannel(long neighborhoodId, String name) {
        LOGGER.info("Creating Channel {}", name);

        // find channel by name, if it doesn't exist create it
        Channel channel = channelDao.findChannel(name).orElseGet(() -> channelDao.createChannel(name));

        if(channelMappingDao.channelMappingsCount(channel.getChannelId(), neighborhoodId) == 0)
            channelMappingDao.createChannelMapping(channel.getChannelId(), neighborhoodId);

        return channel;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Channel> findChannel(long channelId, long neighborhoodId) {
        LOGGER.info("Finding Channel {} from Neighborhood {}", channelId, neighborhoodId);

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

    @Override
    public int calculateChannelPages(long neighborhoodId, int size) {
        LOGGER.info("Calculating Channel Pages for Neighborhood {}", neighborhoodId);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(channelMappingDao.channelMappingsCount(null, neighborhoodId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteChannel(long channelId, long neighborhoodId) {
        LOGGER.info("Deleting Channel {}", channelId);

        ValidationUtils.checkChannelId(channelId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        channelDao.findChannel(channelId, neighborhoodId).orElseThrow(NotFoundException::new);
        channelMappingDao.deleteChannelMapping(channelId, neighborhoodId);

        //if the channel was only being used by this neighborhood, it gets deleted
        if(channelMappingDao.channelMappingsCount(channelId, null) == 0) {
            channelDao.deleteChannel(channelId);
        }

        return true;
    }
}

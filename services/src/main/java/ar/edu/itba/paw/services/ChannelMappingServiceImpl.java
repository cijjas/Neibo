package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.persistence.ChannelMappingDao;
import ar.edu.itba.paw.interfaces.services.ChannelMappingService;
import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.models.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChannelMappingServiceImpl implements ChannelMappingService {

    private final ChannelMappingDao channelMappingDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelMappingServiceImpl.class);

    @Autowired
    public ChannelMappingServiceImpl(final ChannelMappingDao channelMappingDao) {
        this.channelMappingDao = channelMappingDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void createChannelMapping(long channelId, long neighborhoodId) {
        LOGGER.info("Associating Channel {} with Neighborhood {}", channelId, neighborhoodId);
        channelMappingDao.createChannelMapping(channelId, neighborhoodId);
    }
}

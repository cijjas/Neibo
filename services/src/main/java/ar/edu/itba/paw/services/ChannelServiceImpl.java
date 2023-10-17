package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.services.ChannelMappingService;
import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.enums.BaseChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ChannelServiceImpl implements ChannelService {
    private final ChannelDao channelDao;
    private final ChannelMappingService channelMappingService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelServiceImpl.class);

    @Autowired
    public ChannelServiceImpl(final ChannelDao channelDao, final ChannelMappingService channelMappingService) {
        this.channelDao = channelDao;
        this.channelMappingService = channelMappingService;
    }

    @Override
    public List<Channel> getChannels(long neighborhoodId) {
        LOGGER.info("Getting Channels from Neighborhood {}", neighborhoodId);
        return channelDao.getChannels(neighborhoodId);
    }

    @Override
    public Channel createChannel(long neighborhoodId, String name) {
        LOGGER.info("Creating Channel {}", name);
        Channel channel = channelDao.createChannel(name);
        channelMappingService.createChannelMapping(channel.getChannelId(), neighborhoodId);
        return channel;
    }

    @Override
    public Optional<Channel> findChannelById(long id) {
        return channelDao.findChannelById(id);
    }

    @Override
    public Optional<Channel> findChannelByName(String name) {
        return channelDao.findChannelByName(name);
    }


    @Override
    public Map<String, Channel> getNeighborChannels(long neighborhoodId, long neighborId) {
        Map<String, Channel> channelMap = channelDao.getChannels(neighborhoodId).stream()
                .collect(Collectors.toMap(Channel::getChannel, Function.identity()));
        channelMap.remove(BaseChannel.ANNOUNCEMENTS.toString());
        return channelMap;
    }

    @Override
    public Map<String, Channel> getAdminChannels(long neighborhoodId) {
        try {
            List<Channel> allChannels = channelDao.getChannels(neighborhoodId);

            // Filter the channels to find the "Announcements" channel
            Optional<Channel> announcementsChannelOptional = allChannels.stream()
                    .filter(channel -> BaseChannel.ANNOUNCEMENTS.toString().equalsIgnoreCase(channel.getChannel()))
                    .findFirst();

            // Create a new map with the "Announcements" channel, if found
            Map<String, Channel> adminChannelMap = new HashMap<>();
            announcementsChannelOptional.ifPresent(announcementsChannel ->
                    adminChannelMap.put(announcementsChannel.getChannel(), announcementsChannel)
            );

            return adminChannelMap;
        } catch (Exception e) {
            LOGGER.error("Error getting admin channels", e);
        }
        return null;
    }

}

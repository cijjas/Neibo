package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.persistence.ChannelMappingDao;
import ar.edu.itba.paw.interfaces.services.ChannelMappingService;
import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.models.Channel;
import enums.BaseChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ChannelServiceImpl implements ChannelService {
    private final ChannelDao channelDao;
    private final ChannelMappingDao channelMappingDao;

    @Autowired
    public ChannelServiceImpl(final ChannelDao channelDao, final ChannelMappingDao channelMappingDao) {
        this.channelDao = channelDao;
        this.channelMappingDao = channelMappingDao;
    }

    @Override
    public List<Channel> getChannels(long neighborhoodId) {
        return channelDao.getChannels(neighborhoodId);
    }

    @Override
    public Channel createChannel(long neighborhoodId, String name) {
        Channel channel = channelDao.createChannel(name);
        channelMappingDao.createChannelMappingDao(channel.getChannelId(), neighborhoodId);
        return channel;
    }

    @Override
    public Optional<Channel> findChannelById(long id) {
        return channelDao.findChannelById(id);
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
            System.out.println(e);
        }
        return null;
    }

}

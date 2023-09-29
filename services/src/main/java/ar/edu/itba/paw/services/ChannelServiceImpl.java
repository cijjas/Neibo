package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.models.Channel;
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

    @Override
    public Map<String, Channel> getNeighborChannels(long neighborId) {
        Map<String, Channel> channelMap = channelDao.getChannels().stream()
                .collect(Collectors.toMap(Channel::getChannel, Function.identity()));
        channelMap.remove("Announcements");
        return channelMap;
    }

    @Override
    public Map<String, Channel> getAdminChannels() {
        try {
            List<Channel> allChannels = channelDao.getChannels();

            // Filter the channels to find the "Announcements" channel
            Optional<Channel> announcementsChannelOptional = allChannels.stream()
                    .filter(channel -> "Announcements".equalsIgnoreCase(channel.getChannel()))
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

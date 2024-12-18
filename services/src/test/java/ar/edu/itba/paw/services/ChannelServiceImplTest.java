package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.persistence.ChannelMappingDao;
import ar.edu.itba.paw.models.Entities.Channel;
import ar.edu.itba.paw.models.Entities.ChannelMapping;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class ChannelServiceImplTest {

    @Mock
    private ChannelDao channelDao;
    @Mock
    private ChannelMappingDao channelMappingDao;

    @InjectMocks
    private ChannelServiceImpl channelService;

    @Test
    public void create_channel_channelMapping() {
        // Pre Conditions
        long neighborhoodId = 1L;
        String channelName = "General";
        Channel mockChannel = new Channel.Builder().channelId(1L).channel(channelName).build();
        ChannelMapping mockMapping = new ChannelMapping(new Neighborhood.Builder().neighborhoodId(neighborhoodId).build(), new Channel.Builder().channelId(1L).build());

        when(channelDao.findChannel(channelName)).thenReturn(Optional.of(mockChannel));
        when(channelMappingDao.findChannelMapping(neighborhoodId, mockChannel.getChannelId())).thenReturn(Optional.of(mockMapping));

        // Exercise
        Channel result = channelService.createChannel(neighborhoodId, channelName);

        // Validation & Post Conditions
        verify(channelDao, times(1)).findChannel(channelName);
        verify(channelDao, never()).createChannel(anyString());
        verify(channelMappingDao, times(1)).findChannelMapping(neighborhoodId, mockChannel.getChannelId());
        verify(channelMappingDao, never()).createChannelMapping(anyLong(), anyLong());
        assertEquals(mockChannel, result);
    }

    @Test
    public void create_noChannel_noChannelMapping() {
        // Pre Conditions
        long neighborhoodId = 1L;
        String channelName = "General";
        Channel mockChannel = new Channel.Builder().channelId(1L).channel(channelName).build();

        when(channelDao.findChannel(channelName)).thenReturn(Optional.empty());
        when(channelDao.createChannel(channelName)).thenReturn(mockChannel);
        when(channelMappingDao.createChannelMapping(neighborhoodId, mockChannel.getChannelId()))
                .thenReturn(new ChannelMapping(new Neighborhood.Builder().neighborhoodId(neighborhoodId).build(), new Channel.Builder().channelId(1L).build()));

        // Exercise
        Channel result = channelService.createChannel(neighborhoodId, channelName);

        // Assert
        verify(channelDao, times(1)).findChannel(channelName);
        verify(channelDao, times(1)).createChannel(channelName);
        verify(channelMappingDao, times(1)).findChannelMapping(neighborhoodId, mockChannel.getChannelId());
        verify(channelMappingDao, times(1)).createChannelMapping(neighborhoodId, mockChannel.getChannelId());
        assertEquals(mockChannel, result);
    }

    @Test
    public void create_channel_noChannelMapping() {
        // Pre Conditions
        long neighborhoodId = 1L;
        String channelName = "General";
        Channel mockChannel = new Channel.Builder().channelId(1L).channel(channelName).build();

        when(channelDao.findChannel(channelName)).thenReturn(Optional.of(mockChannel));
        when(channelMappingDao.findChannelMapping(neighborhoodId, mockChannel.getChannelId())).thenReturn(Optional.empty());
        when(channelMappingDao.createChannelMapping(neighborhoodId, mockChannel.getChannelId()))
                .thenReturn(new ChannelMapping(new Neighborhood.Builder().neighborhoodId(neighborhoodId).build(), new Channel.Builder().channelId(1L).build()));

        // Exercise
        Channel result = channelService.createChannel(neighborhoodId, channelName);

        // Validations & Post Conditions
        verify(channelDao, times(1)).findChannel(channelName);
        verify(channelDao, never()).createChannel(anyString());
        verify(channelMappingDao, times(1)).findChannelMapping(neighborhoodId, mockChannel.getChannelId());
        verify(channelMappingDao, times(1)).createChannelMapping(neighborhoodId, mockChannel.getChannelId());
        assertEquals(mockChannel, result);
    }

    @Test
    public void delete_noChannelMappings() {
        // Pre Conditions
        long channelId = 1L;
        long neighborhoodId = 2L;

        when(channelDao.findChannel(neighborhoodId, channelId)).thenReturn(Optional.of(new Channel.Builder().build()));
        when(channelMappingDao.getChannelMappings(null, channelId, 1, 1)).thenReturn(Collections.emptyList());

        // Exercise
        boolean result = channelService.deleteChannel(neighborhoodId, channelId);

        // Validations & Post Conditions
        assertTrue(result);

        verify(channelDao, times(1)).findChannel(neighborhoodId, channelId);
        verify(channelMappingDao, times(1)).deleteChannelMapping(neighborhoodId, channelId);
        verify(channelMappingDao, times(1)).getChannelMappings(null, channelId, 1, 1);
        verify(channelDao, times(1)).deleteChannel(channelId);
    }

    @Test
    public void delete_channelMapping() {
        // Pre Conditions
        long channelId = 1L;
        long neighborhoodId = 2L;

        when(channelDao.findChannel(neighborhoodId, channelId)).thenReturn(Optional.of(new Channel.Builder().build()));
        when(channelMappingDao.getChannelMappings(null, channelId, 1, 1)).thenReturn(Collections.singletonList(new ChannelMapping(new Neighborhood.Builder().neighborhoodId(neighborhoodId).build(), new Channel.Builder().channelId(channelId).build())));

        // Exercise
        boolean result = channelService.deleteChannel(neighborhoodId, channelId);

        // Validations & Post Conditions
        assertTrue(result);

        verify(channelDao, times(1)).findChannel(neighborhoodId, channelId);
        verify(channelMappingDao, times(1)).deleteChannelMapping(neighborhoodId, channelId);
        verify(channelMappingDao, times(1)).getChannelMappings(null, channelId, 1, 1);
        verify(channelDao, never()).deleteChannel(channelId); // Channel should not be deleted
    }
}

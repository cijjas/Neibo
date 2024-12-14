package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.Entities.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
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
    public void testCreateChannel_ChannelAndMappingExist() {
        // Arrange
        long neighborhoodId = 1L;
        String channelName = "General";
        Channel mockChannel = new Channel.Builder().channelId(1L).channel(channelName).build();
        ChannelMapping mockMapping = new ChannelMapping(new Neighborhood.Builder().neighborhoodId(neighborhoodId).build(), new Channel.Builder().channelId(1L).build());

        when(channelDao.findChannel(channelName)).thenReturn(Optional.of(mockChannel));
        when(channelMappingDao.findChannelMapping(mockChannel.getChannelId(), neighborhoodId)).thenReturn(Optional.of(mockMapping));

        // Act
        Channel result = channelService.createChannel(neighborhoodId, channelName);

        // Assert
        verify(channelDao, times(1)).findChannel(channelName);
        verify(channelDao, never()).createChannel(anyString());
        verify(channelMappingDao, times(1)).findChannelMapping(mockChannel.getChannelId(), neighborhoodId);
        verify(channelMappingDao, never()).createChannelMapping(anyLong(), anyLong());
        assertEquals(mockChannel, result);
    }

    @Test
    public void testCreateChannel_ChannelExistsButMappingDoesNotExist() {
        // Arrange
        long neighborhoodId = 1L;
        String channelName = "General";
        Channel mockChannel = new Channel.Builder().channelId(1L).channel(channelName).build();

        when(channelDao.findChannel(channelName)).thenReturn(Optional.of(mockChannel));
        when(channelMappingDao.findChannelMapping(mockChannel.getChannelId(), neighborhoodId)).thenReturn(Optional.empty());
        when(channelMappingDao.createChannelMapping(mockChannel.getChannelId(), neighborhoodId))
                .thenReturn(new ChannelMapping(new Neighborhood.Builder().neighborhoodId(neighborhoodId).build(), new Channel.Builder().channelId(1L).build()));

        // Act
        Channel result = channelService.createChannel(neighborhoodId, channelName);

        // Assert
        verify(channelDao, times(1)).findChannel(channelName);
        verify(channelDao, never()).createChannel(anyString());
        verify(channelMappingDao, times(1)).findChannelMapping(mockChannel.getChannelId(), neighborhoodId);
        verify(channelMappingDao, times(1)).createChannelMapping(mockChannel.getChannelId(), neighborhoodId);
        assertEquals(mockChannel, result);
    }

    @Test
    public void testCreateChannel_ChannelDoesNotExist() {
        // Arrange
        long neighborhoodId = 1L;
        String channelName = "General";
        Channel mockChannel = new Channel.Builder().channelId(1L).channel(channelName).build();

        when(channelDao.findChannel(channelName)).thenReturn(Optional.empty());
        when(channelDao.createChannel(channelName)).thenReturn(mockChannel);
        when(channelMappingDao.createChannelMapping(mockChannel.getChannelId(), neighborhoodId))
                .thenReturn(new ChannelMapping(new Neighborhood.Builder().neighborhoodId(neighborhoodId).build(), new Channel.Builder().channelId(1L).build()));

        // Act
        Channel result = channelService.createChannel(neighborhoodId, channelName);

        // Assert
        verify(channelDao, times(1)).findChannel(channelName);
        verify(channelDao, times(1)).createChannel(channelName);
        verify(channelMappingDao, times(1)).findChannelMapping(mockChannel.getChannelId(), neighborhoodId);
        verify(channelMappingDao, times(1)).createChannelMapping(mockChannel.getChannelId(), neighborhoodId);
        assertEquals(mockChannel, result);
    }

}

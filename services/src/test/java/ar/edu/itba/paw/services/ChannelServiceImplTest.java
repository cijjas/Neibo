package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.services.ChannelMappingService;
import ar.edu.itba.paw.models.MainEntities.Channel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChannelServiceImplTest {
    private static final Long ID = 1L;
    private static final String NAME = "Amantes de los burritos";
    private static final Long NEIGHBORHOOD_ID = 1L;

    @Mock
    private ChannelDao channelDao;
    @Mock
    private ChannelMappingService channelMappingService;
    @InjectMocks
    private ChannelServiceImpl cs;

    @Test
    public void testCreate() {
        // 1. Preconditions
        when(channelDao.createChannel(anyString())).thenReturn(new Channel.Builder()
                .channelId(ID)
                .channel(NAME)
                .build()
        );

        // 2. Exercise
        Channel newChannel = cs.createChannel(NEIGHBORHOOD_ID, NAME);

        // 3. Postconditions
        Assert.assertNotNull(newChannel);
        Assert.assertEquals(newChannel.getChannelId(), ID);
        Assert.assertEquals(newChannel.getChannel(), NAME);

    }

    @Test(expected = RuntimeException.class)
    public void testCreateAlreadyExists() {
        // 1. Preconditions
        when(channelDao.createChannel(eq(NAME))).thenThrow(RuntimeException.class);

        // 2. Exercise
        Channel newChannel = cs.createChannel(NEIGHBORHOOD_ID, NAME);

        // 3. Postconditions
    }
}

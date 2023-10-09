package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.interfaces.persistence.ChannelMappingDao;
import ar.edu.itba.paw.models.Channel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
public class ChannelServiceImplTest {
    private static final long ID = 1;
    private static final String NAME = "Amantes de los burritos";
    private static final long NEIGHBORHOOD_ID = 1;

    // private final UserServiceImpl us = new UserServiceImpl(null);
    // Qué usamos como UserDao para el UserServiceImpl? No queremos conectarlo al Postgres de verdad, es una pérdida de
    // tiempo escribir un propio, por ejemplo, InMemoryTestUserDao que guarde los usuarios en un mapa en memoria...
    // Para esto generamos un mock con Mockito, y le pedimos que nos cree el UserServiceImpl inyectando la clase
    // mock-eada:
    @Mock // Le pedimos que nos genere una clase mock de UserDao
    private ChannelDao channelDao;
    @Mock
    private ChannelMappingDao channelMappingDao;
    @InjectMocks // Le pedimos que cree un UserServiceImpl, y que en el ctor (que toma un UserDao) inyecte un mock.
    private ChannelServiceImpl cs;
    @Test
    public void testCreate() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(channelDao.createChannel(anyString())).thenReturn(new Channel.Builder()
                .channelId(ID)
                .channel(NAME)
                .build()
        );

        // 2. Ejercitar
        // Pruebo la funcionalidad de usuarios
        Channel newChannel = cs.createChannel(NEIGHBORHOOD_ID,NAME);

        // 3. Postcondiciones
        Assert.assertNotNull(newChannel);
        Assert.assertEquals(newChannel.getChannelId(), ID);
        Assert.assertEquals(newChannel.getChannel(), NAME);

        // Verifico que se haya llamado create del UserDao una vez
        // NUNCA HAGAN ESTO, PORQUE ESTAS PROBANDO EL UserServiceImpl QUE TE IMPORTA CÓMO EL USA EL UserDao
        // Mockito.verify(userDao, times(1)).create(EMAIL, PASSWORD);
    }
    @Test(expected = RuntimeException.class) // "Espero que este test lance y falle con una exception tal"
    public void testCreateAlreadyExists() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(channelDao.createChannel(eq(NAME))).thenThrow(RuntimeException.class);

        // 2. Ejercitar
        Channel newChannel = cs.createChannel(NEIGHBORHOOD_ID,NAME);

        // 3. Postcondiciones
        // (Nada, espero que lo anterior tire exception)
    }

    @Test
    public void testFindById() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(channelDao.findChannelById(eq(ID))).thenReturn(Optional.of(new Channel.Builder()
                .channelId(ID)
                .channel(NAME)
                .build()
        ));

        // 2. Ejercitar
        Optional<Channel> newChannel = cs.findChannelById(ID);

        // 3. Postcondiciones
        Assert.assertTrue(newChannel.isPresent());
        Assert.assertEquals(ID, newChannel.get().getChannelId());
    }

}

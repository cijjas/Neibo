package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.models.Tag;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TagServiceImplTest {

    private static final long ID = 1;
    private static final String TAG = "Varsovia";
    private static final long POST_ID = 1;

    @Mock
    private TagDao tagDao;
    @InjectMocks
    private TagServiceImpl ts;

    @Test
    public void testCreate() {
        // 1. Preconditions
        when(tagDao.createTag(anyString())).thenReturn(new Tag.Builder()
                .tagId(ID)
                .tag(TAG)
                .build()
        );

        // 2. Exercise
        Tag newTag = ts.createTag(TAG);

        // 3. Postconditions
        Assert.assertNotNull(newTag);
        Assert.assertEquals(newTag.getTagId(), ID);
        Assert.assertEquals(newTag.getTag(), TAG);

    }

    @Test(expected = RuntimeException.class)
    public void testCreateAlreadyExists() {
        // 1. Preconditions
        when(tagDao.createTag(eq(TAG))).thenThrow(RuntimeException.class);

        // 2. Exercise
        Tag newTag = ts.createTag(TAG);

        // 3. Postconditions
    }

    /*@Test
    public void testFindById() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        List<Tag> tagsList = Collections.singletonList(new Tag.Builder()
                .tagId(ID)
                .tag(TAG)
                .build());

        when(tagDao.findTagsByPostId(eq(POST_ID))).thenReturn(Optional.of(tagsList));

        // 2. Ejercitar
        Optional<List<Tag>> newTagList = ts.findTagsByPostId(POST_ID);

        // 3. Postcondiciones
        Assert.assertTrue(newTagList.isPresent());
        Assert.assertEquals(ID, newTagList.get().get(0).getTagId());
    }*/
}

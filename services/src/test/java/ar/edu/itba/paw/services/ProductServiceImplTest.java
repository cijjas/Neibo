package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {

    @Mock
    private ProductDao productDao;
    @Mock
    private ImageService imageService;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    public void create_noImage() {
        // Pre Conditions
        long userId = 1L;
        String name = "Product A";
        String description = "Description of Product A";
        Double price = 100.0;
        boolean used = false;
        long departmentId = 2L;
        List<Long> imageIds = null;
        long units = 10L;

        when(productDao.createProduct(eq(userId), eq(name), eq(description), eq(price), eq(used), eq(departmentId), eq(0L), eq(0L), eq(0L), eq(units)))
                .thenReturn(new Product.Builder().build());

        // Exercise
        Product result = productService.createProduct(userId, name, description, price, units, used, departmentId, imageIds);

        // Validations & Post Conditions
        verify(productDao, times(1)).createProduct(eq(userId), eq(name), eq(description), eq(price), eq(used), eq(departmentId), eq(0L), eq(0L), eq(0L), eq(units));
        assertNotNull(result);
    }

    @Test
    public void create_oneImage() {
        // Pre Conditions
        long userId = 1L;
        String name = "Product B";
        String description = "Description of Product B";
        Double price = 150.0;
        boolean used = true;
        long departmentId = 3L;
        List<Long> imageIds = Collections.singletonList(5L);
        long units = 20L;

        when(productDao.createProduct(eq(userId), eq(name), eq(description), eq(price), eq(used), eq(departmentId), eq(5L), eq(0L), eq(0L), eq(units)))
                .thenReturn(new Product.Builder().build());

        // Exercise
        Product result = productService.createProduct(userId, name, description, price, units, used, departmentId, imageIds);

        // Validations & Post Conditions
        verify(productDao, times(1)).createProduct(eq(userId), eq(name), eq(description), eq(price), eq(used), eq(departmentId), eq(5L), eq(0L), eq(0L), eq(units));
        assertNotNull(result);
    }

    @Test
    public void create_twoImages() {
        // Pre Conditions
        long userId = 1L;
        String name = "Product C";
        String description = "Description of Product C";
        Double price = 200.0;
        boolean used = false;
        long departmentId = 4L;
        List<Long> imageIds = Arrays.asList(5L, 6L);
        long units = 30L;

        when(productDao.createProduct(eq(userId), eq(name), eq(description), eq(price), eq(used), eq(departmentId), eq(5L), eq(6L), eq(0L), eq(units)))
                .thenReturn(new Product.Builder().build());

        // Exercise
        Product result = productService.createProduct(userId, name, description, price, units, used, departmentId, imageIds);

        // Validations & Post Conditions
        verify(productDao, times(1)).createProduct(eq(userId), eq(name), eq(description), eq(price), eq(used), eq(departmentId), eq(5L), eq(6L), eq(0L), eq(units));
        assertNotNull(result);
    }

    @Test
    public void create_threeImages() {
        // Pre Conditions
        long userId = 1L;
        String name = "Product D";
        String description = "Description of Product D";
        Double price = 250.0;
        boolean used = true;
        long departmentId = 5L;
        List<Long> imageIds = Arrays.asList(5L, 6L, 7L);
        long units = 40L;

        when(productDao.createProduct(eq(userId), eq(name), eq(description), eq(price), eq(used), eq(departmentId), eq(5L), eq(6L), eq(7L), eq(units)))
                .thenReturn(new Product.Builder().build());

        // Exercise
        Product result = productService.createProduct(userId, name, description, price, units, used, departmentId, imageIds);

        // Validations & Post Conditions
        verify(productDao, times(1)).createProduct(eq(userId), eq(name), eq(description), eq(price), eq(used), eq(departmentId), eq(5L), eq(6L), eq(7L), eq(units));
        assertNotNull(result);
    }

    @Test
    public void update_noImage() {
        // Pre Conditions
        long productId = 1L;

        Product product = new Product.Builder().build();
        when(productDao.findProduct(productId)).thenReturn(Optional.of(product));

        // Exercise
        productService.updateProductPartially(productId, "Product Name", null, null, null, null, null, Collections.emptyList());

        // Validations & Post Conditions
        assertNull(product.getPrimaryPicture());
        assertNull(product.getSecondaryPicture());
        assertNull(product.getTertiaryPicture());

        verify(productDao, times(1)).findProduct(productId);
        verify(imageService, never()).findImage(anyLong());
    }

    @Test
    public void update_oneImage() {
        // Pre Conditions
        long productId = 1L;
        long imageId = 100L;

        Product product = new Product.Builder().build();
        Image primaryImage = new Image.Builder().build();
        when(productDao.findProduct(productId)).thenReturn(Optional.of(product));
        when(imageService.findImage(imageId)).thenReturn(Optional.of(primaryImage));

        // Exercise
        productService.updateProductPartially(productId, null, null, null, null, null, null, Collections.singletonList(imageId));

        // Validations & Post Conditions
        assertEquals(primaryImage, product.getPrimaryPicture());
        assertNull(product.getSecondaryPicture());
        assertNull(product.getTertiaryPicture());

        verify(productDao, times(1)).findProduct(productId);
        verify(imageService, times(1)).findImage(imageId);
    }

    @Test
    public void update_twoImages() {
        // Pre Conditions
        long productId = 1L;
        List<Long> imageIds = Arrays.asList(100L, 200L);

        Product product = new Product.Builder().build();
        Image primaryImage = new Image.Builder().build();
        Image secondaryImage = new Image.Builder().build();
        when(productDao.findProduct(productId)).thenReturn(Optional.of(product));
        when(imageService.findImage(imageIds.get(0))).thenReturn(Optional.of(primaryImage));
        when(imageService.findImage(imageIds.get(1))).thenReturn(Optional.of(secondaryImage));

        // Exercise
        productService.updateProductPartially(productId, null, null, null, null, null, null, imageIds);

        // Validations & Post Conditions
        assertEquals(primaryImage, product.getPrimaryPicture());
        assertEquals(secondaryImage, product.getSecondaryPicture());
        assertNull(product.getTertiaryPicture());

        verify(productDao, times(1)).findProduct(productId);
        verify(imageService, times(1)).findImage(imageIds.get(0));
        verify(imageService, times(1)).findImage(imageIds.get(1));
    }

    @Test
    public void update_threeImages() {
        // Pre Conditions
        long productId = 1L;
        List<Long> imageIds = Arrays.asList(100L, 200L, 300L);

        Product product = new Product.Builder().build();
        Image primaryImage = new Image.Builder().build();
        Image secondaryImage = new Image.Builder().build();
        Image tertiaryImage = new Image.Builder().build();
        when(productDao.findProduct(productId)).thenReturn(Optional.of(product));
        when(imageService.findImage(imageIds.get(0))).thenReturn(Optional.of(primaryImage));
        when(imageService.findImage(imageIds.get(1))).thenReturn(Optional.of(secondaryImage));
        when(imageService.findImage(imageIds.get(2))).thenReturn(Optional.of(tertiaryImage));

        // Exercise
        productService.updateProductPartially(productId, null, null, null, null, null, null, imageIds);

        // Validations & Post Conditions
        assertEquals(primaryImage, product.getPrimaryPicture());
        assertEquals(secondaryImage, product.getSecondaryPicture());
        assertEquals(tertiaryImage, product.getTertiaryPicture());

        verify(productDao, times(1)).findProduct(productId);
        verify(imageService, times(1)).findImage(imageIds.get(0));
        verify(imageService, times(1)).findImage(imageIds.get(1));
        verify(imageService, times(1)).findImage(imageIds.get(2));
    }
}

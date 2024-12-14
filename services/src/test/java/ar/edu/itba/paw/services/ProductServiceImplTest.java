package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.BaseChannel;
import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.models.Entities.Post;
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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {

    @Mock
    private ProductDao productDao;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    public void testCreateProductNoImages() {
        // Arrange
        long userId = 1L;
        String name = "Product A";
        String description = "Description of Product A";
        Double price = 100.0;
        boolean used = false;
        long departmentId = 2L;
        List<Long> imageIds = null; // No images
        long units = 10L;

        when(productDao.createProduct(eq(userId), eq(name), eq(description), eq(price), eq(used), eq(departmentId), eq(0L), eq(0L), eq(0L), eq(units)))
                .thenReturn(new Product.Builder().build());

        // Act
        Product result = productService.createProduct(userId, name, description, price, used, departmentId, imageIds, units);

        // Assert
        verify(productDao, times(1)).createProduct(eq(userId), eq(name), eq(description), eq(price), eq(used), eq(departmentId), eq(0L), eq(0L), eq(0L), eq(units));
        assertNotNull(result);
    }

    @Test
    public void testCreateProductOneImage() {
        // Arrange
        long userId = 1L;
        String name = "Product B";
        String description = "Description of Product B";
        Double price = 150.0;
        boolean used = true;
        long departmentId = 3L;
        List<Long> imageIds = Collections.singletonList(5L); // One image
        long units = 20L;

        when(productDao.createProduct(eq(userId), eq(name), eq(description), eq(price), eq(used), eq(departmentId), eq(5L), eq(0L), eq(0L), eq(units)))
                .thenReturn(new Product.Builder().build());

        // Act
        Product result = productService.createProduct(userId, name, description, price, used, departmentId, imageIds, units);

        // Assert
        verify(productDao, times(1)).createProduct(eq(userId), eq(name), eq(description), eq(price), eq(used), eq(departmentId), eq(5L), eq(0L), eq(0L), eq(units));
        assertNotNull(result);
    }

    @Test
    public void testCreateProductTwoImages() {
        // Arrange
        long userId = 1L;
        String name = "Product C";
        String description = "Description of Product C";
        Double price = 200.0;
        boolean used = false;
        long departmentId = 4L;
        List<Long> imageIds = Arrays.asList(5L, 6L); // Two images
        long units = 30L;

        when(productDao.createProduct(eq(userId), eq(name), eq(description), eq(price), eq(used), eq(departmentId), eq(5L), eq(6L), eq(0L), eq(units)))
                .thenReturn(new Product.Builder().build());

        // Act
        Product result = productService.createProduct(userId, name, description, price, used, departmentId, imageIds, units);

        // Assert
        verify(productDao, times(1)).createProduct(eq(userId), eq(name), eq(description), eq(price), eq(used), eq(departmentId), eq(5L), eq(6L), eq(0L), eq(units));
        assertNotNull(result);
    }

    @Test
    public void testCreateProductThreeImages() {
        // Arrange
        long userId = 1L;
        String name = "Product D";
        String description = "Description of Product D";
        Double price = 250.0;
        boolean used = true;
        long departmentId = 5L;
        List<Long> imageIds = Arrays.asList(5L, 6L, 7L); // Three images
        long units = 40L;

        when(productDao.createProduct(eq(userId), eq(name), eq(description), eq(price), eq(used), eq(departmentId), eq(5L), eq(6L), eq(7L), eq(units)))
                .thenReturn(new Product.Builder().build());

        // Act
        Product result = productService.createProduct(userId, name, description, price, used, departmentId, imageIds, units);

        // Assert
        verify(productDao, times(1)).createProduct(eq(userId), eq(name), eq(description), eq(price), eq(used), eq(departmentId), eq(5L), eq(6L), eq(7L), eq(units));
        assertNotNull(result);
    }

}

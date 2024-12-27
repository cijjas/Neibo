package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.RequestStatus;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.RequestDao;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.Entities.Request;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class RequestServiceImplTest {

    @Mock
    private RequestDao requestDao;

    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    public void testUpdateRequest_AcceptedStatus() {
        // Pre Conditions
        long neighborhoodId = 1L;
        long requestId = 2L;
        long requestStatusId = RequestStatus.ACCEPTED.getId();
        long remainingUnits = 10L;
        long requestUnits = 5L;

        Product mockProduct = mock(Product.class);
        Request mockRequest = mock(Request.class);

        when(requestDao.findRequest(neighborhoodId, requestId))
                .thenReturn(Optional.of(mockRequest));
        when(mockRequest.getProduct()).thenReturn(mockProduct);
        when(mockRequest.getUnits()).thenReturn((int) requestUnits);
        when(mockProduct.getRemainingUnits()).thenReturn(remainingUnits);

        // Exercise
        Request result = requestService.updateRequest(neighborhoodId, requestId, requestStatusId);

        // Verification & Post Conditions
        verify(requestDao, times(1)).findRequest(neighborhoodId, requestId);
        verify(mockRequest, times(1)).setStatus(RequestStatus.ACCEPTED);
        verify(mockRequest, times(1)).setPurchaseDate(any(Date.class));
        verify(mockProduct, times(1)).setRemainingUnits(remainingUnits - requestUnits);
        assertNotNull(result);
        assertEquals(mockRequest, result);
    }

    @Test
    public void testUpdateRequest_InsufficientStock() {
        // Pre Conditions
        long neighborhoodId = 1L;
        long requestId = 2L;
        long requestStatusId = RequestStatus.ACCEPTED.getId();
        long remainingUnits = 3L;
        long requestUnits = 5L;

        Product mockProduct = mock(Product.class);
        Request mockRequest = mock(Request.class);

        when(requestDao.findRequest(neighborhoodId, requestId))
                .thenReturn(Optional.of(mockRequest));
        when(mockRequest.getProduct()).thenReturn(mockProduct);
        when(mockRequest.getUnits()).thenReturn((int) requestUnits);
        when(mockProduct.getRemainingUnits()).thenReturn(remainingUnits);

        // Exercise & Validation
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                requestService.updateRequest(neighborhoodId, requestId, requestStatusId));

        assertEquals("Cant fulfill the request, not enough stock.", exception.getMessage());

        // Verification
        verify(requestDao, times(1)).findRequest(neighborhoodId, requestId);
        verify(mockRequest, never()).setStatus(RequestStatus.ACCEPTED);
        verify(mockProduct, never()).setRemainingUnits(anyLong());
    }

    @Test
    public void testUpdateRequest_DeclinedStatus() {
        // Pre Conditions
        long neighborhoodId = 1L;
        long requestId = 2L;
        long requestStatusId = RequestStatus.DECLINED.getId();

        Request mockRequest = mock(Request.class);

        when(requestDao.findRequest(neighborhoodId, requestId))
                .thenReturn(Optional.of(mockRequest));

        // Exercise
        Request result = requestService.updateRequest(neighborhoodId, requestId, requestStatusId);

        // Verification & Post Conditions
        verify(requestDao, times(1)).findRequest(neighborhoodId, requestId);
        verify(mockRequest, times(1)).setStatus(RequestStatus.DECLINED);
        assertNotNull(result);
        assertEquals(mockRequest, result);
    }

    @Test
    public void testUpdateRequest_NotFound() {
        // Pre Conditions
        long neighborhoodId = 1L;
        long requestId = 2L;
        long requestStatusId = RequestStatus.REQUESTED.getId();

        when(requestDao.findRequest(neighborhoodId, requestId))
                .thenReturn(Optional.empty());

        // Exercise & Validation
        Exception exception = assertThrows(NotFoundException.class, () ->
                requestService.updateRequest(neighborhoodId, requestId, requestStatusId));

        // Verification
        verify(requestDao, times(1)).findRequest(neighborhoodId, requestId);
    }
}

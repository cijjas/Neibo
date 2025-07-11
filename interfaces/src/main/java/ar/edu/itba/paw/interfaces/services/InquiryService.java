package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Inquiry;

import java.util.List;
import java.util.Optional;

public interface InquiryService {

    Inquiry createInquiry(long neighborhoodId, long userId, long productId, String message);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Inquiry> findInquiry(long neighborhoodId, long productId, long inquiryId);

    List<Inquiry> getInquiries(long productId, int size, int page);

    int countInquiries(long productId);

    // -----------------------------------------------------------------------------------------------------------------

    Inquiry replyInquiry(long neighborhoodId, long productId, long inquiryId, String reply);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteInquiry(long neighborhoodId, long productId, long inquiryId);
}

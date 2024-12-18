package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Inquiry;

import java.util.List;
import java.util.Optional;

public interface InquiryService {

    Inquiry createInquiry(long userId, long productId, String message);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Inquiry> findInquiry(long inquiryId);

    Optional<Inquiry> findInquiry(long neighborhoodId, long productId, long inquiryId);

    List<Inquiry> getInquiries(long neighborhoodId, long productId, int size, int page);

    int calculateInquiryPages(long productId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Inquiry replyInquiry(long inquiryId, String reply);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteInquiry(long neighborhoodId, long productId, long inquiryId);
}

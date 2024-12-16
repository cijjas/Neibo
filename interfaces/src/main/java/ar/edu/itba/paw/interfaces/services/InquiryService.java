package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Inquiry;

import java.util.List;
import java.util.Optional;

public interface InquiryService {

    Inquiry createInquiry(long userId, final long productId, final String message);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Inquiry> findInquiry(final long inquiryId);

    Optional<Inquiry> findInquiry(long neighborhoodId, long productId, final long inquiryId);

    List<Inquiry> getInquiries(long neighborhoodId, long productId, int size, int page);

    int calculateInquiryPages(long productId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Inquiry replyInquiry(final long inquiryId, final String reply);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteInquiry(final long neighborhoodId, final long productId, final long inquiryId);
}

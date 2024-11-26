package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Inquiry;

import java.util.List;
import java.util.Optional;

public interface InquiryService {

    Inquiry createInquiry(long userId, final long productId, final String message);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Inquiry> findInquiry(final long inquiryId);

    Optional<Inquiry> findInquiry(final long inquiryId, long productId, long neighborhoodId);

    List<Inquiry> getInquiries(long productId, int page, int size, long neighborhoodId);

    int calculateInquiryPages(long productId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Inquiry replyInquiry(final long inquiryId, final String reply);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteInquiry(final long inquiryId);
}

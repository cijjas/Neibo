package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.JunctionEntities.Inquiry;

import java.util.List;
import java.util.Optional;

public interface InquiryService {
    Inquiry createInquiry(final long userId, final long productId, final String message);

    void replyInquiry(final long inquiryId, final String reply);

    Optional<Inquiry> findInquiryById(final long inquiryId);

    List<Inquiry> getInquiriesByProduct(final long productId);

    List<Inquiry> getInquiriesByProductAndCriteria(long productId, int page, int size);

    int getTotalInquiryPages(long productId, int size);
}

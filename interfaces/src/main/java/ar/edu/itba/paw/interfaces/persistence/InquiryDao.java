package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.JunctionEntities.Inquiry;

import java.util.Optional;

public interface InquiryDao {
    Inquiry createInquiry(final long userId, final long productId, final String message);

    Inquiry replyInquiry(final long inquiryId, final String reply);

    Optional<Inquiry> findInquiryById(final long inquiryId);
}

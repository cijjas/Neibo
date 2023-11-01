package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.JunctionEntities.Inquiry;

public interface InquiryService {
    Inquiry createInquiry(final long userId, final long productId, final String message);

    void replyInquiry(final long inquiryId, final String reply);
}

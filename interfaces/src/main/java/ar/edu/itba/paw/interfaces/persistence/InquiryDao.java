package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Inquiry;

import java.util.List;
import java.util.Optional;

public interface InquiryDao {

    // ---------------------------------------------- INQUIRY INSERT ---------------------------------------------------

    Inquiry createInquiry(final long userId, final long productId, final String message);

    // ---------------------------------------------- INQUIRY INSERT ---------------------------------------------------

    Optional<Inquiry> findInquiryById(final long inquiryId);

    List<Inquiry> getInquiriesByProductAndCriteria(long productId, int page, int size);

    // ---------------------------------------------------

    int getInquiriesCountByProduct(long productId);
}

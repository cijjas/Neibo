package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Inquiry;

import java.util.List;
import java.util.Optional;

public interface InquiryDao {

    // ---------------------------------------------- INQUIRY INSERT ---------------------------------------------------

    Inquiry createInquiry(final long userId, final long productId, final String message);

    // ---------------------------------------------- INQUIRY INSERT ---------------------------------------------------

    Optional<Inquiry> findInquiry(final long inquiryId);

    Optional<Inquiry> findInquiry(final long inquiryId, long productId, long neighborhoodId);

    List<Inquiry> getInquiries(long productId, int page, int size);

    int countInquiries(long productId);

    // ---------------------------------------------- INQUIRY DELETE ---------------------------------------------------

    boolean deleteInquiry(final long neighborhoodId, final long productId, final long inquiryId);
}

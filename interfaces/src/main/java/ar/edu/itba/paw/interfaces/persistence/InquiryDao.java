package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Inquiry;

import java.util.List;
import java.util.Optional;

public interface InquiryDao {

    // ---------------------------------------------- INQUIRY INSERT ---------------------------------------------------

    Inquiry createInquiry(long userId, long productId, String message);

    // ---------------------------------------------- INQUIRY INSERT ---------------------------------------------------

    Optional<Inquiry> findInquiry(long inquiryId);

    Optional<Inquiry> findInquiry(long neighborhoodId, long productId, long inquiryId);

    List<Inquiry> getInquiries(long productId, int page, int size);

    int countInquiries(long productId);

    // ---------------------------------------------- INQUIRY DELETE ---------------------------------------------------

    boolean deleteInquiry(long neighborhoodId, long productId, long inquiryId);
}

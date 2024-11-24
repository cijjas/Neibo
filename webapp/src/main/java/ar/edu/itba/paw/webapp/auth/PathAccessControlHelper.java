package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Entities.*;
import ar.edu.itba.paw.webapp.controller.UserRoleController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractSecondId;

@Component
public class PathAccessControlHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(PathAccessControlHelper.class);

    private final ProductService prs;
    private final PostService ps;
    private final InquiryService is;
    private final RequestService rs;
    private final CommentService cs;
    private final BookingService bs;
    private final ReviewService rvs;
    private final AuthHelper authHelper;

    @Autowired
    public PathAccessControlHelper(ProductService prs, PostService ps, InquiryService is, RequestService rs, CommentService cs, BookingService bs, ReviewService rvs) {
        this.prs = prs;
        this.ps = ps;
        this.is = is;
        this.rs = rs;
        this.cs = cs;
        this.bs = bs;
        this.rvs = rvs;
        this.authHelper = new AuthHelper();
    }

    // The Super Admin can perform all the actions
    // A Neighborhood Administrator can perform all the actions that correspond to that specific Neighborhood

    // ------------------------------------------ GENERIC RESTRICTIONS -------------------------------------------------

    // This method isolates each Neighborhood from each other, prohibiting cross neighborhood operations
    // It also grants the Neighborhood Administrator the ability to perform all actions within its Neighborhood
    // The restriction is applied to all* entities nested in '/neighborhood'
    // * Path '/neighborhoods/*/users/*' does not enforce this method due to more complicated authentication structure
    public boolean isNeighborhoodMember(HttpServletRequest request) {
        LOGGER.info("Neighborhood Belonging Bind");

        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        // Only place where the URI does not have a fixed size
        String requestURI = request.getRequestURI();
        String[] uriParts = requestURI.split("/");
        if (uriParts.length >= 3 && uriParts[1].equals("neighborhoods")) {
            try {
                long neighborhoodId = Long.parseLong(uriParts[2]);
                return authHelper.getRequestingUserNeighborhoodId(authentication) == neighborhoodId;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return false;
    }


    // --------------------------------------------- NEIGHBORHOODS -----------------------------------------------------

    // Usage of optional Worker Query Param in '/neighborhoods' is restricted for anonymous Users
    public boolean canUseWorkerQPInNeighborhoods(Long workerId) {
        LOGGER.info("Verifying Query Params Accessibility");

        Authentication authentication = authHelper.getAuthentication();

        if (workerId == null)
            return true;

        return !authHelper.isAnonymous(authentication);
    }

    // -------------------------------------------------- USERS --------------------------------------------------------

    // Usage of List Users is limited to the Neighbors and the Administrators (they can only list for the neighborhood they belong to)
    // * Workers Neighborhood ('/neighborhoods/0') can be accessed by the Users from other Neighborhoods (except Rejected Neighborhood)
    public boolean canListUsers(long neighborhoodId) {
        LOGGER.info("Verifying User List Accessibility");

        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isAnonymous(authentication) || authHelper.isUnverifiedOrRejected(authentication))
            return false;

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        return neighborhoodId == 0 || neighborhoodId == authHelper.getRequestingUserNeighborhoodId(authentication);
    }

    // Restricted from Anonymous Users
    // Rejected and Unverified Users can access their User (self find)
    // Neighbors and Administrators can access the find for all the Users in their Neighborhood
    // Find in Worker Neighborhoods can be executed by all the registered users
    public boolean canFindUser(long neighborhoodId, long userId) {
        LOGGER.info("Verifying Detail User Accessibility");

        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isAnonymous(authentication))
            return false;

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        if (authHelper.isUnverifiedOrRejected(authentication))
            return authHelper.getRequestingUserId(authentication) == userId;

        return neighborhoodId == 0 || neighborhoodId == authHelper.getRequestingUser(authentication).getNeighborhoodId();
    }

    // Restricted from Anonymous Users
    // Rejected, Unverified, Workers and Neighbors can update their own profile
    // Administrators can update the profiles of the Neighbors in their Neighborhood
    public boolean canUpdateUser(long userId, long neighborhoodId) {
        LOGGER.info("Verifying Update User Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isAnonymous(authentication))
            return false;

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        if (authHelper.getRequestingUser(authentication).getNeighborhoodId() != neighborhoodId)
            return false;

        if (authHelper.isAdministrator(authentication))
            return true;

        return authHelper.getRequestingUserId(authentication) == userId;
    }

    // ----------------------------------------------- WORKERS ---------------------------------------------------------

    // Workers can update their own profile
    public boolean canUpdateWorker(long workerId) {
        LOGGER.info("Verifying Worker Update Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        return workerId == authHelper.getRequestingUserId(authentication);
    }

    // --------------------------------------------- PROFESSION --------------------------------------------------------

    // Worker Query Param in '/professions' can only be used by Neighbors, Workers and Administrators
    public boolean canUseWorkerQPInProfessions(String workerURN) {
        LOGGER.info("Verifying Query Params Accessibility");

        if (workerURN == null)
            return true;

        Authentication authentication = authHelper.getAuthentication();

        return !authHelper.isAnonymous(authentication) && !authHelper.isUnverifiedOrRejected(authentication);
    }

    // ------------------------------------------------- LIKES ---------------------------------------------------------

    // Restricted from Anonymous, Unverified and Rejected
    // Neighbors and Administrator can use it when specifying at least one of the Query Params
    public boolean canListLikes(String postURN, String userURN) {
        LOGGER.info("Verifying Get Likes Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        // In this case all likes in the application are returned (only Super Admin allowed)
        if (authHelper.isSuperAdministrator(authentication))
            return true;

        if (postURN == null && userURN == null)
            return false;

        if (userURN != null && postURN == null)
            return authHelper.getRequestingUserNeighborhoodId(authentication) == extractFirstId(userURN);

        if (userURN == null)
            return authHelper.getRequestingUserNeighborhoodId(authentication) == extractFirstId(postURN);

        return authHelper.getRequestingUserNeighborhoodId(authentication) == extractFirstId(postURN)
                && authHelper.getRequestingUserNeighborhoodId(authentication) == extractFirstId(userURN);
    }

    // Restricted from Anonymous, Unverified and Rejected
    // Neighbors can delete their own Likes
    // Administrators can delete the Likes of the Neighbors they monitor
    public boolean canDeleteLike(String userURN) {
        LOGGER.info("Verifying Accessibility for the User's entities");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isAnonymous(authentication) || authHelper.isUnverifiedOrRejected(authentication))
            return false;

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        if (authHelper.isAdministrator(authentication))
            return authHelper.getRequestingUserNeighborhoodId(authentication) == extractFirstId(userURN);

        return authHelper.getRequestingUserNeighborhoodId(authentication) == extractFirstId(userURN)
                && authHelper.getRequestingUserId(authentication) == extractSecondId(userURN);
    }

    // ---------------------------------------------- AFFILIATIONS -----------------------------------------------------

    // Workers can delete Affiliations with Neighborhoods if it includes them
    public boolean canDeleteAffiliation(String workerURN) {
        LOGGER.info("Verifying Delete Affiliation Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        return authHelper.getRequestingUserId(authentication) == extractFirstId(workerURN);
    }

    // Workers can update Affiliations with Neighborhoods if it includes them
    public boolean canUpdateAffiliation(String neighborhoodURN) {
        LOGGER.info("Verifying Update Affiliation Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        return authHelper.getRequestingUserNeighborhoodId(authentication) == extractFirstId(neighborhoodURN);
    }

    // ----------------------------------------------- REVIEWS ---------------------------------------------------------

    public boolean canCreateReview(long workerId, String user) {
        LOGGER.info("Verifying Review Creation Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        Optional<Review> latestReview = rvs.findLatestReview(workerId, extractSecondId(user));

        return latestReview.map(review -> ChronoUnit.HOURS.between(review.getDate().toInstant(), Instant.now()) >= 24).orElse(true);

    }

    // -----------------------------------------------------------------------------------------------------------------
    // --------------------------------------- NEIGHBORHOOD NESTED ENTITIES --------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    // ---------------------------------------------- ATTENDANCES ------------------------------------------------------

    // Neighbors can delete their own Attendance
    public boolean canDeleteAttendance(long userId) {
        LOGGER.info("Verifying Delete Attendance Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        return authHelper.getRequestingUserId(authentication) == userId;
    }

    // ------------------------------------------------ COMMENTS -------------------------------------------------------

    // Neighbors can delete their own Comments
    public boolean canDeleteComment(long commentId) {
        LOGGER.info("Verifying Delete Comment Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        Comment c = cs.findComment(commentId).orElseThrow(NotFoundException::new);
        return authHelper.getRequestingUserId(authentication) == c.getUser().getUserId();
    }

    // ------------------------------------------------ BOOKINGS -------------------------------------------------------

    // Neighbors can delete their own Booking
    public boolean canDeleteBooking(long bookingId, long neighborhoodId) {
        LOGGER.info("Verifying Booking Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        Booking b = bs.findBooking(bookingId, neighborhoodId).orElseThrow(NotFoundException::new);
        return authHelper.getRequestingUserId(authentication) == b.getUser().getUserId();
    }

    // ------------------------------------------------ PRODUCTS ----------------------------------------------------------

    // Sellers can delete their own Product
    public boolean canDeleteProduct(long productId) {
        LOGGER.info("Verifying Product Delete Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        Product p = prs.findProduct(productId).orElseThrow(NotFoundException::new);
        return p.getSeller().getUserId() == authHelper.getRequestingUserId(authentication);
    }

    // Sellers can Update their own Product
    public boolean canUpdateProduct(long productId) {
        LOGGER.info("Verifying Product Update Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        Product p = prs.findProduct(productId).orElseThrow(NotFoundException::new);
        return p.getSeller().getUserId() == authHelper.getRequestingUserId(authentication);
    }

    // ------------------------------------------------ POSTS ----------------------------------------------------------

    // Neighbors can delete their own Post
    public boolean canDeletePost(long postId) {
        LOGGER.info("Verifying Post Delete Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        Post p = ps.findPost(postId).orElseThrow(NotFoundException::new);
        return p.getUser().getUserId() == authHelper.getRequestingUserId(authentication);
    }

    // ---------------------------------------------- INQUIRIES --------------------------------------------------------

    // Sellers can not create an Inquiry for their own Product
    public boolean canCreateInquiry(long productId) {
        LOGGER.info("Verifying Inquiry Creation Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        Product p = prs.findProduct(productId).orElseThrow(NotFoundException::new);
        return p.getSeller().getUserId() != authHelper.getRequestingUserId(authentication);
    }

    // The Inquirer can delete their Inquiry
    public boolean canDeleteInquiry(long inquiryId) {
        LOGGER.info("Verifying Inquiry Delete Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        Inquiry i = is.findInquiry(inquiryId).orElseThrow(NotFoundException::new);
        return i.getUser().getUserId() == authHelper.getRequestingUserId(authentication);
    }

    // Sellers can answer the Inquiries for their own Product
    public boolean canAnswerInquiry(long inquiryId) {
        LOGGER.info("Verifying Inquiry Answering Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        Inquiry i = is.findInquiry(inquiryId).orElseThrow(NotFoundException::new);
        return i.getProduct().getSeller().getUserId() == authHelper.getRequestingUserId(authentication);
    }

    // ---------------------------------------------- REQUESTS ---------------------------------------------------------

    // Sellers can view the Requests for their Products and their Requests overall
    public boolean canAccessRequests(String userURN, String productURN) {
        LOGGER.info("Verifying Requests Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (userURN == null && productURN == null)
            return authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication);

        if (userURN != null) {
            return authHelper.getRequestingUserId(authentication) == extractSecondId(userURN);
        }

        Product product = prs.findProduct(extractSecondId(productURN)).orElseThrow(NotFoundException::new);
        return product.getSeller().getUserId() == authHelper.getRequestingUserId(authentication);
    }

    // The Seller and the Requester can access the particular Request that they take part in
    public boolean canAccessRequest(long requestId) {
        LOGGER.info("Verifying Request Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        Request r = rs.findRequest(requestId).orElseThrow(NotFoundException::new);
        return r.getUser().getUserId() == authHelper.getRequestingUserId(authentication) || r.getProduct().getSeller().getUserId() == authHelper.getRequestingUserId(authentication);
    }

    // Sellers can update their own Products
    public boolean canUpdateRequest(long requestId) {
        LOGGER.info("Verifying Update Request Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        Request r = rs.findRequest(requestId).orElseThrow(NotFoundException::new);
        return r.getProduct().getSeller().getUserId() == authHelper.getRequestingUserId(authentication);
    }

    // Requesters can delete their own Requests
    public boolean canDeleteRequest(long requestId) {
        LOGGER.info("Verifying List Transactions Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        Request r = rs.findRequest(requestId).orElseThrow(NotFoundException::new);
        return r.getUser().getUserId() == authHelper.getRequestingUserId(authentication);
    }
}

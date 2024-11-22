package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.TwoId;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractFirstId;
import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractTwoId;

@Component
public class FormAccessControlHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormAccessControlHelper.class);

    private final ProductService prs;
    private final PostService ps;
    private final InquiryService is;
    private final RequestService rs;
    private final CommentService cs;
    private final BookingService bs;
    private final UserService us;
    private final AuthHelper authHelper;

    @Autowired
    public FormAccessControlHelper(ProductService prs, PostService ps, InquiryService is, RequestService rs, CommentService cs, BookingService bs, UserService us){
        this.prs = prs;
        this.ps = ps;
        this.is = is;
        this.rs = rs;
        this.cs = cs;
        this.bs = bs;
        this.us = us;
        this.authHelper = new AuthHelper();
    }


    // ----------------------------------------------- USER REFS -------------------------------------------------------

    // Verifies that the User URN received matches the authenticated User
    // Commonly used by forms that require an author
    // Neighbors can only reference themselves whilst Administrators and the Super Admin can reference all Users
    public boolean canReferenceUserInCreation(String userURN) {
        LOGGER.info("Verifying create reference to the User's entities");

        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isAdministrator(authentication) || authHelper.isSuperAdministrator(authentication))
            return true;

        TwoId userTwoId = extractTwoId(userURN);
        us.findUser(userTwoId.getSecondId(), userTwoId.getFirstId()).orElseThrow(() -> new NotFoundException("User Not Found"));

        return authHelper.getRequestingUserId(authentication) == userTwoId.getSecondId();
    }

    public boolean canReferenceUserInUpdate(String userURN) {
        LOGGER.info("Verifying update reference to the User's entities");

        // null is an valid value when in PATCH
        if (userURN == null)
            return true;

        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isAdministrator(authentication) || authHelper.isSuperAdministrator(authentication))
            return true;

        TwoId userTwoId = extractTwoId(userURN);
        us.findUser(userTwoId.getSecondId(), userTwoId.getFirstId()).orElseThrow(() -> new NotFoundException("User Not Found"));

        return authHelper.getRequestingUserId(authentication) == userTwoId.getSecondId();
    }

    // ------------------------------------------------- LIKES ---------------------------------------------------------

    // Restricted from Anonymous, Unverified and Rejected
    // Neighbors and Administrators can reference any User that belongs to their neighborhood
    public boolean canReferenceUserInLike(String userURN){
        LOGGER.info("Verifying User Reference In Like Form");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        if (authHelper.isAnonymous(authentication) || authHelper.isUnverifiedOrRejected(authentication))
            return false;

        long neighborhoodId = extractTwoId(userURN).getFirstId();
        long userId = extractTwoId(userURN).getSecondId();
        us.findUser(userId, neighborhoodId).orElseThrow(() -> new NotFoundException("Referenced User was not found"));

        if (authHelper.isAdministrator(authentication))
            return authHelper.getRequestingUserNeighborhoodId(authentication) == extractTwoId(userURN).getFirstId();

        return authHelper.getRequestingUserId(authentication) == extractTwoId(userURN).getSecondId();
    }

    // Restricted from Anonymous, Unverified and Rejected
    // Neighbors and Administrators can reference any Post that belongs to their neighborhood
    public boolean canReferencePostInLike(String postURN){
        LOGGER.info("Verifying Post Reference In Like Form");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isAnonymous(authentication) || authHelper.isUnverifiedOrRejected(authentication))
            return false;

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        // Dangerous
        TwoId twoId = extractTwoId(postURN);
        ps.findPost(twoId.getSecondId(), twoId.getFirstId()).orElseThrow(()-> new NotFoundException("Referenced Post was not found."));
        return authHelper.getRequestingUserNeighborhoodId(authentication) == twoId.getFirstId();
    }

    // ---------------------------------------------- AFFILIATIONS -----------------------------------------------------

    // Workers can create Affiliations with Neighborhoods if it includes them
    public boolean canReferenceWorkerInAffiliation(String workerURN) {
        LOGGER.info("Verifying Create Affiliation Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        return authHelper.getRequestingUserId(authentication) == extractFirstId(workerURN);
    }

    // Admins can change the status of the affiliation, workers can only use the unverified status
    public boolean canReferenceWorkerRoleInAffiliation(String workerRoleURN){
        LOGGER.info("Verifying Worker Reference in Affiliation Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        return  extractFirstId(workerRoleURN) == WorkerRole.UNVERIFIED_WORKER.getId();
    }

    // ------------------------------------------------ REVIEWS --------------------------------------------------------

    // Neighbors can create an Attendance for themselves
    public boolean canReferenceUserInAttendance(String userURN) {
        LOGGER.info("Verifying Review Creation Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        // Dangerous
        TwoId userTwoId = extractTwoId(userURN);
        us.findUser(userTwoId.getSecondId(), userTwoId.getFirstId()).orElseThrow(() -> new NotFoundException("User Not Found"));

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        return authHelper.getRequestingUserId(authentication) == userTwoId.getSecondId();
    }

    // ---------------------------------------------- REQUESTS ---------------------------------------------------------

    // Sellers can not put a Request for his own Product
    public boolean canReferenceProductInRequest(String productURN){
        LOGGER.info("Verifying Product Reference in Request Form");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        // Dangerous
        TwoId twoId = extractTwoId(productURN);
        Product p = prs.findProduct(twoId.getSecondId(), twoId.getFirstId()).orElseThrow(()-> new NotFoundException("Product Not Found"));
        return p.getSeller().getUserId() != authHelper.getRequestingUserId(authentication);
    }
}

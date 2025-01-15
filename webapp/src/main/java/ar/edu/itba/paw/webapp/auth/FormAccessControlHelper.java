package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractSecondId;

@Component
public class FormAccessControlHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormAccessControlHelper.class);

    private final ProductService prs;
    private final UserService us;
    private final AuthHelper authHelper;

    @Autowired
    public FormAccessControlHelper(ProductService prs, UserService us) {
        this.prs = prs;
        this.us = us;
        this.authHelper = new AuthHelper();
    }

    // --------------------------------------- NEIGHBORHOOD ENTITIES REFS ----------------------------------------------

    // Verifies that the referenced entity belongs to the same Neighborhood as the User that made the request
    public boolean canReferenceNeighborhoodEntity(Long neighborhoodId) {
        LOGGER.info("Verifying entity reference");

        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isAnonymous(authentication) || authHelper.isUnverifiedOrRejected(authentication))
            return false;

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        return authHelper.getRequestingUserNeighborhoodId(authentication) == neighborhoodId || neighborhoodId == 0;
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

        return authHelper.getRequestingUserId(authentication) == extractFirstId(userURN);
    }

    public boolean canReferenceUserInUpdate(String userURN) {
        LOGGER.info("Verifying update reference to the User's entities");

        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isAdministrator(authentication) || authHelper.isSuperAdministrator(authentication))
            return true;

        return authHelper.getRequestingUserId(authentication) == extractFirstId(userURN);
    }

    public boolean canReferenceNeighborhoodUserRole(long userId, String neighborhood, String userRole){
        LOGGER.info("Verifying update reference to the User's entities");

        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        User userBeingUpdated = us.findUser(userId).orElseThrow(NotFoundException::new);

        // Idempotent cases
        long userRoleId;
        long neighborhoodId;
        if (neighborhood != null && userRole != null) {
            neighborhoodId = extractFirstId(neighborhood);
            userRoleId = extractFirstId(userRole);

            if (authHelper.isAdministrator(authentication)){
                // Admins can't change their neighborhood nor their role
                if (authHelper.getRequestingUserId(authentication) == userId)
                    return false;
                // They can change the roles of their neighbors

                if (authHelper.getRequestingUserNeighborhoodId(authentication) == userBeingUpdated.getNeighborhood().getNeighborhoodId()
                        && neighborhood == null) {

                }

            }
        }
        return true;
        // If neighborhood is specified but is the same
        // If userRole is specified but is the same
        // If both are specified but both are the same
        // if none of them is specified




        // Admin
        // Can change privileges within his neighborhood, cant change his own privileges
        // Neighbor
        // if same neighborhood is specified, then same user role has to be specified as well
        // if different neighborhood then he HAS to put his userRole to unverified


    }

    // ------------------------------------------------- LIKES ---------------------------------------------------------

    // Restricted from Anonymous, Unverified and Rejected
    // Neighbors can reference themselves
    // Administrators can reference any User that belongs to their neighborhood
    public boolean canReferenceUserInLike(String userURN) {
        LOGGER.info("Verifying User Reference In Like Form");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        if (authHelper.isAnonymous(authentication) || authHelper.isUnverifiedOrRejected(authentication))
            return false;

        long neighborhoodId = us.findUser(extractFirstId(userURN)).orElseThrow(NotFoundException::new).getNeighborhood().getNeighborhoodId();

        if (authHelper.isAdministrator(authentication))
            return authHelper.getRequestingUserNeighborhoodId(authentication) == neighborhoodId;

        return authHelper.getRequestingUserId(authentication) == extractFirstId(userURN);
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
    public boolean canReferenceWorkerRoleInAffiliation(String workerRoleURN) {
        LOGGER.info("Verifying Worker Reference in Affiliation Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        return extractFirstId(workerRoleURN) == WorkerRole.UNVERIFIED_WORKER.getId();
    }

    // ------------------------------------------------ REVIEWS --------------------------------------------------------

    // Neighbors can create an Attendance for themselves
    public boolean canReferenceUserInAttendance(String userURN) {
        LOGGER.info("Verifying Review Creation Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        return authHelper.getRequestingUserId(authentication) == extractFirstId(userURN);
    }

    // ---------------------------------------------- REQUESTS ---------------------------------------------------------

    // Sellers can not put a Request for his own Product
    public boolean canReferenceProductInRequest(String productURN) {
        LOGGER.info("Verifying Product Reference in Request Form");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        Product p = prs.findProduct(extractSecondId(productURN)).orElseThrow(NotFoundException::new);
        return p.getSeller().getUserId() != authHelper.getRequestingUserId(authentication);
    }
}

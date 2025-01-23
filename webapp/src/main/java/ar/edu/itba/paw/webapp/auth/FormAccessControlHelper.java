package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.enums.BaseNeighborhood;
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
    private final AuthHelper authHelper;
    private final UserService us;

    @Autowired
    public FormAccessControlHelper(ProductService prs, UserService us) {
        this.prs = prs;
        this.authHelper = new AuthHelper();
        this.us = us;
    }

    // ---------------------------------------------- AFFILIATIONS -----------------------------------------------------

    // Workers can create Affiliations with Neighborhoods if it includes them
    public boolean canReferenceWorkerInAffiliation(String workerURI) {
        LOGGER.info("Verifying Create Affiliation Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        return authHelper.getRequestingUserId(authentication) == extractFirstId(workerURI);
    }

    // Admins can change the status of the affiliation, workers can only use the unverified status
    public boolean canReferenceWorkerRoleInAffiliation(String workerRoleURI) {
        LOGGER.info("Verifying Worker Reference in Affiliation Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        return extractFirstId(workerRoleURI) == WorkerRole.UNVERIFIED_WORKER.getId();
    }

    // ---------------------------------------------- NEIGHBORHOODS ----------------------------------------------------

    // This method places a similar role to the isNeighborhoodMember() method, instead of verifying the request
    // we check the references made with the same idea of isolation.
    // Verifies that the referenced entity belongs to the same Neighborhood as the User that made the request
    public boolean canReferenceNeighborhoodEntity(Long neighborhoodId) {
        LOGGER.info("Verifying entity reference");

        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isAnonymous(authentication) || authHelper.isUnverifiedOrRejected(authentication))
            return false;

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        return authHelper.getRequestingUserNeighborhoodId(authentication) == neighborhoodId || neighborhoodId == BaseNeighborhood.WORKERS.getId();
    }

    // ------------------------------------------------ REVIEWS --------------------------------------------------------

    // Neighbors can create an Attendance for themselves
    public boolean canReferenceUserInAttendance(String userURI) {
        LOGGER.info("Verifying Review Creation Accessibility");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        return authHelper.getRequestingUserId(authentication) == extractFirstId(userURI);
    }

    // ------------------------------------------------- USERS  --------------------------------------------------------

    // Verifies that the User URI received matches the authenticated User
    // Commonly used by forms that require an author
    // Neighbors can only reference themselves whilst Administrators and the Super Admin can reference all Users
    public boolean canReferenceUserInCreation(String userURI) {
        LOGGER.info("Verifying create reference to the User's entities");

        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isAdministrator(authentication) || authHelper.isSuperAdministrator(authentication))
            return true;

        return authHelper.getRequestingUserId(authentication) == extractFirstId(userURI);
    }

    // Only two combinations of neighborhood and role are allowed in creation
    // Worker Neighborhood & Worker Role
    // Non-Special Neighborhood & Unverified Role
    public boolean canCreateUser(String neighborhood, String userRole) {
        LOGGER.info("Verifying combination of Neighborhood and User Role");

        Authentication authentication = authHelper.getAuthentication();

        if (!authHelper.isAnonymous(authentication) && authHelper.isSuperAdministrator(authentication))
            return true;

        long neighborhoodId = extractFirstId(neighborhood);
        long userRoleId = extractFirstId(userRole);

        return (neighborhoodId == BaseNeighborhood.WORKERS.getId() && userRoleId == UserRole.WORKER.getId()) || (!BaseNeighborhood.isABaseNeighborhood(neighborhoodId) && userRoleId == UserRole.UNVERIFIED_NEIGHBOR.getId());
    }

    // Quite complex
    public boolean canUpdateUser(long userId, String neighborhood, String userRole){
        LOGGER.info("Verifying combination of Neighborhood and User Role");

        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isAnonymous(authentication))
            return false;

        if (authHelper.isSuperAdministrator(authentication))
            return true;

        long userRoleId;
        long neighborhoodId;
        User userBeingUpdated = us.findUser(userId).orElseThrow(NotFoundException::new);

        // Nothing was specified
        if (neighborhood == null && userRole == null){
            neighborhoodId = us.findUser(userId).orElseThrow(NotFoundException::new).getNeighborhood().getNeighborhoodId();

            if (authHelper.getRequestingUser(authentication).getNeighborhoodId() != neighborhoodId)
                return false;

            if (authHelper.isAdministrator(authentication))
                return true;

            return authHelper.getRequestingUserId(authentication) == userId;
        }

        // Both attributes where specified
        if (neighborhood != null && userRole != null) {
            neighborhoodId = extractFirstId(neighborhood);
            userRoleId = extractFirstId(userRole);

            if (authHelper.isAdministrator(authentication)){
                // Admins can't change their own Neighborhood nor their role
                if (authHelper.getRequestingUserId(authentication) == userId)
                    return false;
                // If they are changing another Users Neighborhood and User Role:
                // The User has to belong to the Neighborhood monitored by the Admin
                // The Neighborhood specified has to be the same, as the Admin cant migrate Users, or it can be the Rejected Neighborhood
                // The Role specified has to be Neighbor, Unverified or Rejected
                return authHelper.getRequestingUserNeighborhoodId(authentication) == userBeingUpdated.getNeighborhood().getNeighborhoodId()
                        && (neighborhoodId == userBeingUpdated.getNeighborhood().getNeighborhoodId() || neighborhoodId == BaseNeighborhood.REJECTED.getId())
                        && (userRoleId == UserRole.REJECTED.getId() || userRoleId == UserRole.NEIGHBOR.getId() || userRoleId == UserRole.UNVERIFIED_NEIGHBOR.getId());
            }
            if (authHelper.isNeighbor(authentication) || authHelper.isUnverifiedOrRejected(authentication)){
                // Neighbors and Unverified Neighbors, can't alter other Users
                if (authHelper.getRequestingUserId(authentication) != userId)
                    return false;
                // They can change their Neighborhood but downgrading their privileges
                return userRoleId == UserRole.UNVERIFIED_NEIGHBOR.getId() && neighborhoodId != authHelper.getRequestingUserNeighborhoodId(authentication) && !BaseNeighborhood.isABaseNeighborhood(neighborhoodId);
            }
        }

        // Only User Role was specified
        if (neighborhood == null) {
            userRoleId = extractFirstId(userRole);
            if (authHelper.isAdministrator(authentication)){
                // Admins can't change their own role
                if (authHelper.getRequestingUserId(authentication) == userId)
                    return false;
                // If they are changing another Users User Role:
                // The User has to belong to the Neighborhood monitored by the Admin
                // The Role specified has to be Neighbor, Unverified or Rejected
                return authHelper.getRequestingUserNeighborhoodId(authentication) == userBeingUpdated.getNeighborhood().getNeighborhoodId()
                        && (userRoleId == UserRole.REJECTED.getId() || userRoleId == UserRole.NEIGHBOR.getId() || userRoleId == UserRole.UNVERIFIED_NEIGHBOR.getId());
            }
            if (authHelper.isNeighbor(authentication) || authHelper.isUnverifiedOrRejected(authentication)) {
                // Neighbors, Unverified Neighbors and Rejected, can't alter other users
                if (authHelper.getRequestingUserId(authentication) != userId) {
                    return false;
                }
                // They can change their Role but downgrading their privileges
                return userRoleId == UserRole.UNVERIFIED_NEIGHBOR.getId();
            }
        }

        // Only Neighborhood was specified
        if (userRole == null){
            neighborhoodId = extractFirstId(neighborhood);
            // Admins can't change their Neighborhood, and Neighbors can only if they also downgrade their role (first case)
            if (authHelper.isAdministrator(authentication) || authHelper.isNeighbor(authentication)){
                return false;
            }
            if (authHelper.isUnverifiedOrRejected(authentication)) {
                // Unverified and Rejected Users cant alter other users
                if (authHelper.getRequestingUserId(authentication) != userId) {
                    return false;
                }
                // They can change their Neighborhood as they would not be achieving any privilege escalation in the new neighborhood
                return neighborhoodId != authHelper.getRequestingUserNeighborhoodId(authentication) && !BaseNeighborhood.isABaseNeighborhood(neighborhoodId);
            }
        }
        return false;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // --------------------------------------- NEIGHBORHOOD NESTED ENTITIES --------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    /// In neighborhood nested entities the neighborhood biding ensures the requesting user belongs to that neighborhood
    /// so if the user has role Administrator we can be sure that its that neighborhood's Administrator.
    /// Same applies for Users, we can always be sure that the requesting user belongs to the correct Neighborhood.

    // ------------------------------------------------- LIKES ---------------------------------------------------------

    // Restricted from Anonymous, Unverified and Rejected
    // Neighbors can reference themselves
    // Administrators can reference any User that belongs to their neighborhood
    public boolean canReferenceUserInLike(String userURI) {
        LOGGER.info("Verifying User Reference In Like Form");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        if (authHelper.isAnonymous(authentication) || authHelper.isUnverifiedOrRejected(authentication))
            return false;

        return authHelper.getRequestingUserId(authentication) == extractFirstId(userURI);
    }

    // ---------------------------------------------- REQUESTS ---------------------------------------------------------

    // Sellers can not put a Request for his own Product
    public boolean canReferenceProductInRequest(String productURI) {
        LOGGER.info("Verifying Product Reference in Request Form");
        Authentication authentication = authHelper.getAuthentication();

        if (authHelper.isSuperAdministrator(authentication) || authHelper.isAdministrator(authentication))
            return true;

        Product p = prs.findProduct(extractSecondId(productURI)).orElseThrow(NotFoundException::new);
        return p.getSeller().getUserId() != authHelper.getRequestingUserId(authentication);
    }
}

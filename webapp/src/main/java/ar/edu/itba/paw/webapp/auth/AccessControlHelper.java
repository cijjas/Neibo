package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.enums.Authority;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Entities.*;
import ar.edu.itba.paw.models.TwoId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AccessControlHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessControlHelper.class);

    @Autowired
    private ProductService prs;

    @Autowired
    private PostService ps;

    @Autowired
    private InquiryService is;

    @Autowired
    private RequestService rs;

    @Autowired
    private CommentService cs;

    @Autowired
    private BookingService bs;

    @Autowired
    private UserService us;
/*
* Revisar si no se puede verificar el uso de query params en la filter chain, creo que no seria practico esto
* Tal vez se puede mejorar la filter chain para disminuir la logica de los access control helpers a lo estrictamente necesario
* por ejemplo que haya un permit all si sos super admin
* que dentro de neighborhoods admins tengan el permit all
*
*
 * Verificar con los tests
* */

    // The Super Admin can perform all the actions
    // A Neighborhood Administrator can perform all the actions that correspond to that specific Neighborhood

    // ------------------------------------------ GENERIC RESTRICTIONS -------------------------------------------------

    // This method isolates each Neighborhood from each other, prohibiting cross neighborhood operations
    // It also grants the Neighborhood Administrator the ability to perform all actions within its Neighborhood
    // The restriction is applied to all* entities nested in '/neighborhood'
    // * Path '/neighborhoods/*/users/*' does not enforce this method due to more complicated authentication structure
    public boolean isNeighborhoodMember(HttpServletRequest request) {
        LOGGER.info("Neighborhood Belonging Bind");

        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication))
            return true;

        // Only place where the URI does not have a fixed size
        String requestURI = request.getRequestURI();
        String[] uriParts = requestURI.split("/");
        if (uriParts.length >= 3 && uriParts[1].equals("neighborhoods")) {
            try {
                long neighborhoodId = Long.parseLong(uriParts[2]);
                return getRequestingUserNeighborhoodId(authentication) == neighborhoodId;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return false;
    }

    // Verifies that the User URN received matches the authenticated User
    // Commonly used by forms that require an author
    // Neighbors can only reference themselves whilst Administrators and the Super Admin can reference all Users
    public boolean canReferenceUser(String userURN) {
        LOGGER.info("Verifying Accessibility for the User's entities");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication) || isSuperAdministrator(authentication))
            return true;

        TwoId userTwoId = extractTwoURNIds(userURN);
        us.findUser(userTwoId.getSecondId(), userTwoId.getFirstId()).orElseThrow(() -> new NotFoundException("User Not Found"));

        return getRequestingUserId(authentication) == userTwoId.getSecondId();
    }

    // --------------------------------------------- NEIGHBORHOODS -----------------------------------------------------

    // Usage of Worker Query Param in '/neighborhoods' is restricted for anonymous Users
    public boolean canUseWorkerQPInNeighborhoods(Long workerId) {
        LOGGER.info("Verifying Query Params Accessibility");

        Authentication authentication = getAuthentication();

        if (workerId == null)
            return true;

        return !isAnonymous(authentication);
    }

    // -------------------------------------------------- USERS --------------------------------------------------------

    // Usage of List Users is limited to the Neighbors and the Administrators (they can only list for the neighborhood they belong to)
    // * Workers Neighborhood ('/neighborhoods/0') can be accessed by the Users from other Neighborhoods (except Rejected Neighborhood)
    public boolean canListUsers(long neighborhoodId) {
        LOGGER.info("Verifying User List Accessibility");

        Authentication authentication = getAuthentication();

        if (isAnonymous(authentication) || isUnverifiedOrRejected(authentication))
            return false;

        if (isSuperAdministrator(authentication)){
            return true;
        }

        return neighborhoodId == 0 || neighborhoodId == getRequestingUserNeighborhoodId(authentication);
    }

    // Restricted from Anonymous Users
    // Rejected and Unverified Users can access their User Find
    // Neighbors and Administrators can access the find for all the Users in their Neighborhood
    public boolean canFindUser(long neighborhoodId, long userId) {
        LOGGER.info("Verifying Detail User Accessibility");

        Authentication authentication = getAuthentication();

        if (isAnonymous(authentication))
            return false;

        if (isSuperAdministrator(authentication))
            return true;

        if (isUnverifiedOrRejected(authentication))
            return getRequestingUserId(authentication) == userId;

        return neighborhoodId == 0 || neighborhoodId == getRequestingUser(authentication).getNeighborhoodId();
    }

    // Restricted from Anonymous Users
    // Rejected, Unverified and Neighbors can update their own profile
    // Administrators can update the profiles of the Neighbors in their Neighborhood
    public boolean canUpdateUser(long userId, long neighborhoodId) {
        LOGGER.info("Verifying Update User Accessibility");
        Authentication authentication = getAuthentication();

        if (isAnonymous(authentication))
            return false;

        if (isSuperAdministrator(authentication))
            return true;

        if (getRequestingUser(authentication).getNeighborhoodId() != neighborhoodId)
            return false;

        if (isAdministrator(authentication))
            return true;

        return getRequestingUserId(authentication) == userId;
    }

    // ----------------------------------------------- WORKERS ---------------------------------------------------------

    // Workers can update their own profile
    public boolean canUpdateWorker(long workerId){
        LOGGER.info("Verifying Worker Update Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication))
            return true;

        return workerId == getRequestingUserId(authentication);
    }

    // --------------------------------------------- PROFESSION --------------------------------------------------------

    // Worker Query Param in '/professions' can only be used by Neighbors, Workers and Administrators
    public boolean canUseWorkerQPInProfessions(String workerURN) {
        LOGGER.info("Verifying Query Params Accessibility");

        if (workerURN == null)
            return true;

        Authentication authentication = getAuthentication();

        return !isAnonymous(authentication) && !isUnverifiedOrRejected(authentication);
    }

    // ------------------------------------------------- LIKES ---------------------------------------------------------

    // Restricted from Anonymous, Unverified and Rejected
    // Neighbors and Administrators can reference any User that belongs to their neighborhood
    public boolean canReferenceUserInLikeForm(String userURN){
        LOGGER.info("Verifying User Reference In Like Form");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication))
            return true;

        if (isAnonymous(authentication) || isUnverifiedOrRejected(authentication))
            return false;

        long neighborhoodId = extractTwoURNIds(userURN).getFirstId();
        long userId = extractTwoURNIds(userURN).getSecondId();
        us.findUser(userId, neighborhoodId).orElseThrow(() -> new NotFoundException("Referenced User was not found"));

        if (isAdministrator(authentication))
            return getRequestingUserNeighborhoodId(authentication) == extractTwoURNIds(userURN).getFirstId();

        return getRequestingUserId(authentication) == extractTwoURNIds(userURN).getSecondId();
    }

    // Restricted from Anonymous, Unverified and Rejected
    // Neighbors and Administrators can reference any Post that belongs to their neighborhood
    public boolean canReferencePostInLikeForm(String postURN){
        LOGGER.info("Verifying Post Reference In Like Form");
        Authentication authentication = getAuthentication();

        if (isAnonymous(authentication) || isUnverifiedOrRejected(authentication))
            return false;

        if (isSuperAdministrator(authentication))
            return true;

        TwoId twoId = extractTwoURNIds(postURN);
        ps.findPost(twoId.getSecondId(), twoId.getFirstId()).orElseThrow(()-> new NotFoundException("Referenced Post was not found."));
        return getRequestingUserNeighborhoodId(authentication) == twoId.getFirstId();
    }

    // Restricted from Anonymous, Unverified and Rejected
    // Neighbors and Administrator can use it when specifying at least one of the Query Params
    public boolean canListLikes(String postURN, String userURN) {
        LOGGER.info("Verifying Get Likes Accessibility");
        Authentication authentication = getAuthentication();

        // In this case all likes in the application are returned (only Super Admin allowed)
        if (postURN == null && userURN == null)
            return isSuperAdministrator(authentication);

        if (isSuperAdministrator(authentication))
            return true;


        TwoId postTwoId;
        TwoId userTwoId;
        if (userURN != null && postURN == null) {
            userTwoId = extractTwoURNIds(userURN);
            us.findUser(userTwoId.getSecondId(), userTwoId.getFirstId()).orElseThrow(()-> new NotFoundException("User Not Found"));
            return getRequestingUserNeighborhoodId(authentication) == userTwoId.getFirstId();
        }

        if (userURN == null) {
            postTwoId = extractTwoURNIds(postURN);
            ps.findPost(postTwoId.getSecondId(), postTwoId.getFirstId()).orElseThrow(()-> new NotFoundException("Post Not Found"));
            return getRequestingUserNeighborhoodId(authentication) == postTwoId.getFirstId();
        }

        userTwoId = extractTwoURNIds(userURN);
        us.findUser(userTwoId.getSecondId(), userTwoId.getFirstId()).orElseThrow(()-> new NotFoundException("User Not Found"));
        postTwoId = extractTwoURNIds(postURN);
        ps.findPost(postTwoId.getSecondId(), postTwoId.getFirstId()).orElseThrow(()-> new NotFoundException("Post Not Found"));

        return getRequestingUserNeighborhoodId(authentication) == postTwoId.getFirstId()
                &&  getRequestingUserNeighborhoodId(authentication) == userTwoId.getFirstId();
    }

    // Restricted from Anonymous, Unverified and Rejected
    // Neighbors can delete their own Likes
    // Administrators can delete the Likes of the Neighbors they monitor
    public boolean canDeleteLike(String userURN) {
        LOGGER.info("Verifying Accessibility for the User's entities");
        Authentication authentication = getAuthentication();

        TwoId userTwoId = extractTwoURNIds(userURN);
        us.findUser(userTwoId.getSecondId(), userTwoId.getFirstId()).orElseThrow(() -> new NotFoundException("User Not Found"));

        if (isAnonymous(authentication) || isUnverifiedOrRejected(authentication))
            return false;

        if (isSuperAdministrator(authentication))
            return true;

        TwoId twoId = extractTwoURNIds(userURN);

        if (isAdministrator(authentication))
            return getRequestingUserNeighborhoodId(authentication) == twoId.getFirstId();

        return getRequestingUserNeighborhoodId(authentication) == twoId.getFirstId() && getRequestingUserId(authentication) == twoId.getSecondId();
    }

    // ---------------------------------------------- AFFILIATIONS -----------------------------------------------------

    // Workers can create Affiliations with Neighborhoods if it includes them
    public boolean canCreateAffiliation(String workerURN) {
        LOGGER.info("Verifying Create Affiliation Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication))
            return true;

        return getRequestingUserId(authentication) == extractURNId(workerURN);
    }

    // todo: Only SuperAdmins can create affiliations with a worker role that is not unverified
    public boolean canReferenceWorkerRoleInAffiliationForm(String workerRoleURN){

        return true;
    }

    // Workers can delete Affiliations with Neighborhoods if it includes them
    public boolean canDeleteAffiliation(String workerURN) {
        LOGGER.info("Verifying Delete Affiliation Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication))
            return true;

        return getRequestingUserId(authentication) == extractURNId(workerURN);
    }

    // Workers can update Affiliations with Neighborhoods if it includes them
    public boolean canUpdateAffiliation(String neighborhoodURN) {
        LOGGER.info("Verifying Update Affiliation Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication))
            return true;

        return getRequestingUserNeighborhoodId(authentication) == extractURNId(neighborhoodURN);
    }

    // ------------------------------------------------ REVIEWS --------------------------------------------------------

    // Neighbors can create an Attendance for themselves
    public boolean canCreateReview(String userURN) {
        LOGGER.info("Verifying Review Creation Accessibility");
        Authentication authentication = getAuthentication();

        TwoId userTwoId = extractTwoURNIds(userURN);
        us.findUser(userTwoId.getSecondId(), userTwoId.getFirstId()).orElseThrow(() -> new NotFoundException("User Not Found"));

        if (isSuperAdministrator(authentication))
            return true;

        return getRequestingUserId(authentication) == userTwoId.getSecondId();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // --------------------------------------- NEIGHBORHOOD NESTED ENTITIES --------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    // ---------------------------------------------- ATTENDANCES ------------------------------------------------------

    // Neighbors can delete their own Attendance
    public boolean canDeleteAttendance(long userId) {
        LOGGER.info("Verifying Delete Attendance Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication) || isAdministrator(authentication))
            return true;

        return getRequestingUserId(authentication) == userId;
    }

    // ------------------------------------------------ COMMENTS -------------------------------------------------------

    // Neighbors can delete their own Comments
    public boolean canDeleteComment(long commentId) {
        LOGGER.info("Verifying Delete Comment Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication) || isAdministrator(authentication))
            return true;

        Comment c = cs.findComment(commentId).orElseThrow(()-> new NotFoundException("Comment Not Found"));
        return getRequestingUserId(authentication) == c.getUser().getUserId();
    }

    // ------------------------------------------------ BOOKINGS -------------------------------------------------------

    // Neighbors can delete their own Booking
    public boolean canDeleteBooking(long bookingId, long neighborhoodId) {
        LOGGER.info("Verifying Booking Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication) || isAdministrator(authentication))
            return true;

        Booking b = bs.findBooking(bookingId, neighborhoodId).orElseThrow(()-> new NotFoundException("Booking Not Found"));
        return getRequestingUserId(authentication) == b.getUser().getUserId();
    }

    // ------------------------------------------------ PRODUCTS ----------------------------------------------------------

    // Sellers can delete their own Product
    public boolean canDeleteProduct(long productId){
        LOGGER.info("Verifying Product Delete Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication) || isAdministrator(authentication))
            return true;

        Product p = prs.findProduct(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));
        return p.getSeller().getUserId() == getRequestingUserId(authentication);
    }

    // Sellers can Update their own Product
    public boolean canUpdateProduct(long productId){
        LOGGER.info("Verifying Product Update Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication) || isAdministrator(authentication))
            return true;

        Product p = prs.findProduct(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));
        return p.getSeller().getUserId() == getRequestingUserId(authentication);
    }

    // ------------------------------------------------ POSTS ----------------------------------------------------------

    // Neighbors can delete their own Post
    public boolean canDeletePost(long postId){
        LOGGER.info("Verifying Post Delete Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication) || isAdministrator(authentication))
            return true;

        Post p = ps.findPost(postId).orElseThrow(()-> new NotFoundException("Post Not Found"));
        return p.getUser().getUserId() == getRequestingUserId(authentication);
    }

    // ---------------------------------------------- INQUIRIES --------------------------------------------------------

    // Sellers can not create an Inquiry for their own Product
    public boolean canCreateInquiry(long productId){
        LOGGER.info("Verifying Inquiry Creation Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication) || isAdministrator(authentication))
            return true;

        Product p = prs.findProduct(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));
        return p.getSeller().getUserId() != getRequestingUserId(authentication);
    }

    // The Inquirer can delete their Inquiry
    public boolean canDeleteInquiry(long inquiryId){
        LOGGER.info("Verifying Inquiry Delete Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication) || isAdministrator(authentication))
            return true;

        Inquiry i = is.findInquiry(inquiryId).orElseThrow(()-> new NotFoundException("Inquiry Not Found"));
        return i.getUser().getUserId() == getRequestingUserId(authentication);
    }

    // Sellers can answer the Inquiries for their own Product
    public boolean canAnswerInquiry(long inquiryId){
        LOGGER.info("Verifying Inquiry Answering Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication) || isAdministrator(authentication))
            return true;

        Inquiry i = is.findInquiry(inquiryId).orElseThrow(()-> new NotFoundException("Inquiry Not Found"));
        return i.getProduct().getSeller().getUserId() == getRequestingUserId(authentication);
    }

    // ---------------------------------------------- REQUESTS ---------------------------------------------------------

    // Sellers can view the Requests for their Products and their Requests overall
    public boolean canAccessRequests(String userURN, String productURN){
        LOGGER.info("Verifying Requests Accessibility");
        Authentication authentication = getAuthentication();

        if (userURN == null && productURN == null)
            return isSuperAdministrator(authentication) || isAdministrator(authentication);

        if (userURN != null) {
            TwoId userTwoId = extractTwoURNIds(userURN);
            us.findUser(userTwoId.getSecondId(), userTwoId.getFirstId()).orElseThrow(() -> new NotFoundException("User Not Found"));
            return getRequestingUserId(authentication) == extractTwoURNIds(userURN).getSecondId();
        }

        TwoId productTwoId = extractTwoURNIds(productURN);
        Product product = prs.findProduct(productTwoId.getSecondId(), productTwoId.getFirstId()).orElseThrow(() -> new NotFoundException("Product Not Found"));
        return product.getSeller().getUserId() == getRequestingUserId(authentication);
    }

    // The Seller and the Requester can access the particular Request that they take part in
    public boolean canAccessRequest(long requestId){
        LOGGER.info("Verifying Request Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication) || isAdministrator(authentication))
            return true;

        Request r = rs.findRequest(requestId).orElseThrow(()-> new NotFoundException("Request Not Found"));
        return r.getUser().getUserId() == getRequestingUserId(authentication)|| r.getProduct().getSeller().getUserId() == getRequestingUserId(authentication);
    }

    // Sellers can not put a Request for his own Product
    public boolean canReferenceProductInRequestForm(String productURN){
        LOGGER.info("Verifying Product Reference in Request Form");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication) || isAdministrator(authentication))
            return true;

        TwoId twoId = extractTwoURNIds(productURN);
        Product p = prs.findProduct(twoId.getSecondId(), twoId.getFirstId()).orElseThrow(()-> new NotFoundException("Product Not Found"));
        return p.getSeller().getUserId() != getRequestingUserId(authentication);
    }

    // Sellers can update their own Products
    public boolean canUpdateRequest(long requestId){
        LOGGER.info("Verifying Update Request Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication) || isAdministrator(authentication))
            return true;

        Request r = rs.findRequest(requestId).orElseThrow(()-> new NotFoundException("Request Not Found"));
        return r.getProduct().getSeller().getUserId() == getRequestingUserId(authentication);
    }

    // Requesters can delete their own Requests
    public boolean canDeleteRequest(long requestId){
        LOGGER.info("Verifying List Transactions Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication) || isAdministrator(authentication))
            return true;

        Request r = rs.findRequest(requestId).orElseThrow(()-> new NotFoundException("Request Not Found"));
        return r.getUser().getUserId() == getRequestingUserId(authentication);
    }

    // ------------------------------------------------ HELPERS --------------------------------------------------------

    private Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean isUnverifiedOrRejected(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(Authority.ROLE_UNVERIFIED_NEIGHBOR.name()) ||
                        authority.getAuthority().equals(Authority.ROLE_REJECTED.name()));
    }

    private boolean isAnonymous(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private boolean isAdministrator(Authentication authentication){
        return getRequestingUser(authentication).getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(Authority.ROLE_ADMINISTRATOR.name()));
    }

    private boolean isSuperAdministrator(Authentication authentication){
        return getRequestingUser(authentication).getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(Authority.ROLE_SUPER_ADMINISTRATOR.name()));
    }

    private long getRequestingUserId(Authentication authentication){
        return getRequestingUser(authentication).getUserId();
    }

    private long getRequestingUserNeighborhoodId(Authentication authentication){
        return getRequestingUser(authentication).getNeighborhoodId();
    }

    private UserAuth getRequestingUser(Authentication authentication){
        return (UserAuth) authentication.getPrincipal();
    }

    private static long extractURNId(String URN) {
        String[] URNParts = URN.split("/");
        if (URNParts.length < 5) { // Check if there are enough parts for an ID
            throw new IllegalArgumentException("Invalid URN format.");
        }

        return Long.parseLong(URNParts[4]);
    }

    public static TwoId extractTwoURNIds(String URN) {
        String[] URNParts = URN.split("/");
        if (URNParts.length < 7) { // Check if there are enough parts for two IDs
            throw new IllegalArgumentException("Invalid URN format.");
        }

        long firstId = Long.parseLong(URNParts[4]);
        long secondId = Long.parseLong(URNParts[6]);

        return new TwoId(firstId, secondId);
    }
}

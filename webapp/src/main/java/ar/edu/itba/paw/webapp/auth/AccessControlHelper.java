package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.enums.Authority;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Entities.*;
import ar.edu.itba.paw.models.TwoIds;
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
    private ProductService ps;

    @Autowired
    private PostService postService;

    @Autowired
    private InquiryService is;

    @Autowired
    private RequestService rs;

    @Autowired
    private CommentService cs;

    @Autowired
    private BookingService bs;

    // ------------------------------------------ GENERIC RESTRICTIONS -------------------------------------------------

    // All requests that correspond to a certain Neighborhood have to be done from a User from that same Neighborhood
    public boolean isNeighborhoodMember(HttpServletRequest request) {
        LOGGER.info("Neighborhood Belonging Bind");

        Authentication authentication = getAuthentication();

        if (isAnonymous(authentication))
            return false;

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        if (isSuperAdministrator(authentication)){
            return true;
        }

        if (isUnverifiedOrRejected(authentication))
            return false;

        Long userNeighborhoodId = userAuth.getNeighborhoodId();

        String requestURI = request.getRequestURI();
        String[] uriParts = requestURI.split("/");

        if (uriParts.length >= 3 && uriParts[1].equals("neighborhoods")) {
            try {
                Long neighborhoodIdFromURL = Long.parseLong(uriParts[2]);
                return userNeighborhoodId.equals(neighborhoodIdFromURL);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return false;
    }

    // A User can modify the things he creates
    public Boolean canModify(String userURN) {
        LOGGER.info("Verifying Accessibility for the User's entities");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication) || isSuperAdministrator(authentication))
            return true;

        // maybe this should verify the neighborhood id as well :/

        return getRequestingUserId(authentication) == extractTwoURNIds(userURN).getSecondId();
    }

    // --------------------------------------------- NEIGHBORHOODS -----------------------------------------------------

    // Workers, Neighbors and Administrators can access /neighborhoods using Query Params
    public boolean hasAccessNeighborhoodQP(Long workerId) {
        LOGGER.info("Verifying Query Params Accessibility");

        Authentication authentication = getAuthentication();

        if (workerId == null)
            return true;

        return !isAnonymous(authentication);
    }

    // -------------------------------------------------- USERS --------------------------------------------------------

    // Neighbors and Administrators can access the whole user list and the workers user list
    public boolean hasAccessToUserList(long neighborhoodId) {
        LOGGER.info("Verifying User List Accessibility");

        Authentication authentication = getAuthentication();

        if (isAnonymous(authentication) || isUnverifiedOrRejected(authentication))
            return false;

        if (isSuperAdministrator(authentication)){
            return true;
        }

        return neighborhoodId == 0 || neighborhoodId == getRequestingUserNeighborhoodId(authentication);
    }

    // Neighbors and Administrators can access the user details of others
    public boolean hasAccessToUserDetail(long neighborhoodId, long userId) {
        LOGGER.info("Verifying Detail User Accessibility");

        Authentication authentication = getAuthentication();


        if (isAnonymous(authentication))
            return false;

        if (isSuperAdministrator(authentication)){
            return true;
        }

        if (isUnverifiedOrRejected(authentication))
            return getRequestingUserId(authentication) == userId;


        return neighborhoodId == 0 || neighborhoodId == getRequestingUser(authentication).getNeighborhoodId();
    }

    // A user can update his own profile but only an Administrator can update other people's profile
    public Boolean canUpdateUser(long userId, long neighborhoodId) {
        LOGGER.info("Verifying Update User Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication)){
            return true;
        }

        if (getRequestingUser(authentication).getNeighborhoodId() != neighborhoodId)
            return false;

        if (isAdministrator(authentication))
            return true;

        return getRequestingUserId(authentication) == userId;
    }

    // ----------------------------------------------- WORKERS ---------------------------------------------------------

    public Boolean canUpdateWorker(long workerId){
        LOGGER.info("Verifying Worker Update Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication))
            return true;

        return workerId == getRequestingUserId(authentication);
    }

    // --------------------------------------------- PROFESSION --------------------------------------------------------

    // Workers, Neighbors and Administrators can access /neighborhoods using Query Params
    public boolean hasAccessProfessionsQP(String worker) {
        LOGGER.info("Verifying Query Params Accessibility");

        if (worker == null)
            return true;

        Authentication authentication = getAuthentication();

        return !isAnonymous(authentication) && !isUnverifiedOrRejected(authentication);
    }

    // ------------------------------------------------- LIKES ---------------------------------------------------------

    // A User can modify the things he creates
    public Boolean canDeleteLike(String userURN) {
        LOGGER.info("Verifying Accessibility for the User's entities");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication) || isSuperAdministrator(authentication))
            return true;

        // maybe this should verify the neighborhood id as well :/

        return getRequestingUserId(authentication) == extractTwoURNIds(userURN).getSecondId();
    }

    // ---------------------------------------------- ATTENDANCES ------------------------------------------------------

    // A user can create/delete an attendance for themselves, or an Administrator for anyone
    public Boolean canDeleteAttendance(long userId) {
        LOGGER.info("Verifying Delete Attendance Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication) || isSuperAdministrator(authentication))
            return true;

        return getRequestingUserId(authentication) == userId;
    }

    // ---------------------------------------------- AFFILIATIONS -----------------------------------------------------

    public Boolean canCreateAffiliation(String workerURN) {
        LOGGER.info("Verifying Create Affiliation Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication))
            return true;

        return getRequestingUserId(authentication) == extractURNId(workerURN);
    }

    public Boolean canDeleteAffiliation(String workerURN) {
        LOGGER.info("Verifying Delete Affiliation Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication))
            return true;

        return getRequestingUserId(authentication) == extractURNId(workerURN);
    }

    public Boolean canUpdateAffiliation(String neighborhoodURN) {
        LOGGER.info("Verifying Update Affiliation Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication))
            return true;

        return getRequestingUserNeighborhoodId(authentication) == extractURNId(neighborhoodURN);
    }

    // ------------------------------------------------ COMMENTS -------------------------------------------------------

    // A user can create/delete an attendance for themselves, or an Administrator for anyone
    public Boolean canDeleteComment(long commentId) {
        LOGGER.info("Verifying Delete Comment Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication) || isSuperAdministrator(authentication))
            return true;

        Comment c = cs.findComment(commentId).orElseThrow(NotFoundException::new);

        return getRequestingUserId(authentication) == c.getUser().getUserId();
    }

    // ------------------------------------------------ BOOKINGS -------------------------------------------------------

    // A user can create/delete an attendance for themselves, or an Administrator for anyone
    public Boolean canDeleteBooking(long bookingId, long neighborhoodId) {
        LOGGER.info("Verifying Booking Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication) || isSuperAdministrator(authentication))
            return true;

        Booking b = bs.findBooking(bookingId, neighborhoodId).orElseThrow(NotFoundException::new);

        return getRequestingUserId(authentication) == b.getUser().getUserId();
    }

    // ------------------------------------------------ REVIEWS --------------------------------------------------------

    // A user can create/delete an attendance for themselves, or an Administrator for anyone
    public Boolean canCreateReview(String userURN) {
        LOGGER.info("Verifying Review Creation Accessibility");
        Authentication authentication = getAuthentication();

        if (isSuperAdministrator(authentication))
            return true;

        long userId = extractTwoURNIds(userURN).getSecondId();

        return getRequestingUserId(authentication) == userId;
    }

    // ------------------------------------------------ PRODUCTS ----------------------------------------------------------

    // Only the owner of the product and the Administrator can delete the product
    public Boolean canDeleteProduct(long productId){
        LOGGER.info("Verifying Product Delete Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication) || isSuperAdministrator(authentication))
            return true;

        Product p = ps.findProduct(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));

        return p.getSeller().getUserId() == getRequestingUserId(authentication);
    }

    // Only the owner of the product and the Administrator can delete the product
    public Boolean canUpdateProduct(long productId){
        LOGGER.info("Verifying Product Update Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication) || isSuperAdministrator(authentication))
            return true;

        Product p = ps.findProduct(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));

        return p.getSeller().getUserId() == getRequestingUserId(authentication);
    }

    // ------------------------------------------------ POSTS ----------------------------------------------------------

    // Only the creator of the post and the Administrator can delete the post
    public Boolean canDeletePost(long postId){
        LOGGER.info("Verifying Post Delete Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication) || isSuperAdministrator(authentication))
            return true;

        Post p = postService.findPost(postId).orElseThrow(()-> new NotFoundException("Post Not Found"));

        return p.getUser().getUserId() == getRequestingUserId(authentication);
    }

    // ---------------------------------------------- INQUIRIES --------------------------------------------------------

    // The author of the inquiry can delete it
    public Boolean canDeleteInquiry(long inquiryId){
        LOGGER.info("Verifying Inquiry Delete Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication) || isSuperAdministrator(authentication))
            return true;

        Inquiry i = is.findInquiry(inquiryId).orElseThrow(()-> new NotFoundException("Product Not Found"));

        return i.getUser().getUserId() == getRequestingUserId(authentication);
    }

    // The seller of the product cant create an Inquiry for it
    public Boolean canCreateInquiry(long productId){
        LOGGER.info("Verifying Inquiry Creation Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication) || isSuperAdministrator(authentication))
            return true;

        Product p = ps.findProduct(productId).orElseThrow(NotFoundException::new);

        return p.getSeller().getUserId() != getRequestingUserId(authentication);
    }

    // The seller of the product can answer Inquiries for that Product
    public Boolean canAnswerInquiry(long inquiryId){
        LOGGER.info("Verifying Inquiry Answering Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication) || isSuperAdministrator(authentication))
            return true;

        Inquiry i = is.findInquiry(inquiryId).orElseThrow(()-> new NotFoundException("Inquiry Not Found"));

        return i.getProduct().getSeller().getUserId() == getRequestingUserId(authentication);
    }

    // ---------------------------------------------- REQUESTS ---------------------------------------------------------

    // A Neighbor can access the Requests for their products and the Administrator can access all requests
    public Boolean canAccessRequests(String userURN, String productURN){
        LOGGER.info("Verifying Requests Accessibility");
        Authentication authentication = getAuthentication();

        // If both userId and productId are null, only administrators can access
        if (userURN== null && productURN == null) {
            return isAdministrator(authentication) || isSuperAdministrator(authentication);
        }

        // If userId is not null and productId is null, check if the user is the one making the request
        if (userURN != null && productURN == null) {
            return getRequestingUserId(authentication) == extractTwoURNIds(userURN).getSecondId();
        }

        // If productId is not null, check if the seller is the logged-in user
        Product product = ps.findProduct(extractTwoURNIds(productURN).getSecondId()).orElseThrow(() -> new NotFoundException("Product Not Found"));
        return product.getSeller().getUserId() == getRequestingUserId(authentication);
    }

    // The seller and the requester can access the request
    public Boolean canAccessRequest(long requestId){
        LOGGER.info("Verifying Request Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication) || isSuperAdministrator(authentication))
            return true;

        Request r = rs.findRequest(requestId).orElseThrow(()-> new NotFoundException("Request Not Found"));

        return r.getUser().getUserId() == getRequestingUserId(authentication)|| r.getProduct().getSeller().getUserId() == getRequestingUserId(authentication);
    }

    // Only the seller can update the request
    public Boolean canUpdateRequest(long requestId){
        LOGGER.info("Verifying Update Request Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication) || isSuperAdministrator(authentication))
            return true;

        Request r = rs.findRequest(requestId).orElseThrow(()-> new NotFoundException("Request Not Found"));

        return r.getProduct().getSeller().getUserId() == getRequestingUserId(authentication);
    }

    // The Requester can delete his Request
    public Boolean canDeleteRequest(long requestId){
        LOGGER.info("Verifying List Transactions Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication) || isSuperAdministrator(authentication))
            return true;

        Request r = rs.findRequest(requestId).orElseThrow(()-> new NotFoundException("Product Not Found"));

        return r.getUser().getUserId() == getRequestingUserId(authentication);
    }

    // The seller cant create a Request for his own Products
    public Boolean canCreateRequest(String productURN){
        LOGGER.info("Verifying Request Creation Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication) || isSuperAdministrator(authentication))
            return true;

        TwoIds twoIds = extractTwoURNIds(productURN);
        long neighborhoodId = twoIds.getFirstId();
        long productId = twoIds.getSecondId();

        Product p = ps.findProduct(productId, neighborhoodId).orElseThrow(()-> new NotFoundException("Product Not Found"));

        return p.getSeller().getUserId() != getRequestingUserId(authentication);
    }


    // -------------------------------------------------------------------------------

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

    private Boolean isAdministrator(Authentication authentication){
        return getRequestingUser(authentication).getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(Authority.ROLE_ADMINISTRATOR.name()));
    }

    private Boolean isSuperAdministrator(Authentication authentication){
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

    private long extractURNId(String URN) {
        String[] URNParts = URN.split("/");
        if (URNParts.length < 5) { // Check if there are enough parts for an ID
            throw new IllegalArgumentException("Invalid URN format.");
        }

        return Long.parseLong(URNParts[4]);
    }

    public static TwoIds extractTwoURNIds(String URN) {
        String[] URNParts = URN.split("/");
        if (URNParts.length < 7) { // Check if there are enough parts for two IDs
            throw new IllegalArgumentException("Invalid URN format.");
        }

        long firstId = Long.parseLong(URNParts[4]);
        long secondId = Long.parseLong(URNParts[6]);

        return new TwoIds(firstId, secondId);
    }
}

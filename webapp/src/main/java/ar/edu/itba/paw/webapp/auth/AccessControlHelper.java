package ar.edu.itba.paw.webapp.auth;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

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


    // Workers, Neighbors and Administrators can access /neighborhoods using Query Params
    public boolean hasAccessNeighborhoodQP(Long workerId) {
        LOGGER.info("Verifying Query Params Accessibility");

        Authentication authentication = getAuthentication();

        if (!isAnonymous(authentication)) {
            if (workerId != null) {
                return isUnverifiedOrRejected(authentication);
            }
            return true;
        }

        return workerId == null;
    }

    // Neighbors and Administrators can access the user details of others
    public boolean hasAccessToUserDetail(long neighborhoodId, long userId) {
        LOGGER.info("Verifying Detail User Accessibility");

        Authentication authentication = getAuthentication();

        if (isAnonymous(authentication))
            return false;

        if (isUnverifiedOrRejected(authentication))
            return getRequestingUserId(authentication) == userId;


        return neighborhoodId == 0 || neighborhoodId == getRequestingUser(authentication).getNeighborhoodId();
    }

    // Neighbors and Administrators can access the whole user list and the workers user list
    public boolean hasAccessToUserList(long neighborhoodId) {
        LOGGER.info("Verifying User List Accessibility");

        Authentication authentication = getAuthentication();

        if (isAnonymous(authentication))
            return false;

        if (isUnverifiedOrRejected(authentication))
            return false;

        return neighborhoodId == 0 || neighborhoodId == getRequestingUser(authentication).getNeighborhoodId();
    }


    // A user can update his own profile but only an Administrator can update other people's profile
    public Boolean canUpdateUser(long userId, long neighborhoodId) {
        LOGGER.info("Verifying Update User Accessibility");
        Authentication authentication = getAuthentication();

        if (getRequestingUser(authentication).getNeighborhoodId() != neighborhoodId)
            return false;

        if (isAdministrator(authentication))
            return true;

        return getRequestingUserId(authentication) == userId;
    }

    // A user can create/delete an attendance for themselves, or an Administrator for anyone
    public Boolean canModifyAttendance(String userURN) {
        LOGGER.info("Verifying Create Attendance Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication))
            return true;

        return getRequestingUserId(authentication) == extractURNId(userURN);
    }

    // Neighbors can access their own Transactions and Administrators can access all their neighbors' Transactions
    public Boolean canAccessTransactions(long userId) {
        LOGGER.info("Verifying List Transactions Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication))
            return true;

        return getRequestingUserId(authentication) == userId;
    }

    // Only the owner of the product and the Administrator can delete the product
    public Boolean canDeleteProduct(long productId){
        LOGGER.info("Verifying List Transactions Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication))
            return true;

        Product p = ps.findProduct(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));

        return p.getSeller().getUserId() == getRequestingUserId(authentication);
    }

    // Only the creator of the post and the Administrator can delete the post
    public Boolean canDeletePost(long postId){
        LOGGER.info("Verifying List Transactions Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication))
            return true;

        Post p = postService.findPost(postId).orElseThrow(()-> new NotFoundException("Post Not Found"));

        return p.getUser().getUserId() == getRequestingUserId(authentication);
    }

    public Boolean canDeleteInquiry(long inquiryId){
        LOGGER.info("Verifying List Transactions Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication))
            return true;

        Inquiry i = is.findInquiry(inquiryId).orElseThrow(()-> new NotFoundException("Product Not Found"));

        return i.getUser().getUserId() == getRequestingUserId(authentication);
    }

    public Boolean canDeleteRequest(long requestId){
        LOGGER.info("Verifying List Transactions Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication))
            return true;

        Request r = rs.findRequest(requestId).orElseThrow(()-> new NotFoundException("Product Not Found"));

        return r.getUser().getUserId() == getRequestingUserId(authentication);
    }

    // The seller of the product cant create an Inquiry for it
    public Boolean canCreateInquiry(long productId){
        LOGGER.info("Verifying Inquiry Creation Accessibility");
        Authentication authentication = getAuthentication();

        Product p = ps.findProduct(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));

        return p.getSeller().getUserId() != getRequestingUserId(authentication);
    }

    // The seller of the product can answer Inquiries for that Product
    public Boolean canAnswerInquiry(long inquiryId){
        LOGGER.info("Verifying Inquiry Answering Accessibility");
        Authentication authentication = getAuthentication();

        Inquiry i = is.findInquiry(inquiryId).orElseThrow(()-> new NotFoundException("Inquiry Not Found"));

        return i.getProduct().getSeller().getUserId() == getRequestingUserId(authentication);
    }

    // A Neighbor can access the Requests for their products and the Administrator can access all requests
    public Boolean canAccessRequests(Long userId, Long productId){
        LOGGER.info("Verifying Requests Accessibility");
        Authentication authentication = getAuthentication();

        // If both userId and productId are null, only administrators can access
        if (userId == null && productId == null) {
            if (isAdministrator(authentication)) {
                return true;
            } else {
                throw new IllegalArgumentException("requestedBy and productURN query params are missing.");
            }
        }

        // If userId is not null and productId is null, check if the user is the one making the request
        if (userId != null && productId == null) {
            return userId.equals(getRequestingUserId(authentication));
        }

        // If productId is not null, check if the seller is the logged-in user
        Product product = ps.findProduct(productId).orElseThrow(() -> new NotFoundException("Product Not Found"));
        return product.getSeller().getUserId().equals(getRequestingUserId(authentication));
    }

    // The seller of the product and the requester can access the request
    public Boolean canAccessRequest(long requestId){
        LOGGER.info("Verifying Request Accessibility");
        Authentication authentication = getAuthentication();

        if (isAdministrator(authentication))
            return true;

        Request r = rs.findRequest(requestId).orElseThrow(()-> new NotFoundException("Request Not Found"));

        long requestingUserId = getRequestingUserId(authentication);
        return r.getUser().getUserId() == requestingUserId || r.getProduct().getSeller().getUserId() == requestingUserId;
    }

    // The seller cant create a Request for his own Products
    public Boolean canCreateRequest(String productURN){
        LOGGER.info("Verifying Request Creation Accessibility");
        Authentication authentication = getAuthentication();

        TwoIds twoIds = extractTwoURNIds(productURN);
        long neighborhoodId = twoIds.getFirstId();
        long productId = twoIds.getSecondId();

        Product p = ps.findProduct(productId, neighborhoodId).orElseThrow(()-> new NotFoundException("Product Not Found"));

        return p.getSeller().getUserId() != getRequestingUserId(authentication);
    }

    private Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean isUnverifiedOrRejected(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_UNVERIFIED_NEIGHBOR") ||
                        authority.getAuthority().equals("ROLE_REJECTED"));
    }

    private boolean isAnonymous(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private Boolean isAdministrator(Authentication authentication){
        return getRequestingUser(authentication).getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMINISTRATOR"));
    }

    private Boolean isSuperAdministrator(Authentication authentication){
        return getRequestingUser(authentication).getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_SUPER_ADMINISTRATOR"));
    }

    private long getRequestingUserId(Authentication authentication){
        return getRequestingUser(authentication).getUserId();
    }

    private UserAuth getRequestingUser(Authentication authentication){
        return (UserAuth) authentication.getPrincipal();
    }

    //A worker's affiliation to a neighborhood can only be updated by an administrator from said neighborhood
    public Boolean canUpdateAffiliation(String neighborhoodURN) {
        LOGGER.info("Verifying Update Affiliation Accessibility");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        long neighborhoodId = extractURNId(neighborhoodURN);

        if (userAuth.getNeighborhoodId() == neighborhoodId && userAuth.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMINISTRATOR")))
            return true;

        return false;
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

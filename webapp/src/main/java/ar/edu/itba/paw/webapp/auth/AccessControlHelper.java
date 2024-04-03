package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.InquiryService;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.interfaces.services.RequestService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Inquiry;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.Entities.Request;
import ar.edu.itba.paw.models.Entities.User;
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
    private InquiryService is;

    @Autowired
    private RequestService rs;

    // All requests that correspond to a certain Neighborhood have to be done from a User from that same Neighborhood
    public boolean isNeighborhoodMember(HttpServletRequest request) {
        LOGGER.info("Neighborhood Belonging Bind");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        if (userAuth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_UNVERIFIED_NEIGHBOR") || authority.getAuthority().equals("ROLE_REJECTED"))) {
            return false;
        }

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            if (workerId != null) {
                return authentication.getAuthorities().stream()
                        .noneMatch(authority -> authority.getAuthority().equals("ROLE_UNVERIFIED_NEIGHBOR") || authority.getAuthority().equals("ROLE_REJECTED"));
            } else {
                return true;
            }
        }

        return workerId == null;
    }

    // Neighbors and Administrators can access the user details of others
    public boolean hasAccessToUserDetail(long neighborhoodId, long userId) {
        LOGGER.info("Verifying Detail User Accessibility");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            return false;

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        if (authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_UNVERIFIED_NEIGHBOR") || authority.getAuthority().equals("ROLE_REJECTED"))) {
            return ((UserAuth) authentication.getPrincipal()).getUserId() == userId;
        }

        return neighborhoodId == 0 || neighborhoodId == userAuth.getNeighborhoodId();
    }

    // Neighbors and Administrators can access the whole user list and the workers user list
    public boolean hasAccessToUserList(long neighborhoodId) {
        LOGGER.info("Verifying User List Accessibility");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            return false;

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        if (authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_UNVERIFIED_NEIGHBOR") || authority.getAuthority().equals("ROLE_REJECTED"))) {
            return false;
        }

        return neighborhoodId == 0 || neighborhoodId == userAuth.getNeighborhoodId();
    }


    // A user can update his own profile but only an Administrator can update other people's profile
    public Boolean canUpdateUser(long userId, long neighborhoodId) {
        LOGGER.info("Verifying Update User Accessibility");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        if (userAuth.getNeighborhoodId() != neighborhoodId)
            return false;

        if (userAuth.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMINISTRATOR")))
            return true;

        return userAuth.getUserId() == userId;
    }

    // Neighbors can access their own Transactions and Administrators can access all their neighbors' Transactions
    public Boolean canAccessTransactions(long userId) {
        LOGGER.info("Verifying List Transactions Accessibility");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        if (userAuth.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMINISTRATOR")))
            return true;

        return userAuth.getUserId() == userId;
    }

    // Only the owner of the product and the Administrator can delete the product
    public Boolean canDeleteProduct(long productId){
        LOGGER.info("Verifying List Transactions Accessibility");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        if (userAuth.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMINISTRATOR")))
            return true;

        Product p = ps.findProduct(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));

        return p.getSeller().getUserId() == userAuth.getUserId();
    }

    // The seller of the product cant create an Inquiry for it
    public Boolean canCreateInquiry(long productId){
        LOGGER.info("Verifying Inquiry Creation Accessibility");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        Product p = ps.findProduct(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));

        return p.getSeller().getUserId() != userAuth.getUserId();
    }

    // The seller of the product can answer Inquiries for that Product
    public Boolean canAnswerInquiry(long inquiryId){
        LOGGER.info("Verifying Inquiry Answering Accessibility");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        Inquiry i = is.findInquiry(inquiryId).orElseThrow(()-> new NotFoundException("Inquiry Not Found"));

        return i.getProduct().getSeller().getUserId() == userAuth.getUserId();
    }

    // A Neighbor can access the Requests for their products and the Administrator can access all requests
    public Boolean canAccessRequests(Long productId, Long userId){
        LOGGER.info("Verifying Requests Accessibility");

        // null verification is missing

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        if (userAuth.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMINISTRATOR")))
            return true;

        Product p = ps.findProduct(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));

        return p.getSeller().getUserId() == userAuth.getUserId() || userId == userAuth.getUserId();
    }

    // The seller of the product and the requester can access the request
    public Boolean canAccessRequest(long requestId){
        LOGGER.info("Verifying Request Accessibility");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        if (userAuth.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMINISTRATOR")))
            return true;

        Request r = rs.findRequest(requestId).orElseThrow(()-> new NotFoundException("Request Not Found"));

        return r.getUser().getUserId() == userAuth.getUserId() || r.getProduct().getSeller().getUserId() == userAuth.getUserId();
    }

    // The seller cant create a Request for his own Products
    public Boolean canCreateRequest(long productId){
        LOGGER.info("Verifying Request Creation Accessibility");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        Product p = ps.findProduct(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));

        return p.getSeller().getUserId() != userAuth.getUserId();
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
}

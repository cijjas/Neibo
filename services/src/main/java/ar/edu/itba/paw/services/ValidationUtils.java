package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.ShiftStatus;
import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.exceptions.InvalidEnumValueException;
import ar.edu.itba.paw.models.LinkEntry;
import ar.edu.itba.paw.models.ThreeIds;
import ar.edu.itba.paw.models.TwoIds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

public class ValidationUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityServiceImpl.class);

    public static void checkPageAndSize(int page, int size) {
        if (page <= 0) {
            LOGGER.info("Invalid page");
            throw new IllegalArgumentException("Invalid value (" + page + ") for the 'page' parameter. Please use a positive integer greater than 0.");
        }
        checkSize(size);
    }

    public static void checkFulfilled(Boolean fulfilled) {
        if (fulfilled == null) {
            LOGGER.info("Invalid Boolean");
            throw new IllegalArgumentException("Must specify request or purchase. Please use true/false for Query Param 'fulfilled'.");
        }
    }

    public static void checkQuantity(int quantity) {
        if (quantity <= 0) {
            LOGGER.info("Invalid Quantity");
            throw new IllegalArgumentException("Invalid value (" + quantity + ") for the 'quantity' parameter. Please use a positive integer greater than 0.");
        }
    }

    public static void checkId(Long id, String entity) {
        if (id != null && id <= 0) {
            LOGGER.info("Invalid {} ID", entity);
            throw new IllegalArgumentException("Invalid value (" + id + ") for the " + entity + " ID. Please use a positive integer greater than 0.");
        }
    }

    public static void checkIds(Long id1, Long id2, String entity) {
        if (id1 != null && id1 <= 0) {
            LOGGER.info("Invalid {} ID", entity);
            throw new IllegalArgumentException("Invalid identifier value(s) for " + entity + ". IDs must be positive integers greater than 0.");
        }
        if (id2 != null && id2 <= 0) {
            LOGGER.info("Invalid {} ID", entity);
            throw new IllegalArgumentException("Invalid identifier value(s) for " + entity + ". IDs must be positive integers greater than 0.");
        }
    }

    // ---------------------------------------------------------------------------------------------------------

    public static void checkSize(int size) {
        if (size <= 0) {
            LOGGER.info("Invalid Size");
            throw new IllegalArgumentException("Invalid value (" + size + ") for the 'size' parameter. Please use a positive integer greater than 0.");
        }
    }

    public static void checkAmenityId(Long amenityId) {
        ValidationUtils.checkId(amenityId, "Amenity");
    }

    public static void checkPostStatusId(Long postStatusId) {
        ValidationUtils.checkId(postStatusId, "Post Status");
    }

    public static void checkProductStatusId(Long productStatusId) {
        ValidationUtils.checkId(productStatusId, "Product Status");
    }

    public static void checkWorkerStatusId(Long workerStatusId) {
        ValidationUtils.checkId(workerStatusId, "Worker Status");
    }

    public static void checkWorkerRoleId(Long workerRoleId) {
        ValidationUtils.checkId(workerRoleId, "Worker Role");
    }

    public static void checkTransactionTypeId(Long transactionTypeId) {
        ValidationUtils.checkId(transactionTypeId, "Transaction Type");
    }

    public static void checkNeighborhoodId(Long neighborhoodId) {
        ValidationUtils.checkId(neighborhoodId, "Neighborhood");
    }

    public static void checkNeighborhoodIdInUsers(Long neighborhoodId) {
        if (neighborhoodId != null && neighborhoodId < -1) {
            LOGGER.info("Invalid {} ID", "Neighborhood");
            throw new IllegalArgumentException("Invalid value (" + neighborhoodId + ") for the " + "Neighborhood" + " ID. Please use a positive integer greater or equal to -1.");
        }
    }

    public static void checkUserId(Long userId) {
        ValidationUtils.checkId(userId, "User");
    }

    public static void checkPostId(Long postId) {
        ValidationUtils.checkId(postId, "Post");
    }

    public static void checkDepartmentId(Long departmentId) {
        ValidationUtils.checkId(departmentId, "Department");
    }

    public static void checkProductId(Long productId) {
        ValidationUtils.checkId(productId, "Product");
    }

    public static void checkProfessionId(Long professionId) {
        ValidationUtils.checkId(professionId, "Profession");
    }

    public static void checkWorkerId(Long workerId) {
        ValidationUtils.checkId(workerId, "Worker");
    }

    public static void checkPurchaseId(Long purchaseId) {
        ValidationUtils.checkId(purchaseId, "Purchase");
    }

    public static void checkRequestId(Long requestId) {
        ValidationUtils.checkId(requestId, "Request");
    }

    public static void checkResourceId(Long resourceId) {
        ValidationUtils.checkId(resourceId, "Resource");
    }

    public static void checkReviewId(Long reviewId) {
        ValidationUtils.checkId(reviewId, "Review");
    }

    public static void checkShiftIds(Long startTimeId, Long dayId) {
        ValidationUtils.checkIds(startTimeId, dayId, "Shift");
    }

    public static void checkAttendanceId(Long eventId, Long userId) {
        ValidationUtils.checkIds(eventId, userId, "Attendance");
    }

    public static void checkAttendanceId(Long attendanceId) {
        ValidationUtils.checkId(attendanceId, "User ID needed to determine the Attendance");
    }

    public static void checkEventId(Long eventId) {
        ValidationUtils.checkId(eventId, "Event");
    }

    public static void checkTagId(Long tagId) {
        ValidationUtils.checkId(tagId, "Tag");
    }

    public static void checkChannelId(Long channelId) {
        ValidationUtils.checkId(channelId, "Channel");
    }

    public static void checkLanguageId(Long languageId) {
        ValidationUtils.checkId(languageId, "Language");
    }

    public static void checkCommentId(Long commentId) {
        ValidationUtils.checkId(commentId, "Comment");
    }

    public static void checkContactId(Long contactId) {
        ValidationUtils.checkId(contactId, "Contact");
    }

    public static void checkAvailabilityId(Long availabilityId) {
        ValidationUtils.checkId(availabilityId, "Availability");
    }

    public static void checkBookingId(Long bookingId) {
        ValidationUtils.checkId(bookingId, "Booking");
    }

    public static void checkImageId(Long imageId) {
        ValidationUtils.checkId(imageId, "Image");
    }

    public static void checkInquiryId(Long inquiryId) {
        ValidationUtils.checkId(inquiryId, "Inquiry");
    }

    public static void checkShiftId(Long likeId) {
        ValidationUtils.checkId(likeId, "Shift");
    }

    public static void checkUserRoleId(Long userRoleId) {
        ValidationUtils.checkId(userRoleId, "User-Role");
    }

    public static void checkLikeIds(Long postId, Long userId) {
        ValidationUtils.checkIds(postId, userId, "Like");
    }

    // ---------------------------------------------------------------------------------------------------------

    public static void checkOptionalWorkerRoleString(String workerRole) {
        if (workerRole == null)
            return;
        try {
            WorkerRole.valueOf(workerRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            Set<LinkEntry> links = new HashSet<>();
            links.add(new LinkEntry("Valid Worker Roles", "worker-roles"));
            throw new InvalidEnumValueException("Invalid worker role: '" + workerRole + "'. ", links);
        }
    }

    public static void checkWorkerRoleString(String workerRole) {
        try {
            WorkerRole.valueOf(workerRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            Set<LinkEntry> links = new HashSet<>();
            links.add(new LinkEntry("Valid Worker Roles", "worker-roles"));
            throw new InvalidEnumValueException("Invalid worker role: '" + workerRole + "'. ", links);
        }
    }

    public static void checkOptionalShiftStatusString(String shiftStatus) {
        if (shiftStatus == null)
            return;
        try {
            ShiftStatus.valueOf(shiftStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            Set<LinkEntry> links = new HashSet<>();
            links.add(new LinkEntry("Valid Shift Statuses", "shift-statuses"));
            throw new InvalidEnumValueException("Invalid shift status: '" + shiftStatus + "'. ", links);
        }
    }

    public static void checkOptionalDateString(String date) {
        if (date == null)
            return;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateFormat.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid value (" + date + ") for the 'date' parameter. Please use a date in YYYY-(M)M-(D)D format.");
        }
    }

    // --------------------------------------------------------------------------------------------------------------

    public static Long checkURNAndExtractWorkerStatusId(String workerStatusURN){
        if (workerStatusURN == null)
            return null;
        if (!URNValidator.validateURN(workerStatusURN, "workerStatus")){
            throw new IllegalArgumentException("Malformed URN");
        }
        Long workerStatusId = ValidationUtils.extractURNId(workerStatusURN);
        ValidationUtils.checkWorkerStatusId(workerStatusId);
        return workerStatusId;
    }

    public static Long checkURNAndExtractWorkerRoleId(String workerRoleURN){
        if (workerRoleURN == null)
            return null;
        if (!URNValidator.validateURN(workerRoleURN, "workerRoles")){
            throw new IllegalArgumentException("Malformed URN");
        }
        Long workerRoleId = ValidationUtils.extractURNId(workerRoleURN);
        ValidationUtils.checkWorkerRoleId(workerRoleId);
        return workerRoleId;
    }

    public static Long checkURNAndExtractPostStatusId(String postStatusURN){
        if (postStatusURN == null)
            return null;
        if (!URNValidator.validateURN(postStatusURN, "postStatuses")){
            throw new IllegalArgumentException("Malformed URN");
        }
        Long postStatusId = ValidationUtils.extractURNId(postStatusURN);
        ValidationUtils.checkPostStatusId(postStatusId);
        return postStatusId;
    }

    public static Long checkURNAndExtractProfessionId(String professionURN){
        if (professionURN == null)
            return null;
        if (!URNValidator.validateURN(professionURN, "professions")){
            throw new IllegalArgumentException("Malformed URN");
        }
        Long professionId = ValidationUtils.extractURNId(professionURN);
        ValidationUtils.checkProfessionId(professionId);
        return professionId;
    }

    public static Long checkURNAndExtractUserRoleId(String userRoleURN){
        if (userRoleURN == null)
            return null;
        if (!URNValidator.validateURN(userRoleURN, "userRole")){
            throw new IllegalArgumentException("Malformed URN");
        }
        Long userRoleId = ValidationUtils.extractURNId(userRoleURN);
        ValidationUtils.checkUserRoleId(userRoleId);
        return userRoleId;
    }

    public static Long checkURNAndExtractUserDepartmentId(String departmentURN){
        if (departmentURN == null)
            return null;
        if (!URNValidator.validateURN(departmentURN, "departments")){
            throw new IllegalArgumentException("Malformed URN");
        }
        Long departmentId = ValidationUtils.extractURNId(departmentURN);
        ValidationUtils.checkDepartmentId(departmentId);
        return departmentId;
    }

    public static Long checkURNAndExtractUserProductStatusId(String productStatusURN){
        if (productStatusURN == null)
            return null;
        if (!URNValidator.validateURN(productStatusURN, "productStatuses")){
            throw new IllegalArgumentException("Malformed URN");
        }
        Long productStatusId = ValidationUtils.extractURNId(productStatusURN);
        ValidationUtils.checkProductStatusId(productStatusId);
        return productStatusId;
    }

    public static Long checkURNAndExtractWorkerId(String workerURN){
        if (workerURN == null)
            return null;
        if (!URNValidator.validateURN(workerURN, "workers")){
            throw new IllegalArgumentException("Malformed URN");
        }
        Long workerId = ValidationUtils.extractURNId(workerURN);
        ValidationUtils.checkWorkerId(workerId);
        return workerId;
    }

    public static Long checkURNAndExtractNeighborhoodId(String neighborhoodURN){
        if (neighborhoodURN == null)
            return null;
        if (!URNValidator.validateURN(neighborhoodURN, "neighborhood")){
            throw new IllegalArgumentException("Malformed URN");
        }
        Long neighborhoodId = ValidationUtils.extractURNId(neighborhoodURN);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        return neighborhoodId;
    }

    public static Long checkURNAndExtractUserId(String userURN){
        if (userURN == null)
            return null;
        if (!URNValidator.validateURN(userURN, "users")){
            throw new IllegalArgumentException("Malformed URN");
        }
        TwoIds userTwoIds = ValidationUtils.extractTwoURNIds(userURN);
        ValidationUtils.checkNeighborhoodId(userTwoIds.getFirstId());
        ValidationUtils.checkUserId(userTwoIds.getSecondId());
        return userTwoIds.getSecondId();
    }

    public static Long checkURNAndExtractAmenityId(String amenityURN){
        if (amenityURN == null)
            return null;
        if (!URNValidator.validateURN(amenityURN, "amenity")){
            throw new IllegalArgumentException("Malformed URN");
        }
        TwoIds amenityTwoIds = ValidationUtils.extractTwoURNIds(amenityURN);
        ValidationUtils.checkNeighborhoodId(amenityTwoIds.getFirstId());
        ValidationUtils.checkAmenityId(amenityTwoIds.getSecondId());
        return amenityTwoIds.getSecondId();
    }

    public static Long checkURNAndExtractPostId(String postURN){
        if (postURN == null)
            return null;
        if (!URNValidator.validateURN(postURN, "posts")){
            throw new IllegalArgumentException("Malformed URN");
        }
        TwoIds postTwoIds = ValidationUtils.extractTwoURNIds(postURN);
        ValidationUtils.checkNeighborhoodId(postTwoIds.getFirstId());
        ValidationUtils.checkPostId(postTwoIds.getSecondId());
        return postTwoIds.getSecondId();
    }

    public static Long checkURNAndExtractChannelId(String channelURN){
        if (channelURN == null)
            return null;
        if (!URNValidator.validateURN(channelURN, "channel")){
            throw new IllegalArgumentException("Malformed URN");
        }
        TwoIds channelTwoIds = ValidationUtils.extractTwoURNIds(channelURN);
        ValidationUtils.checkNeighborhoodId(channelTwoIds.getFirstId());
        ValidationUtils.checkChannelId(channelTwoIds.getSecondId());
        return channelTwoIds.getSecondId();
    }

    public static Long checkURNAndExtractTagId(String tagURN){
        if (tagURN == null)
            return null;
        if (!URNValidator.validateURN(tagURN, "tags")){
            throw new IllegalArgumentException("Malformed URN");
        }
        TwoIds tagTwoIds = ValidationUtils.extractTwoURNIds(tagURN);
        ValidationUtils.checkNeighborhoodId(tagTwoIds.getFirstId());
        ValidationUtils.checkTagId(tagTwoIds.getSecondId());
        return tagTwoIds.getSecondId();
    }

    // --------------------------------------------------------------------------------------------------------------

    public static long extractURNId(String URN) {
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

    public static ThreeIds extractThreeURNIds(String URN) {
        String[] URNParts = URN.split("/");
        if (URNParts.length < 9) { // Check if there are enough parts for three IDs
            throw new IllegalArgumentException("Invalid URN format.");
        }

        long firstId = Long.parseLong(URNParts[4]);
        long secondId = Long.parseLong(URNParts[6]);
        long thirdId = Long.parseLong(URNParts[8]);

        return new ThreeIds(firstId, secondId, thirdId);
    }
}

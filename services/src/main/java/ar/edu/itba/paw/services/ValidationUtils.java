package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.interfaces.exceptions.InvalidEnumValueException;
import ar.edu.itba.paw.models.LinkEntry;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ValidationUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityServiceImpl.class);

    public static void checkPageAndSize(int page, int size) {
        if (page <= 0) {
            LOGGER.info("Invalid page");
            throw new IllegalArgumentException("Invalid value (" + page + ") for the 'page' parameter. Please use a positive integer greater than 0.");
        }
        checkSize(size);
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

    public static void checkNeighborhoodId(Long neighborhoodId) {
        ValidationUtils.checkId(neighborhoodId, "Neighborhood");
    }

    public static void checkNeighborhoodsIds(long[] neighborhoods) {
        for (Long neighborhoodId : neighborhoods)
            ValidationUtils.checkId(neighborhoodId, "Neighborhood");
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

    public static void checkWorkerId(Long workerId) {
        ValidationUtils.checkId(workerId, "Worker");
    }

    public static void checkPurchaseId(Long purchaseId) {
        ValidationUtils.checkId(purchaseId, "Purchase");
    }

    public static void checkRequestId(Long productId, Long userId) {
        ValidationUtils.checkIds(userId, productId, "Request");
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
        ValidationUtils.checkId(attendanceId, "Attendance");
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

    public static void checkCommentId(Long commentId) {
        ValidationUtils.checkId(commentId, "Comment");
    }

    public static void checkContactId(Long contactId) {
        ValidationUtils.checkId(contactId, "Contact");
    }

    public static void checkBuyerId(Long buyerId) {
        ValidationUtils.checkId(buyerId, "Buyer");
    }

    public static void checkSellerId(Long sellerId) {
        ValidationUtils.checkId(sellerId, "Seller");
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

    public static void checkLikeId(Long likeId) {
        ValidationUtils.checkId(likeId, "Like");
    }

    public static void checkShiftId(Long likeId) {
        ValidationUtils.checkId(likeId, "Shift");
    }

    public static void checkLikeIds(Long postId, Long userId) {
        ValidationUtils.checkIds(postId, userId, "Like");
    }

    public static void checkWorkerAreaIds(Long workerId, Long neighborhoodId) {
        ValidationUtils.checkIds(workerId, neighborhoodId, "WorkerArea");
    }

    // ---------------------------------------------------------------------------------------------------------

    public static void checkTransactionTypeString(String transactionType){
        if(transactionType == null) {
            Set<LinkEntry> links = new HashSet<>();
            links.add(new LinkEntry("Valid Transaction Types", "transaction-types"));
            throw new InvalidEnumValueException("Transaction type is required. Please specify a valid transaction type.", links);
        }
        try {
            TransactionType.valueOf(transactionType.toUpperCase());
        } catch (IllegalArgumentException e) {
            Set<LinkEntry> links = new HashSet<>();
            links.add(new LinkEntry("Valid Transaction Types", "transaction-types"));
            throw new InvalidEnumValueException("Invalid transaction type: '" + transactionType + "'. ", links);
        }
    }

    public static void checkPostStatusString(String postStatus){
        try {
            PostStatus.valueOf(postStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            Set<LinkEntry> links = new HashSet<>();
            links.add(new LinkEntry("Valid Post Statuses", "post-statuses"));
            throw new InvalidEnumValueException("Invalid post status : '" + postStatus + "'. ", links);
        }
    }

    public static void checkDepartmentString(String department){
        if(department == null) {
            return;
        }
        try {
            Department.valueOf(department.toUpperCase());
        } catch (IllegalArgumentException e) {
            Set<LinkEntry> links = new HashSet<>();
            links.add(new LinkEntry("Valid Departments", "departments"));
            throw new InvalidEnumValueException("Invalid department : '" + department + "'. ", links);
        }
    }

    public static void checkProductStatusString(String productStatus){
        if(productStatus == null) {
            Set<LinkEntry> links = new HashSet<>();
            links.add(new LinkEntry("Valid Product Statuses", "product-statuses"));
            throw new InvalidEnumValueException("Product Status is required. Please specify a valid Product Status.", links);
        }
        try {
            ProductStatus.valueOf(productStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            Set<LinkEntry> links = new HashSet<>();
            links.add(new LinkEntry("Valid Product Statuses", "product-statuses"));
            throw new InvalidEnumValueException("Invalid product status: '" + productStatus + "'. ", links);
        }
    }

    public static void checkUserRoleString(String userRole){
        if (userRole == null)
            return;
        try {
            UserRole.valueOf(userRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            Set<LinkEntry> links = new HashSet<>();
            links.add(new LinkEntry("Valid User Roles", "user-roles"));
            throw new InvalidEnumValueException("Invalid user role: '" + userRole + "'. ",links);
        }
    }

    public static void checkProfessionString(String profession){
        try {
            Professions.valueOf(profession.toUpperCase());
        } catch (IllegalArgumentException e) {
            Set<LinkEntry> links = new HashSet<>();
            links.add(new LinkEntry("Valid Professions", "professions"));
            throw new InvalidEnumValueException("Invalid profession: '" + profession + "'. ", links);
        }
    }

    public static void checkProfessionsArray(List<String> professions){
        for ( String profession : professions ){
            checkProfessionString(profession);
        }
    }

    public static void checkWorkerRoleString(String workerRole){
        try {
            WorkerRole.valueOf(workerRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            Set<LinkEntry> links = new HashSet<>();
            links.add(new LinkEntry("Valid Worker Roles", "worker-roles"));
            throw new InvalidEnumValueException("Invalid worker role: '" + workerRole + "'. ", links);
        }
    }

    public static void checkWorkerStatusString(String workerStatus){
        try {
            WorkerStatus.valueOf(workerStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            Set<LinkEntry> links = new HashSet<>();
            links.add(new LinkEntry("Valid Worker Statuses", "worker-statuses"));
            throw new InvalidEnumValueException("Invalid worker status: '" + workerStatus + "'. ", links);
        }
    }

    public static void checkShiftStatusString(String shiftStatus){
        try {
            ShiftStatus.valueOf(shiftStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            Set<LinkEntry> links = new HashSet<>();
            links.add(new LinkEntry("Valid Shift Statuses", "shift-statuses"));
            throw new InvalidEnumValueException("Invalid shift status: '" + shiftStatus + "'. ", links);
        }
    }

    public static void checkDateString(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateFormat.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid value (" + date + ") for the 'date' parameter. Please use a date in YYYY-(M)M-(D)D format.");
        }
    }
}

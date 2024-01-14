package ar.edu.itba.paw.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityServiceImpl.class);

    public static void checkPageAndSize(int page, int size) {
        if (page <= 0) {
            LOGGER.info("Invalid page");
            throw new IllegalArgumentException("Invalid value (" + page + ") for the 'page' parameter. Please use a positive integer greater than 0.");
        }
        checkSize(size);
    }

    public static void checkId(long id, String entity) {
        if (id <= 0) {
            LOGGER.info("Invalid {} ID", entity);
            throw new IllegalArgumentException("Invalid value (" + id + ") for the " + entity + " id. Please use a positive integer greater than 0.");
        }
    }

    public static void checkIds(long id1, long id2, String entity) {
        if (id1 <= 0) {
            LOGGER.info("Invalid {} ID", entity);
            throw new IllegalArgumentException("Invalid identifier value(s) for " + entity + ". IDs must be positive integers greater than 0.");
        }
        if (id2 <= 0) {
            LOGGER.info("Invalid {} ID", entity);
            throw new IllegalArgumentException("Invalid identifier value(s) for " + entity + ". IDs must be positive integers greater than 0.");
        }
    }

    //only if both are <=0 throw exception
    public static void checkConditionalIds(long id1, long id2, String entity) {
        if (id1 <= 0 && id2 <= 0) {
            LOGGER.info("Invalid {} ID", entity);
            throw new IllegalArgumentException("Invalid identifier value(s) for " + entity + ". IDs must be positive integers greater than 0.");
        }
    }

    //Checks if an id is negative, since both can be 0 (get all case); Also checks that not both are > 0 (only one condition allowed)
    public static void checkNegativeIds(long id1, long id2, String entity) {
        if (id1 < 0 || id2 < 0) {
            LOGGER.info("Invalid {} ID", entity);
            throw new IllegalArgumentException("Invalid identifier value(s) for " + entity + ". IDs must be positive integers greater than 0.");
        }
        if(id1 > 0 && id2 > 0) {
            LOGGER.info("Invalid {} ID", entity);
            throw new IllegalArgumentException("Invalid identifier value(s) for " + entity + ". May only filter by one criteria.");
        }
    }

    public static void checkNegativeId(long id1, String entity) {
        if (id1 < 0) {
            LOGGER.info("Invalid {} ID", entity);
            throw new IllegalArgumentException("Invalid identifier value(s) for " + entity + ". ID must be positive integer greater than 0.");
        }
    }

    // ---------------------------------------------------------------------------------------------------------

    public static void checkSize(int size) {
        if (size <= 0) {
            LOGGER.info("Invalid Size");
            throw new IllegalArgumentException("Invalid value (" + size + ") for the 'size' parameter. Please use a positive integer greater than 0.");
        }
    }

    public static void checkAmenityId(long amenityId) {
        ValidationUtils.checkId(amenityId, "Amenity");
    }

    public static void checkNeighborhoodId(long neighborhoodId) {
        ValidationUtils.checkId(neighborhoodId, "Neighborhood");
    }

    public static void checkNeighborhoodsIds(long[] neighborhoods) {
        for (long neighborhoodId : neighborhoods)
            ValidationUtils.checkId(neighborhoodId, "Neighborhood");
    }

    public static void checkUserId(long userId) {
        ValidationUtils.checkId(userId, "User");
    }

    public static void checkPostId(long postId) {
        ValidationUtils.checkId(postId, "Post");
    }

    public static void checkDepartmentId(long departmentId) {
        ValidationUtils.checkId(departmentId, "Department");
    }

    public static void checkProductId(long productId) {
        ValidationUtils.checkId(productId, "Product");
    }

    public static void checkWorkerId(long workerId) {
        ValidationUtils.checkId(workerId, "Worker");
    }

    public static void checkPurchaseId(long purchaseId) {
        ValidationUtils.checkId(purchaseId, "Purchase");
    }

    public static void checkRequestId(long productId, long userId) {
        ValidationUtils.checkConditionalIds(userId, productId, "Request");
    }

    public static void checkRequestId(long requestId) {
        ValidationUtils.checkId(requestId, "Request");
    }

    public static void checkResourceId(long resourceId) {
        ValidationUtils.checkId(resourceId, "Resource");
    }

    public static void checkReviewId(long reviewId) {
        ValidationUtils.checkId(reviewId, "Review");
    }

    public static void checkShiftId(long startTimeId, long dayId) {
        ValidationUtils.checkIds(startTimeId, dayId, "Shift");
    }

    public static void checkAttendanceId(long eventId, long userId) {
        ValidationUtils.checkIds(eventId, userId, "Attendance");
    }

    public static void checkNegativeTagId(long postId) {
        ValidationUtils.checkNegativeId(postId, "Tag");
    }

    public static void checkAttendanceId(long attendanceId) {
        ValidationUtils.checkId(attendanceId, "Attendance");
    }

    public static void checkEventId(long eventId) {
        ValidationUtils.checkId(eventId, "Event");
    }

    public static void checkTagId(long tagId) {
        ValidationUtils.checkId(tagId, "Tag");
    }

    public static void checkChannelId(long channelId) {
        ValidationUtils.checkId(channelId, "Channel");
    }

    public static void checkCommentId(long commentId) {
        ValidationUtils.checkId(commentId, "Comment");
    }

    public static void checkContactId(long contactId) {
        ValidationUtils.checkId(contactId, "Contact");
    }

    public static void checkBuyerId(long buyerId) {
        ValidationUtils.checkId(buyerId, "Buyer");
    }

    public static void checkSellerId(long sellerId) {
        ValidationUtils.checkId(sellerId, "Seller");
    }

    public static void checkAvailabilityId(long availabilityId) {
        ValidationUtils.checkId(availabilityId, "Availability");
    }

    public static void checkBookingId(long bookingId) {
        ValidationUtils.checkId(bookingId, "Booking");
    }

    public static void checkImageId(long imageId) {
        ValidationUtils.checkId(imageId, "Image");
    }

    public static void checkInquiryId(long inquiryId) {
        ValidationUtils.checkId(inquiryId, "Inquiry");
    }

    public static void checkLikeId(long likeId) {
        ValidationUtils.checkId(likeId, "Like");
    }

    public static void checkLikeIds(long postId, long userId) {
        ValidationUtils.checkIds(postId, userId, "Like");
    }

    public static void checkNegativeLikeIds(long postId, long userId) {
        ValidationUtils.checkNegativeIds(postId, userId, "Like");
    }

    public static void checkWorkerAreaIds(long workerId, long neighborhoodId) {
        ValidationUtils.checkIds(workerId, neighborhoodId, "WorkerArea");
    }
}

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

    //only if both are <=0 throw exception
    public static void checkConditionalIds(Long id1, Long id2, String entity) {
        if (id1 != null && id2 != null && id1 <= 0 && id2 <= 0) {
            LOGGER.info("Invalid {} ID", entity);
            throw new IllegalArgumentException("Invalid identifier value(s) for " + entity + ". IDs must be positive integers greater than 0.");
        }
    }

    //Checks if an id is negative, since both can be 0 (get all case); Also checks that not both are > 0 (only one condition allowed)
    public static void checkNegativeIds(Long id1, Long id2, String entity) {
        if (id1 < 0 || id2 < 0) {
            LOGGER.info("Invalid {} ID", entity);
            throw new IllegalArgumentException("Invalid identifier value(s) for " + entity + ". IDs must be positive integers greater than 0.");
        }
        if(id1 > 0 && id2 > 0) {
            LOGGER.info("Invalid {} ID", entity);
            throw new IllegalArgumentException("Invalid identifier value(s) for " + entity + ". May only filter by one criteria.");
        }
    }

    public static void checkNegativeId(Long id1, String entity) {
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
        ValidationUtils.checkConditionalIds(userId, productId, "Request");
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

    public static void checkShiftId(Long startTimeId, Long dayId) {
        ValidationUtils.checkIds(startTimeId, dayId, "Shift");
    }

    public static void checkAttendanceId(Long eventId, Long userId) {
        ValidationUtils.checkIds(eventId, userId, "Attendance");
    }

    public static void checkNegativeTagId(Long postId) {
        ValidationUtils.checkNegativeId(postId, "Tag");
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

    public static void checkNegativeLikeIds(Long postId, Long userId) {
        ValidationUtils.checkNegativeIds(postId, userId, "Like");
    }

    public static void checkWorkerAreaIds(Long workerId, Long neighborhoodId) {
        ValidationUtils.checkIds(workerId, neighborhoodId, "WorkerArea");
    }
}

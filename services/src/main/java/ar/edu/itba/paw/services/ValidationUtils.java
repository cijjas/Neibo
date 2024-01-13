package ar.edu.itba.paw.services;

public class ValidationUtils {

    public static void checkPageAndSize(int page, int size) {
        if (page <= 0)
            throw new IllegalArgumentException("Invalid value (" + page + ") for the 'page' parameter. Please use a positive integer greater than 0.");
        checkSize(size);
    }

    public static void checkId(long id, String entity) {
        if (id <= 0)
            throw new IllegalArgumentException("Invalid value (" + id + ") for the " + entity + " id. Please use a positive integer greater than 0.");
    }

    public static void checkIds(long id1, long id2, String entity) {
        if (id1 <= 0)
            throw new IllegalArgumentException("Invalid identifier value(s) for " + entity + ". IDs must be positive integers greater than 0.");
        if (id2 <= 0)
            throw new IllegalArgumentException("Invalid identifier value(s) for " + entity + ". IDs must be positive integers greater than 0.");
    }

    // ---------------------------------------------------------------------------------------------------------

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
        ValidationUtils.checkIds(userId, productId, "Request");
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

    public static void checkSize(int size) {
        if (size <= 0)
            throw new IllegalArgumentException("Invalid value (" + size + ") for the 'size' parameter. Please use a positive integer greater than 0.");
    }
}

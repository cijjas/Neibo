package ar.edu.itba.paw.enums;

public enum Endpoint {
    AFFILIATIONS("affiliations"),
    AMENITIES("amenities"),
    ATTENDANCE("attendance"),
    BOOKINGS("bookings"),
    CHANNELS("channels"),
    COMMENTS("comments"),
    CONTACTS("contacts"),
    COUNT("count"),
    DEPARTMENTS("departments"),
    EVENTS("events"),
    IMAGES("images"),
    INQUIRIES("inquiries"),
    LANGUAGES("languages"),
    LIKES("likes"),
    NEIGHBORHOODS("neighborhoods"),
    POSTS("posts"),
    POST_STATUSES("post-statuses"),
    PRODUCTS("products"),
    PRODUCT_STATUSES("product-statuses"),
    PROFESSIONS("professions"),
    REQUESTS("requests"),
    REQUEST_STATUSES("request-statuses"),
    RESOURCES("resources"),
    REVIEWS("reviews"),
    SHIFTS("shifts"),
    TAGS("tags"),
    TRANSACTION_TYPES("transaction-types"),
    USERS("users"),
    USER_ROLES("user-roles"),
    WORKERS("workers"),
    WORKER_ROLES("worker-roles"),
    WORKER_STATUSES("worker-statuses");

    private final String endpoint;

    Endpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    @Override
    public String toString() {
        return endpoint;
    }
}


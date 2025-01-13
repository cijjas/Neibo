package ar.edu.itba.paw.webapp.dto;

import java.net.URI;

public class RootLinks {
    // Self
    private URI self;

    // ------- Dynamic Endpoints
    // Neighborhoods
    private URI neighborhoods;
    private URI workersNeighborhood;
    // Affiliations
    private URI affiliations;
    // Images
    private URI images;
    // Neighborhood
    private URI neighborhood;
    // Workers
    private URI workers;
    // Departments
    private URI departments;
    // Professions
    private URI professions;
    // Shifts
    private URI shifts;

    // ------- Static Endpoints
    // Languages
    private URI languages;
    private URI englishLanguage;
    private URI spanishLanguage;
    // User Roles
    private URI administratorUserRole;
    private URI superAdministratorUserRole;
    private URI neighborUserRole;
    private URI unverifiedNeighborUserRole;
    private URI rejectedUserRole;
    private URI workerUserRole;
    // Worker Posts
    private URI workerPosts;
    // Worker Roles (Affiliation)
    private URI verifiedWorkerRole;
    private URI unverifiedWorkerRole;
    private URI rejectedWorkerRole;
    // Worker Statuses
    private URI hotWorkerStatus;
    private URI trendingWorkerStatus;
    // Post Statuses
    private URI postStatuses;
    private URI nonePostStatus;
    private URI hotPostStatus;
    private URI trendingPostStatus;
    // Product Status
    private URI boughtProductStatus;
    private URI soldProductStatus;
    private URI sellingProductStatus;
    // Request Statuses
    private URI acceptedRequestStatus;
    private URI declinedRequestStatus;
    private URI requestedRequestStatus;
    // Transaction Types
    private URI purchaseTransactionType;
    private URI saleTransactionType;

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getLanguages() {
        return languages;
    }

    public void setLanguages(URI languages) {
        this.languages = languages;
    }

    public URI getPostStatuses() {
        return postStatuses;
    }

    public void setPostStatuses(URI postStatuses) {
        this.postStatuses = postStatuses;
    }

    public URI getNeighborhoods() {
        return neighborhoods;
    }

    public void setNeighborhoods(URI neighborhoods) {
        this.neighborhoods = neighborhoods;
    }

    public URI getNonePostStatus() {
        return nonePostStatus;
    }

    public void setNonePostStatus(URI nonePostStatus) {
        this.nonePostStatus = nonePostStatus;
    }

    public URI getWorkerPosts() {
        return workerPosts;
    }

    public void setWorkerPosts(URI workerPosts) {
        this.workerPosts = workerPosts;
    }

    public URI getAffiliations() {
        return affiliations;
    }

    public void setAffiliations(URI affiliations) {
        this.affiliations = affiliations;
    }

    public URI getImages() {
        return images;
    }

    public void setImages(URI images) {
        this.images = images;
    }

    public URI getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(URI neighborhood) {
        this.neighborhood = neighborhood;
    }

    public URI getWorkers() {
        return workers;
    }

    public void setWorkers(URI workers) {
        this.workers = workers;
    }

    public URI getDepartments() {
        return departments;
    }

    public void setDepartments(URI departments) {
        this.departments = departments;
    }

    public URI getProfessions() {
        return professions;
    }

    public void setProfessions(URI professions) {
        this.professions = professions;
    }

    public URI getShifts() {
        return shifts;
    }

    public void setShifts(URI shifts) {
        this.shifts = shifts;
    }

    public URI getEnglishLanguage() {
        return englishLanguage;
    }

    public void setEnglishLanguage(URI englishLanguage) {
        this.englishLanguage = englishLanguage;
    }

    public URI getSpanishLanguage() {
        return spanishLanguage;
    }

    public void setSpanishLanguage(URI spanishLanguage) {
        this.spanishLanguage = spanishLanguage;
    }

    public URI getAdministratorUserRole() {
        return administratorUserRole;
    }

    public void setAdministratorUserRole(URI administratorUserRole) {
        this.administratorUserRole = administratorUserRole;
    }

    public URI getSuperAdministratorUserRole() {
        return superAdministratorUserRole;
    }

    public void setSuperAdministratorUserRole(URI superAdministratorUserRole) {
        this.superAdministratorUserRole = superAdministratorUserRole;
    }

    public URI getNeighborUserRole() {
        return neighborUserRole;
    }

    public void setNeighborUserRole(URI neighborUserRole) {
        this.neighborUserRole = neighborUserRole;
    }

    public URI getUnverifiedNeighborUserRole() {
        return unverifiedNeighborUserRole;
    }

    public void setUnverifiedNeighborUserRole(URI unverifiedNeighborUserRole) {
        this.unverifiedNeighborUserRole = unverifiedNeighborUserRole;
    }

    public URI getRejectedUserRole() {
        return rejectedUserRole;
    }

    public void setRejectedUserRole(URI rejectedUserRole) {
        this.rejectedUserRole = rejectedUserRole;
    }

    public URI getWorkerUserRole() {
        return workerUserRole;
    }

    public void setWorkerUserRole(URI workerUserRole) {
        this.workerUserRole = workerUserRole;
    }

    public URI getVerifiedWorkerRole() {
        return verifiedWorkerRole;
    }

    public void setVerifiedWorkerRole(URI verifiedWorkerRole) {
        this.verifiedWorkerRole = verifiedWorkerRole;
    }

    public URI getUnverifiedWorkerRole() {
        return unverifiedWorkerRole;
    }

    public void setUnverifiedWorkerRole(URI unverifiedWorkerRole) {
        this.unverifiedWorkerRole = unverifiedWorkerRole;
    }

    public URI getRejectedWorkerRole() {
        return rejectedWorkerRole;
    }

    public void setRejectedWorkerRole(URI rejectedWorkerRole) {
        this.rejectedWorkerRole = rejectedWorkerRole;
    }

    public URI getHotWorkerStatus() {
        return hotWorkerStatus;
    }

    public void setHotWorkerStatus(URI hotWorkerStatus) {
        this.hotWorkerStatus = hotWorkerStatus;
    }

    public URI getTrendingWorkerStatus() {
        return trendingWorkerStatus;
    }

    public void setTrendingWorkerStatus(URI trendingWorkerStatus) {
        this.trendingWorkerStatus = trendingWorkerStatus;
    }

    public URI getHotPostStatus() {
        return hotPostStatus;
    }

    public void setHotPostStatus(URI hotPostStatus) {
        this.hotPostStatus = hotPostStatus;
    }

    public URI getTrendingPostStatus() {
        return trendingPostStatus;
    }

    public void setTrendingPostStatus(URI trendingPostStatus) {
        this.trendingPostStatus = trendingPostStatus;
    }

    public URI getBoughtProductStatus() {
        return boughtProductStatus;
    }

    public void setBoughtProductStatus(URI boughtProductStatus) {
        this.boughtProductStatus = boughtProductStatus;
    }

    public URI getSoldProductStatus() {
        return soldProductStatus;
    }

    public void setSoldProductStatus(URI soldProductStatus) {
        this.soldProductStatus = soldProductStatus;
    }

    public URI getSellingProductStatus() {
        return sellingProductStatus;
    }

    public void setSellingProductStatus(URI sellingProductStatus) {
        this.sellingProductStatus = sellingProductStatus;
    }

    public URI getAcceptedRequestStatus() {
        return acceptedRequestStatus;
    }

    public void setAcceptedRequestStatus(URI acceptedRequestStatus) {
        this.acceptedRequestStatus = acceptedRequestStatus;
    }

    public URI getDeclinedRequestStatus() {
        return declinedRequestStatus;
    }

    public void setDeclinedRequestStatus(URI declinedRequestStatus) {
        this.declinedRequestStatus = declinedRequestStatus;
    }

    public URI getRequestedRequestStatus() {
        return requestedRequestStatus;
    }

    public void setRequestedRequestStatus(URI requestedRequestStatus) {
        this.requestedRequestStatus = requestedRequestStatus;
    }

    public URI getPurchaseTransactionType() {
        return purchaseTransactionType;
    }

    public void setPurchaseTransactionType(URI purchaseTransactionType) {
        this.purchaseTransactionType = purchaseTransactionType;
    }

    public URI getSaleTransactionType() {
        return saleTransactionType;
    }

    public void setSaleTransactionType(URI saleTransactionType) {
        this.saleTransactionType = saleTransactionType;
    }

    public URI getWorkersNeighborhood() {
        return workersNeighborhood;
    }

    public void setWorkersNeighborhood(URI workersNeighborhood) {
        this.workersNeighborhood = workersNeighborhood;
    }
}

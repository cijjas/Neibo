package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Entities.*;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.ExtractionUtils;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.*;

@Component
public class AccessControlHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessControlHelper.class);

    private final ProductService prs;
    private final PostService ps;
    private final InquiryService is;
    private final RequestService rs;
    private final CommentService cs;
    private final BookingService bs;
    private final ReviewService res;
    private final UserService us;
    private final WorkerService ws;
    private final NeighborhoodService ns;
    private final ShiftService ss;
    private final EventService es;
    private final AmenityService as;
    private final AvailabilityService avs;
    private final ChannelService chs;
    private final TagService ts;
    private final DepartmentService ds;
    private final ImageService ims;
    private final ProfessionService pros;
    private final AffiliationService afs;
    private final AuthHelper authHelper;

    @Autowired
    public AccessControlHelper(ProductService prs, PostService ps, InquiryService is, RequestService rs, CommentService cs, BookingService bs,
                               ReviewService res, UserService us, NeighborhoodService ns, WorkerService ws, ShiftService ss, EventService es,
                               AmenityService as, AvailabilityService avs, ChannelService chs, TagService ts, DepartmentService ds, ImageService ims,
                               ProfessionService pros, AffiliationService afs) {
        this.prs = prs;
        this.ps = ps;
        this.is = is;
        this.rs = rs;
        this.cs = cs;
        this.bs = bs;
        this.res = res;
        this.us = us;
        this.ns = ns;
        this.ws = ws;
        this.ss = ss;
        this.es = es;
        this.as = as;
        this.avs = avs;
        this.chs = chs;
        this.ts = ts;
        this.ds = ds;
        this.ims = ims;
        this.pros = pros;
        this.authHelper = new AuthHelper();
        this.afs = afs;
    }

    // ---------------------------------------------- AFFILIATIONS -----------------------------------------------------

    public boolean canListAffiliations(String neighborhoodURI, String workerURI) {
        LOGGER.info("Verifying List Affiliations Accessibility");

        // Structure Validation
        if (!URIValidator.validateOptionalURI(neighborhoodURI, Endpoint.NEIGHBORHOODS) || !URIValidator.validateOptionalURI(workerURI, Endpoint.WORKERS))
            return true; // Constraints handle the error

        // Entity Existence
        if ((neighborhoodURI != null && !ns.findNeighborhood(extractFirstId(neighborhoodURI)).isPresent()) || (workerURI != null  && !ws.findWorker(extractFirstId(workerURI)).isPresent()))
            return false;

        // Reference Authorization
        if (authHelper.isAnonymous())
            return false;
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator() || authHelper.isNeighbor())
            return neighborhoodURI != null && extractFirstId(neighborhoodURI) == authHelper.getRequestingUserNeighborhoodId();
        return workerURI != null && authHelper.getRequestingUserId() == extractFirstId(workerURI); // Is a worker
    }

    public boolean canCreateAffiliation(String neighborhoodURI, String workerURI, String workerRoleURI) {
        LOGGER.info("Verifying Create Affiliation Accessibility");

        // Structure Validation
        if (!URIValidator.validateURI(neighborhoodURI, Endpoint.NEIGHBORHOODS) || !URIValidator.validateURI(workerURI, Endpoint.WORKERS) || !URIValidator.validateURI(workerRoleURI, Endpoint.WORKER_ROLES))
            return true; // Constraints handle the error

        // Entity Existence
        if (!ns.findNeighborhood(extractFirstId(neighborhoodURI)).isPresent() || !ws.findWorker(extractFirstId(workerURI)).isPresent() || !WorkerRole.fromId(extractFirstId(workerRoleURI)).isPresent())
            return false;

        // Reference Authorization
        if (authHelper.isAnonymous())
            return false;
        if (authHelper.isSuperAdministrator())
            return true;
        return authHelper.getRequestingUserId() == extractFirstId(workerURI) && extractFirstId(workerRoleURI) == WorkerRole.UNVERIFIED_WORKER.getId(); // Is a worker
    }

    public boolean canUpdateAffiliation(String neighborhoodURI, String workerURI, String workerRoleURI) {
        LOGGER.info("Verifying Update Affiliation Accessibility");

        // Structure Validation
        if (!URIValidator.validateURI(neighborhoodURI, Endpoint.NEIGHBORHOODS) || !URIValidator.validateURI(workerURI, Endpoint.WORKERS) || !URIValidator.validateURI(workerRoleURI, Endpoint.WORKER_ROLES))
            return true; // Constraints handle the error

        // Entity Existence
        if (workerRoleURI != null && !WorkerRole.fromId(extractFirstId(workerRoleURI)).isPresent())
            return false;

        // Reference Authorization
        if (authHelper.isAnonymous())
            return false;
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return authHelper.getRequestingUserNeighborhoodId() == extractFirstId(neighborhoodURI);

        return false;
    }

    public boolean canDeleteAffiliation(String neighborhoodURI, String workerURI) {
        LOGGER.info("Verifying Delete Affiliation Accessibility");

        // Structure Validation
        if (!URIValidator.validateURI(neighborhoodURI, Endpoint.NEIGHBORHOODS) || !URIValidator.validateURI(workerURI, Endpoint.WORKERS))
            return true; // Constraints handle the error

        // Entity Existence
        if (!ns.findNeighborhood(extractFirstId(neighborhoodURI)).isPresent() || !ws.findWorker(extractFirstId(workerURI)).isPresent())
            return false;

        // Reference Authorization
        if (authHelper.isAnonymous())
            return false;
        if (authHelper.isSuperAdministrator())
            return true;
        return authHelper.getRequestingUserId() == extractFirstId(workerURI) && ns.findNeighborhood(extractFirstId(neighborhoodURI)).isPresent();
    }

    // --------------------------------------------- NEIGHBORHOODS -----------------------------------------------------

    // This method isolates each Neighborhood from each other, prohibiting cross neighborhood operations
    // It also grants the Neighborhood Administrator the ability to perform all actions within its Neighborhood
    // The restriction is applied to all* entities nested in '/neighborhood'
    // * Path '/neighborhoods/*/users/*' does not enforce this method due to more complicated authentication structure
    public boolean isNeighborhoodMember(HttpServletRequest request) {
        LOGGER.info("Neighborhood Belonging Bind");

        if (authHelper.isSuperAdministrator())
            return true;

        String requestURI = request.getRequestURI();
        try {
            if (ExtractionUtils.isNeighborhoodPath(requestURI)) {
                long neighborhoodId = ExtractionUtils.extractNeighborhoodId(requestURI);
                return authHelper.getRequestingUserNeighborhoodId() == neighborhoodId ||
                        neighborhoodId == BaseNeighborhood.WORKERS.getId();
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Failed to validate neighborhood membership", e);
            return false;
        }
        return false;
    }

    public boolean canListNeighborhoods(String withWorkerURI, String withoutWorkerURI) {
        LOGGER.info("Verifying List Neighborhoods Accessibility");

        // Structure Validation
        if (!URIValidator.validateOptionalURI(withWorkerURI, Endpoint.WORKERS) || !URIValidator.validateOptionalURI(withoutWorkerURI, Endpoint.WORKERS))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<Worker> optionalWithWorker = Optional.empty();
        if (withWorkerURI != null){
            optionalWithWorker = ws.findWorker(extractFirstId(withWorkerURI));
            if (!optionalWithWorker.isPresent())
                return false;
        }
        Optional<Worker> optionalWithoutWorker = Optional.empty();
        if (withoutWorkerURI != null){
            optionalWithoutWorker = ws.findWorker(extractFirstId(withoutWorkerURI));
            if (!optionalWithoutWorker.isPresent())
                return false;
        }

        // Reference Authorization
        return (!optionalWithWorker.isPresent() && !optionalWithoutWorker.isPresent()) || (authHelper.isAnonymous() && !authHelper.isUnverifiedOrRejected());
    }

    // --------------------------------------------- PROFESSION --------------------------------------------------------

    public boolean canUseWorkerQPInProfessions(String workerURI) {
        LOGGER.info("Verifying Query Params Accessibility");

        // Structure Validation
        if (!URIValidator.validateOptionalURI(workerURI, Endpoint.WORKERS))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<Worker> optionalWorker = Optional.empty();
        if (workerURI != null){
            optionalWorker = ws.findWorker(extractFirstId(workerURI));
            if (!optionalWorker.isPresent())
                return false;
        }

        // Reference Authorization
        return (!optionalWorker.isPresent() || (!authHelper.isAnonymous() && !authHelper.isUnverifiedOrRejected()));
    }

    // ----------------------------------------------- REVIEWS ---------------------------------------------------------

    public boolean canCreateReview(String userURI, long workerId) {
        LOGGER.info("Verifying Create Review Accessibility");

        // Structure Validation
        if (!URIValidator.validateURI(userURI, Endpoint.USERS))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> optionalUser = us.findUser(extractFirstId(userURI));
        if (!optionalUser.isPresent())
            return false;

        // Reference Authorization
        if (authHelper.isAnonymous() || authHelper.isUnverifiedOrRejected() || authHelper.isWorker())
            return false;
        if (authHelper.isSuperAdministrator())
            return true;
        Optional<Review> latestReview = res.findLatestReview(workerId, optionalUser.get().getUserId());
        // Neighbors & Admins
        return optionalUser.get().getUserId() == authHelper.getRequestingUserId()
            && latestReview.map(review -> ChronoUnit.HOURS.between(review.getDate().toInstant(), Instant.now()) >= 24).orElse(true); // Spam Prevention
    }

    public boolean canDeleteReview(long workerId, long reviewId) {
        LOGGER.info("Verifying Delete Review Accessibility");

        // Entity  Existence
        Optional<Review> optionalReview = res.findReview(workerId, reviewId);
        if (!optionalReview.isPresent())
            return true; // Not Found is handled by the controller

        // Reference Authorization
        if (authHelper.isAnonymous())
            return false;
        if (authHelper.isSuperAdministrator())
            return true;
        return optionalReview.get().getUser().getUserId() == authHelper.getRequestingUserId();
    }

    // -------------------------------------------------- USERS --------------------------------------------------------

    public boolean canListUsers(String neighborhoodURI, String userRoleURI) {
        LOGGER.info("Verifying User List Accessibility");

        // Structure Validation
        if (!URIValidator.validateOptionalURI(neighborhoodURI, Endpoint.NEIGHBORHOODS) || !URIValidator.validateOptionalURI(userRoleURI, Endpoint.USER_ROLES))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<Neighborhood> optionalNeighborhood = Optional.empty();
        if (neighborhoodURI != null){
            optionalNeighborhood = ns.findNeighborhood(extractFirstId(neighborhoodURI));
            if (!optionalNeighborhood.isPresent())
                return false;
        }
        Optional<UserRole> optionalUserRole;
        if (userRoleURI != null){
            optionalUserRole = UserRole.fromId(extractFirstId(userRoleURI));
            if (!optionalUserRole.isPresent())
                return false;
        }

        // Reference Authorization
        if (authHelper.isAnonymous())
            return false;
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isUnverifiedOrRejected())
            return false;
        // Workers, Neighbors & Administrators
        return optionalNeighborhood.isPresent()
                && (optionalNeighborhood.get().getNeighborhoodId() == BaseNeighborhood.WORKERS.getId()
                || optionalNeighborhood.get().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId());
    }

    public boolean canFindUser(long userId) {
        LOGGER.info("Verifying Detail User Accessibility");

        // Entity Existence
        Optional<User> optionalUser = us.findUser(userId);
        if (!optionalUser.isPresent())
            return true; // Not Found is handled by controller

        // Reference Authorization
        if (authHelper.isAnonymous())
            return false;
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isUnverifiedOrRejected())
            return authHelper.getRequestingUserId() == userId;
        // Workers, Neighbors & Admins
        return optionalUser.get().getNeighborhood().getNeighborhoodId() == BaseNeighborhood.WORKERS.getId()
                || optionalUser.get().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUser().getNeighborhoodId();
    }

    public boolean canCreateUser(String neighborhoodURI, String userRoleURI, String languageURI){
        LOGGER.info("Verifying Create User Accessibility");

        // Structure Validation
        if (!URIValidator.validateURI(neighborhoodURI, Endpoint.NEIGHBORHOODS) || !URIValidator.validateURI(userRoleURI, Endpoint.USER_ROLES) || !URIValidator.validateURI(languageURI, Endpoint.LANGUAGES))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<Neighborhood> optionalNeighborhood = ns.findNeighborhood(extractFirstId(neighborhoodURI));
        if (!optionalNeighborhood.isPresent())
            return false;
        Optional<UserRole> optionalUserRole = UserRole.fromId(extractFirstId(userRoleURI));
        if (!optionalUserRole.isPresent())
            return false;
        Optional<Language> optionalLanguage = Language.fromId(extractFirstId(languageURI));
        if (!optionalLanguage.isPresent())
            return false;

        // Reference Authorization
        if (authHelper.isAnonymous() || authHelper.isUnverifiedOrRejected() || authHelper.isWorker() || authHelper.isNeighbor() || authHelper.isAdministrator()) {
            return (!BaseNeighborhood.isABaseNeighborhood(optionalNeighborhood.get().getNeighborhoodId()) && optionalUserRole.get().getId() == UserRole.UNVERIFIED_NEIGHBOR.getId())
                    || (optionalNeighborhood.get().getNeighborhoodId() == BaseNeighborhood.WORKERS.getId() && optionalUserRole.get().getId() == UserRole.WORKER.getId());
        }
        return authHelper.isSuperAdministrator();
    }

    public boolean canUpdateUser(String neighborhoodURI, String userRoleURI, String languageURI, String profilePictureURI, long userId){
        LOGGER.info("Verifying Update User Accessibility");

        // Structure Validation
        if (!URIValidator.validateOptionalURI(neighborhoodURI, Endpoint.NEIGHBORHOODS) || !URIValidator.validateOptionalURI(userRoleURI, Endpoint.USER_ROLES) || !URIValidator.validateOptionalURI(languageURI, Endpoint.LANGUAGES) || !URIValidator.validateOptionalURI(profilePictureURI, Endpoint.IMAGES) )
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> user = us.findUser(userId);
        if (!user.isPresent())
            return true; // Not Found is handled by controller
        Optional<Neighborhood> neighborhood = Optional.empty();
        if (neighborhoodURI != null){
            neighborhood = ns.findNeighborhood(extractFirstId(neighborhoodURI));
            if (!neighborhood.isPresent())
                return false;
        }
        Optional<Image> image = Optional.empty();
        if (profilePictureURI != null){
            image = ims.findImage(extractFirstId(profilePictureURI));
            if (!image.isPresent())
                return false;
        }
        Optional<UserRole> userRole = Optional.empty();
        if (userRoleURI != null){
            userRole = UserRole.fromId(extractFirstId(userRoleURI));
            if (!userRole.isPresent())
                return false;
        }
        Optional<Language> language = Optional.empty();
        if (languageURI != null){
            language = Language.fromId(extractFirstId(languageURI));
            if (!language.isPresent())
                return false;
        }

        // Reference Authorization
        if (authHelper.isAnonymous())
            return false;
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator()) {
            return (
                        !neighborhood.isPresent() && !userRole.isPresent()
                            && user.get().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId()
                    )
                    ||
                    (
                        neighborhood.isPresent() && userRole.isPresent()
                        && user.get().getUserId() != authHelper.getRequestingUserId()
                        && user.get().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId()
                        && (((userRole.get() == UserRole.NEIGHBOR || userRole.get() == UserRole.UNVERIFIED_NEIGHBOR) && neighborhood.get().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId())
                        || (userRole.get() == UserRole.REJECTED && neighborhood.get().getNeighborhoodId() == BaseNeighborhood.REJECTED.getId()))
                    )
                    ||
                    (
                        !neighborhood.isPresent() && userRole.isPresent()
                        && user.get().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId()
                        && (userRole.get() == UserRole.NEIGHBOR || userRole.get() == UserRole.UNVERIFIED_NEIGHBOR)
                    )
                    ||
                    (
                        neighborhood.isPresent() && !userRole.isPresent()
                        && user.get().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId()
                        && neighborhood.get().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId()
                    );
        }
        if (authHelper.isNeighbor()) {
            return (
                        !neighborhood.isPresent() && !userRole.isPresent()
                        && user.get().getUserId() == authHelper.getRequestingUserId()
                    )
                    ||
                    (
                        neighborhood.isPresent() && userRole.isPresent()
                        && user.get().getUserId() == authHelper.getRequestingUserId()
                        && userRole.get() == UserRole.UNVERIFIED_NEIGHBOR
                        && neighborhood.get().getNeighborhoodId() != authHelper.getRequestingUserNeighborhoodId()
                        && !BaseNeighborhood.isABaseNeighborhood(neighborhood.get().getNeighborhoodId())
                    )
                    ||
                    (
                        !neighborhood.isPresent() && userRole.isPresent()
                        && user.get().getUserId() == authHelper.getRequestingUserId()
                        && user.get().getRole().getId() == userRole.get().getId()
                    )
                    ||
                    (
                        neighborhood.isPresent() && !userRole.isPresent()
                        && user.get().getUserId() == authHelper.getRequestingUserId()
                        && user.get().getNeighborhood().getNeighborhoodId().longValue() == neighborhood.get().getNeighborhoodId().longValue()
                    );
        }
        if (authHelper.isWorker()) {
            return user.get().getUserId() == authHelper.getRequestingUserId()
                    && (!neighborhood.isPresent() || neighborhood.get().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId())
                    && (!userRole.isPresent() || userRole.get().getId() == user.get().getRole().getId());
        }
        if (authHelper.isUnverifiedOrRejected()) {
            return (
                        !neighborhood.isPresent() && !userRole.isPresent()
                            && user.get().getUserId() == authHelper.getRequestingUserId()
                    )
                    ||
                    (
                        neighborhood.isPresent() && userRole.isPresent()
                        && user.get().getUserId() == authHelper.getRequestingUserId()
                        && userRole.get() == UserRole.UNVERIFIED_NEIGHBOR
                        && !BaseNeighborhood.isABaseNeighborhood(neighborhood.get().getNeighborhoodId())
                    )
                    ||
                    (
                        !neighborhood.isPresent() && userRole.isPresent()
                        && user.get().getUserId() == authHelper.getRequestingUserId()
                        && user.get().getRole().getId() == userRole.get().getId()
                    )
                    ||
                    (
                        neighborhood.isPresent() && !userRole.isPresent()
                        && user.get().getUserId() == authHelper.getRequestingUserId()
                        && (
                                (user.get().getRole().getId() == UserRole.UNVERIFIED_NEIGHBOR.getId() && !BaseNeighborhood.isABaseNeighborhood(neighborhood.get().getNeighborhoodId()))
                                || (user.get().getRole().getId() == UserRole.REJECTED.getId() && neighborhood.get().getNeighborhoodId() == BaseNeighborhood.REJECTED.getId())
                        )
                    )
                    ;
        }

        return false;
    }

    // ----------------------------------------------- WORKERS ---------------------------------------------------------

    public boolean canListWorkers(List<String> neighborhoodURIs, List<String> professionURIs, String workerRoleURI, String workerStatusURI) {
        LOGGER.info("Verifying Worker List Accessibility");

        // Structure Validation
        if (!URIValidator.validateOptionalURI(workerRoleURI, Endpoint.WORKER_ROLES) || !URIValidator.validateOptionalURI(workerStatusURI, Endpoint.WORKER_STATUSES))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<WorkerRole> optionalWorkerRole = Optional.empty();
        if (workerRoleURI != null){
            optionalWorkerRole = WorkerRole.fromId(extractFirstId(workerRoleURI));
            if (!optionalWorkerRole.isPresent())
                return false;
        }
        Optional<WorkerStatus> optionalWorkerStatus = Optional.empty();
        if (workerStatusURI != null){
            optionalWorkerStatus = WorkerStatus.fromId(extractFirstId(workerStatusURI));
            if (!optionalWorkerStatus.isPresent())
                return false;
        }
        if (neighborhoodURIs != null && !neighborhoodURIs.isEmpty()) {
            for (String neighborhoodURI : neighborhoodURIs) {
                // Structure Validation
                if (!URIValidator.validateURI(neighborhoodURI, Endpoint.NEIGHBORHOODS))
                    return true; // Constraints handle the error
                // Entity Existence
                if (!ns.findNeighborhood(extractFirstId(neighborhoodURI)).isPresent())
                    return false;
            }
        }
        if (professionURIs != null && !professionURIs.isEmpty()) {
            for (String professionURI : professionURIs) {
                // Structure Validation
                if (!URIValidator.validateURI(professionURI, Endpoint.PROFESSIONS))
                    return true; // Constraints handle the error
                // Entity Existence
                if (!pros.findProfession(extractFirstId(professionURI)).isPresent())
                    return false;
            }
        }

        // Reference Authorization
        if (authHelper.isAnonymous())
            return false;
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isNeighbor() || authHelper.isAdministrator()){
            if (neighborhoodURIs == null || neighborhoodURIs.isEmpty())
                return true;
            if (neighborhoodURIs.size() == 1) {
                Optional<Neighborhood> neighborhoodOpt = ns.findNeighborhood(extractFirstId(neighborhoodURIs.get(0)));
                return neighborhoodOpt.map(neighborhood ->
                        neighborhood.getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId()
                ).orElse(false);
            }
        }
        if (authHelper.isWorker())
            return neighborhoodURIs == null || neighborhoodURIs.isEmpty();
        // Unverified, Rejected
        return false;
    }

    public boolean canFindWorker(long workerId) {
        LOGGER.info("Verifying Find Worker Accessibility");

        // Entity Existence
        Optional<Worker> optionalWorker = ws.findWorker(workerId);
        if (!optionalWorker.isPresent())
            return false;

        // Reference Authorization
        if (authHelper.isAnonymous())
            return false;
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isWorker())
            return true;
        if (authHelper.isNeighbor() || authHelper.isAdministrator()){
            return !afs.getAffiliations(authHelper.getRequestingUserNeighborhoodId(), workerId, 1, 1).isEmpty();
        }
        // Unverified, Rejected
        return false;
    }

    public boolean canCreateWorker(String userURI, List<String> professionURIs, String imageURI) {
        LOGGER.info("Verifying Worker Create Accessibility");

        // Structure Validation
        if (!URIValidator.validateURI(userURI, Endpoint.USERS) || !URIValidator.validateURI(imageURI, Endpoint.IMAGES))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> optionalUser = us.findUser(extractFirstId(userURI));
        if (!optionalUser.isPresent())
            return false;
        Optional<Image> optionalImage = ims.findImage(extractFirstId(imageURI));
        if (!optionalImage.isPresent())
            return false;
        if (professionURIs == null || professionURIs.isEmpty())
            return true; // Constraints handle the error
        for (String professionURI : professionURIs) {
            // Structure Validation
            if (!URIValidator.validateURI(professionURI, Endpoint.PROFESSIONS))
                return true; // Constraints handle the error
            // Entity Existence
            if (!pros.findProfession(extractFirstId(professionURI)).isPresent())
                return false;
        }

        // Reference Authentication
        Optional<Worker> optionalWorker = ws.findWorker(extractFirstId(userURI));
        return (optionalUser.get().getRole().getId() == UserRole.WORKER.getId() && !optionalWorker.isPresent());
    }

    public boolean canUpdateWorker(String userURI, List<String> professionURIs, String imageURI, long workerId) {
        LOGGER.info("Verifying Worker Update Accessibility");

        // Structure Validation
        if (!URIValidator.validateOptionalURI(userURI, Endpoint.USERS) || !URIValidator.validateOptionalURI(imageURI, Endpoint.IMAGES))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> optionalUser = Optional.empty();
        if (userURI != null){
            optionalUser = us.findUser(extractFirstId(userURI));
            if (!optionalUser.isPresent())
                return false;
        }
        Optional<Image> optionalImage= Optional.empty();
        if (imageURI != null){
            optionalImage = ims.findImage(extractFirstId(imageURI));
            if (!optionalImage.isPresent())
                return false;
        }
        if (professionURIs != null && !professionURIs.isEmpty()) {
            for (String professionURI : professionURIs) {
                // Structure Validation
                if (!URIValidator.validateURI(professionURI, Endpoint.PROFESSIONS))
                    return true; // Constraints handle the error
                // Entity Existence
                if (!pros.findProfession(extractFirstId(professionURI)).isPresent())
                    return false;
            }
        }

        // Reference Authorization
        if (authHelper.isAnonymous())
            return false;
        if (authHelper.isSuperAdministrator())
            return true;
        return (!optionalUser.isPresent() || optionalUser.get().getUserId() == authHelper.getRequestingUserId()) && workerId == authHelper.getRequestingUserId();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // --------------------------------------- NEIGHBORHOOD NESTED ENTITIES --------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    // ----------------------------------------------- AMENITIES -------------------------------------------------------

    public boolean canCreateOrUpdateAmenity(List<String> shiftURIs) {
        LOGGER.info("Verifying Create or Update Amenity Accessibility");

        if (shiftURIs == null || shiftURIs.isEmpty())
            return true; // Valid use case
        for (String shiftURI : shiftURIs) {
            // Structure Validation
            if (!URIValidator.validateURI(shiftURI, Endpoint.SHIFTS))
                return true; // Constraints handle the error
            // Entity Existence
            if (!ss.findShift(extractFirstId(shiftURI)).isPresent())
                return false;
        }
        return true;
    }

    // ---------------------------------------------- ATTENDANCES ------------------------------------------------------

    public boolean canListOrCountAttendance(String eventURI, String userURI) {
        LOGGER.info("Verifying List or Count Attendance Accessibility");

        // Structure
        if (!URIValidator.validateOptionalURI(eventURI, Endpoint.EVENTS) || !URIValidator.validateOptionalURI(userURI, Endpoint.USERS))
            return true; // Constraints handle the error

        // Entity Existence
        TwoId twoId;
        Optional<Event> optionalEvent = Optional.empty();
        if (eventURI != null){
            twoId = extractTwoId(eventURI);
            optionalEvent = es.findEvent(twoId.getFirstId(), twoId.getSecondId());
            if (!optionalEvent.isPresent())
                return false;
        }
        Optional<User> optionalUser = Optional.empty();
        if (userURI != null) {
            optionalUser = us.findUser(extractFirstId(userURI));
            if (!optionalUser.isPresent())
                return false;
        }

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        return (!optionalEvent.isPresent() || authHelper.getRequestingUserNeighborhoodId() == optionalEvent.get().getNeighborhood().getNeighborhoodId())
                && (!optionalUser.isPresent() || authHelper.getRequestingUserNeighborhoodId() == optionalUser.get().getNeighborhood().getNeighborhoodId());
    }

    public boolean canCreateOrDeleteAttendance(String eventURI, String userURI) {
        LOGGER.info("Verifying Create or Delete Attendance Accessibility");

        // Structure Validation
        if (!URIValidator.validateURI(eventURI, Endpoint.EVENTS) || !URIValidator.validateURI(userURI, Endpoint.USERS))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> userOptional = us.findUser(extractFirstId(userURI));
        if (!userOptional.isPresent())
            return false;
        TwoId twoId = extractTwoId(eventURI);
        Optional<Event> eventOptional = es.findEvent(twoId.getFirstId(), twoId.getSecondId());
        if (!eventOptional.isPresent())
            return false;

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return authHelper.getRequestingUserNeighborhoodId() == eventOptional.get().getNeighborhood().getNeighborhoodId() && authHelper.getRequestingUserNeighborhoodId() == userOptional.get().getNeighborhood().getNeighborhoodId();
        return authHelper.getRequestingUserNeighborhoodId() == eventOptional.get().getNeighborhood().getNeighborhoodId() && authHelper.getRequestingUserId() == userOptional.get().getUserId();
    }

    // ------------------------------------------------ BOOKINGS -------------------------------------------------------

    public boolean canListBookings(String userURI, String amenityURI) {
        LOGGER.info("Verifying List Bookings Accessibility");

        // Structure
        if (!URIValidator.validateOptionalURI(userURI, Endpoint.USERS) || !URIValidator.validateOptionalURI(amenityURI, Endpoint.AMENITIES))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> optionalUser = Optional.empty();
        if (userURI != null) {
            optionalUser = us.findUser(extractFirstId(userURI));
            if (!optionalUser.isPresent())
                return false;
        }
        TwoId twoId;
        Optional<Amenity> optionalAmenity = Optional.empty();
        if (amenityURI != null){
            twoId = extractTwoId(amenityURI);
            optionalAmenity = as.findAmenity(twoId.getFirstId(), twoId.getSecondId());
            if (!optionalAmenity.isPresent())
                return false;
        }

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        return (!optionalUser.isPresent() || authHelper.getRequestingUserNeighborhoodId() == optionalUser.get().getNeighborhood().getNeighborhoodId())
                && (!optionalAmenity.isPresent() || authHelper.getRequestingUserNeighborhoodId() == optionalAmenity.get().getNeighborhood().getNeighborhoodId());
    }

    public boolean canCreateBooking(String userURI, String amenityURI, String shiftURI) {
        LOGGER.info("Verifying Create Booking Accessibility");

        // Structure Validation
        if (!URIValidator.validateURI(userURI, Endpoint.USERS) || !URIValidator.validateURI(amenityURI, Endpoint.AMENITIES) || !URIValidator.validateURI(shiftURI, Endpoint.SHIFTS))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> optionalUser = us.findUser(extractFirstId(userURI));
        if (!optionalUser.isPresent())
            return false;
        TwoId twoId = extractTwoId(amenityURI);
        Optional<Amenity> optionalAmenity = as.findAmenity(twoId.getFirstId(), twoId.getSecondId());
        if (!optionalAmenity.isPresent())
            return false;
        Optional<Shift> optionalShift = ss.findShift(extractFirstId(shiftURI));
        if (!optionalShift.isPresent())
            return false;
        Optional<Availability> optionalAvailability = avs.findAvailability(extractFirstId(shiftURI), extractSecondId(amenityURI));
        if (!optionalAvailability.isPresent())
            return false;

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return authHelper.getRequestingUserNeighborhoodId() == optionalAmenity.get().getNeighborhood().getNeighborhoodId() && authHelper.getRequestingUserNeighborhoodId() == optionalUser.get().getNeighborhood().getNeighborhoodId();
        return authHelper.getRequestingUserNeighborhoodId() == optionalAmenity.get().getNeighborhood().getNeighborhoodId() && authHelper.getRequestingUserId() == optionalUser.get().getUserId();
    }

    public boolean canDeleteBooking(long bookingId, long neighborhoodId) {
        LOGGER.info("Verifying Delete Booking Accessibility");

        // Entity Existence
        Optional<Booking> optionalBooking = bs.findBooking(neighborhoodId, bookingId);
        if (!optionalBooking.isPresent())
            return true; // Not Found is handled by the controller

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return authHelper.getRequestingUserNeighborhoodId() == optionalBooking.get().getUser().getNeighborhood().getNeighborhoodId();
        return authHelper.getRequestingUserId() == optionalBooking.get().getUser().getUserId();
    }

    // ------------------------------------------------ COMMENTS -------------------------------------------------------

    public boolean canCreateComment(String userURI) {
        LOGGER.info("Verifying CreateComment Accessibility");

        // Structure Validation
        if (!URIValidator.validateURI(userURI, Endpoint.USERS))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> optionalUser = us.findUser(extractFirstId(userURI));
        if (!optionalUser.isPresent())
            return false;

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return authHelper.getRequestingUserNeighborhoodId() == optionalUser.get().getNeighborhood().getNeighborhoodId();

        return authHelper.getRequestingUserId() == optionalUser.get().getUserId();
    }

    public boolean canDeleteComment(long neighborhoodId, long postId, long commentId) {
        LOGGER.info("Verifying Delete Comment Accessibility");

        // Entity Existence
        Optional<Comment> optionalComment = cs.findComment(neighborhoodId, postId, commentId);
        if (!optionalComment.isPresent())
            return true; // Not Found is handled by the controller

        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return authHelper.getRequestingUserNeighborhoodId() == optionalComment.get().getUser().getNeighborhood().getNeighborhoodId();

        return authHelper.getRequestingUserId() == optionalComment.get().getUser().getUserId();
    }


    // ---------------------------------------------- INQUIRIES --------------------------------------------------------

    public boolean canCreateInquiry(String userURI, long productId) {
        LOGGER.info("Verifying Inquiry Creation Accessibility");

        // Structure Validation
        if (!URIValidator.validateURI(userURI, Endpoint.USERS))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> optionalUser = us.findUser(extractFirstId(userURI));
        if (!optionalUser.isPresent())
            return false;
        Optional<Product> optionalProduct = prs.findProduct(productId);
        if (!optionalProduct.isPresent())
            return true; // Not Found is handled by controller

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return authHelper.getRequestingUserNeighborhoodId() == optionalUser.get().getNeighborhood().getNeighborhoodId();

        return optionalUser.get().getUserId() == authHelper.getRequestingUserId() && optionalProduct.get().getSeller().getUserId() != authHelper.getRequestingUserId();
    }

    public boolean canUpdateInquiry(String userURI, long inquiryId) {
        LOGGER.info("Verifying Inquiry Update Accessibility");

        // Structure Validation
        if (!URIValidator.validateOptionalURI(userURI, Endpoint.USERS))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> optionalUser = Optional.empty();
        if (userURI != null) {
            optionalUser = us.findUser(extractFirstId(userURI));
            if (!optionalUser.isPresent())
                return false;
        }
        Optional<Inquiry> optionalInquiry = is.findInquiry(inquiryId);
        if (!optionalInquiry.isPresent())
            return true; // Not Found is handled by controller

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return !optionalUser.isPresent() || optionalUser.get().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId();
        return ((!optionalUser.isPresent())
                || (optionalUser.get().getUserId() == authHelper.getRequestingUserId()))
                && optionalInquiry.get().getProduct().getSeller().getUserId() == authHelper.getRequestingUserId();
    }

    public boolean canDeleteInquiry(long inquiryId) {
        LOGGER.info("Verifying Inquiry Delete Accessibility");

        // Entity Existence
        Optional<Inquiry> optionalInquiry = is.findInquiry(inquiryId);
        if (!optionalInquiry.isPresent())
            return true; // Not Found is handled by controller

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return optionalInquiry.get().getUser().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId();
        return optionalInquiry.get().getUser().getUserId() == authHelper.getRequestingUserId();
    }

    // ------------------------------------------------- LIKES ---------------------------------------------------------

    public boolean canListOrCountLikes(String userURI, String postURI) {
        LOGGER.info("Verifying List or Count Like Accessibility");

        // Structure Validation
        if (!URIValidator.validateOptionalURI(userURI, Endpoint.USERS) || !URIValidator.validateOptionalURI(postURI, Endpoint.POSTS))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> optionalUser = Optional.empty();
        if (userURI != null) {
            optionalUser = us.findUser(extractFirstId(userURI));
            if (!optionalUser.isPresent())
                return false;
        }
        Optional<Post> optionalPost = Optional.empty();
        TwoId twoId;
        if (postURI != null) {
            twoId = extractTwoId(postURI);
            optionalPost= ps.findPost(twoId.getFirstId(), twoId.getSecondId());
            if (!optionalPost.isPresent())
                return false;
        }

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        return (!optionalUser.isPresent() || optionalUser.get().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId())
            && (!optionalPost.isPresent() || optionalPost.get().getUser().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId());
    }

    public boolean canCreateOrDeleteLike(String userURI, String postURI) {
        LOGGER.info("Verifying Create or Delete Like Accessibility");

        // Structure Validation
        if (!URIValidator.validateURI(userURI, Endpoint.USERS) || !URIValidator.validateURI(postURI, Endpoint.POSTS))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> optionalUser = us.findUser(extractFirstId(userURI));
        if (!optionalUser.isPresent())
            return false;
        TwoId twoId = extractTwoId(postURI);
        Optional<Post> optionalPost = optionalPost= ps.findPost(twoId.getFirstId(), twoId.getSecondId());
        if (!optionalPost.isPresent())
            return false;

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return (optionalUser.get().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId())
                && (optionalPost.get().getUser().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId());
        return (optionalUser.get().getUserId() == authHelper.getRequestingUserId())
                && (optionalPost.get().getUser().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId());
    }

    // ------------------------------------------------ POSTS ----------------------------------------------------------

    public boolean canListOrCountPosts(String userURI, String channelURI, List<String> tagURIs, String postStatusURI) {
        LOGGER.info("Verifying List Posts Accessibility");

        // Structure Validation
        if (!URIValidator.validateOptionalURI(userURI, Endpoint.USERS) || !URIValidator.validateOptionalURI(channelURI, Endpoint.CHANNELS) || !URIValidator.validateOptionalURI(postStatusURI, Endpoint.POST_STATUSES))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> optionalUser = Optional.empty();
        if (userURI != null) {
            optionalUser = us.findUser(extractFirstId(userURI));
            if (!optionalUser.isPresent())
                return false;
        }
        Optional<Channel> optionalChannel;
        TwoId channelTwoId = null;
        if (channelURI != null) {
            channelTwoId = extractTwoId(channelURI);
            optionalChannel = chs.findChannel(channelTwoId.getFirstId(), channelTwoId.getSecondId());
            if (!optionalChannel.isPresent())
                return false;
        }
        Optional<PostStatus> optionalPostStatus = Optional.empty();
        if (postStatusURI != null) {
            optionalPostStatus = PostStatus.fromId(extractFirstId(postStatusURI));
            if (!optionalPostStatus.isPresent())
                return false;
        }
        if (tagURIs != null && !tagURIs.isEmpty()) {
            for (String tagURI : tagURIs) {
                // Structure Validation
                if (!URIValidator.validateURI(tagURI, Endpoint.TAGS))
                    return true; // Constraints handle the error
                // Entity Existence
                TwoId twoId = extractTwoId(tagURI);
                if (!ts.findTag(twoId.getFirstId(), twoId.getSecondId()).isPresent())
                    return false;
                // Reference Authorization
                if (twoId.getFirstId() != authHelper.getRequestingUserNeighborhoodId())
                    return false;
            }
        }

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        return (!optionalUser.isPresent() || optionalUser.get().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId())
            && (channelTwoId == null || channelTwoId.getFirstId() == authHelper.getRequestingUserNeighborhoodId()); // I have to use the URI instead of the optional to avoid extra logic, channels have N:M relationship with neighborhoods
    }

    public boolean canCreatePost(String userURI, String channelURI, List<String> tagURIs) {
        LOGGER.info("Verifying Create Post Accessibility");

        // Structure Validation
        if (!URIValidator.validateURI(userURI, Endpoint.USERS) || !URIValidator.validateURI(channelURI, Endpoint.CHANNELS))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> optionalUser = us.findUser(extractFirstId(userURI));
        if (!optionalUser.isPresent())
            return false;
        TwoId channelTwoId = extractTwoId(channelURI);
        Optional<Channel> optionalChannel = chs.findChannel(channelTwoId.getFirstId(), channelTwoId.getSecondId());
        if (!optionalChannel.isPresent())
            return false;
        if (tagURIs != null && !tagURIs.isEmpty()) {
            for (String tagURI : tagURIs) {
                // Structure Validation
                if (!URIValidator.validateURI(tagURI, Endpoint.TAGS))
                    return true; // Constraints handle the error
                // Entity Existence
                TwoId twoId = extractTwoId(tagURI);
                if (!ts.findTag(twoId.getFirstId(), twoId.getSecondId()).isPresent())
                    return false;
                // Reference Authorization
                if (twoId.getFirstId() != authHelper.getRequestingUserNeighborhoodId())
                    return false;
            }
        }

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return (optionalUser.get().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId())
                && (channelTwoId.getFirstId() == authHelper.getRequestingUserNeighborhoodId()); // I have to use the URI instead of the optional to avoid extra logic, channels have N:M relationship with neighborhoods
        return (optionalUser.get().getUserId() == authHelper.getRequestingUserId())&& (channelTwoId.getFirstId() == authHelper.getRequestingUserNeighborhoodId()); // I have to use the URI instead of the optional to avoid extra logic, channels have N:M relationship with neighborhoods
    }

    public boolean canDeletePost(long postId) {
        LOGGER.info("Verifying Post Delete Accessibility");

        // Entity  Existence
        Optional<Post> optionalPost = ps.findPost(postId);
        if (!optionalPost.isPresent())
            return true; // Not Found is handled by the controller

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return optionalPost.get().getUser().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId();
        return optionalPost.get().getUser().getUserId() == authHelper.getRequestingUserId();
    }

    // ------------------------------------------------ PRODUCTS ----------------------------------------------------------

    public boolean canListProducts(String userURI, String departmentURI, String productStatusURI) {
        LOGGER.info("Verifying List Products Accessibility");

        // Structure Validation
        if (!URIValidator.validateOptionalURI(userURI, Endpoint.USERS) || !URIValidator.validateOptionalURI(departmentURI, Endpoint.DEPARTMENTS) || !URIValidator.validateOptionalURI(productStatusURI, Endpoint.PRODUCT_STATUSES))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> optionalUser = Optional.empty();
        if (userURI != null) {
            optionalUser = us.findUser(extractFirstId(userURI));
            if (!optionalUser.isPresent())
                return false;
        }
        Optional<Department> optionalDepartment;
        if (departmentURI != null) {
             optionalDepartment = ds.findDepartment(extractFirstId(departmentURI));
            if (!optionalDepartment.isPresent())
                return false;
        }
        Optional<ProductStatus> optionalProductStatus;
        if (productStatusURI != null) {
            optionalProductStatus = ProductStatus.fromId(extractFirstId(productStatusURI));
            if (!optionalProductStatus.isPresent())
                return false;
        }

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        return (!optionalUser.isPresent() || optionalUser.get().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId());
    }

    public boolean canCreateProduct(String userURI, String departmentURI) {
        LOGGER.info("Verifying Create Product Accessibility");

        // Structure Validation
        if (!URIValidator.validateURI(userURI, Endpoint.USERS) || !URIValidator.validateURI(departmentURI, Endpoint.DEPARTMENTS))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> optionalUser = us.findUser(extractFirstId(userURI));
        if (!optionalUser.isPresent())
            return false;
        Optional<Department>  optionalDepartment = ds.findDepartment(extractFirstId(departmentURI));
        if (!optionalDepartment.isPresent())
            return false;

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return (optionalUser.get().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId());
        return (optionalUser.get().getUserId() == authHelper.getRequestingUserId());
    }

    public boolean canUpdateProduct(String userURI, String departmentURI, long productId) {
        LOGGER.info("Verifying Update Product Accessibility");

        // Structure Validation
        if (!URIValidator.validateOptionalURI(userURI, Endpoint.USERS) || !URIValidator.validateOptionalURI(departmentURI, Endpoint.DEPARTMENTS))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<Product> optionalProduct = prs.findProduct(productId);
        if (!optionalProduct.isPresent())
            return true; // Not Found is handled by the controller
        Optional<User> optionalUser = Optional.empty();
        if (userURI != null) {
            optionalUser = us.findUser(extractFirstId(userURI));
            if (!optionalUser.isPresent())
                return false;
        }
        Optional<Department> optionalDepartment;
        if (departmentURI != null) {
            optionalDepartment = ds.findDepartment(extractFirstId(departmentURI));
            if (!optionalDepartment.isPresent())
                return false;
        }

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return (!optionalUser.isPresent() || optionalUser.get().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId());
        return (!optionalUser.isPresent() || optionalUser.get().getUserId() == authHelper.getRequestingUserId())
                && (optionalProduct.get().getSeller().getUserId() == authHelper.getRequestingUserId());
    }

    public boolean canDeleteProduct(long productId) {
        LOGGER.info("Verifying Delete Product Accessibility");

        // Entity Existence
        Optional<Product> optionalProduct = prs.findProduct(productId);
        if (!optionalProduct.isPresent())
            return true; // Not Found is handled by the controller

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return optionalProduct.get().getSeller().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId();
        return optionalProduct.get().getSeller().getUserId() == authHelper.getRequestingUserId();
    }

    // ---------------------------------------------- REQUESTS ---------------------------------------------------------

    public boolean canListOrCountRequests(String userURI, String productURI, String transactionTypeURI, String requestStatusURI) {
        LOGGER.info("Verifying List Requests Accessibility");

        // Structure Validation
        if (!URIValidator.validateOptionalURI(userURI, Endpoint.USERS) || !URIValidator.validateOptionalURI(productURI, Endpoint.PRODUCTS) || !URIValidator.validateOptionalURI(transactionTypeURI, Endpoint.TRANSACTION_TYPES) || !URIValidator.validateOptionalURI(requestStatusURI, Endpoint.REQUEST_STATUSES))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> optionalUser = Optional.empty();
        if (userURI != null) {
            optionalUser = us.findUser(extractFirstId(userURI));
            if (!optionalUser.isPresent())
                return false;
        }
        Optional<Product> optionalProduct = Optional.empty();
        if (productURI != null) {
            TwoId productTwoId = extractTwoId(productURI);
            optionalProduct = prs.findProduct(productTwoId.getFirstId(), productTwoId.getSecondId());
            if (!optionalProduct.isPresent())
                return false;
        }
        Optional<TransactionType> optionalTransactionType;
        if (transactionTypeURI != null) {
            optionalTransactionType = TransactionType.fromId(extractFirstId(transactionTypeURI));
            if (!optionalTransactionType.isPresent())
                return false;
        }
        Optional<RequestStatus> optionalRequestStatus;
        if (requestStatusURI != null) {
            optionalRequestStatus = RequestStatus.fromId(extractFirstId(requestStatusURI));
            if (!optionalRequestStatus.isPresent())
                return false;
        }

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return (!optionalProduct.isPresent() || optionalProduct.get().getSeller().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId())
                && (!optionalUser.isPresent() || optionalUser.get().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId());
        return (optionalProduct.isPresent() && optionalProduct.get().getSeller().getUserId() == authHelper.getRequestingUserId())
                || (optionalUser.isPresent() && optionalUser.get().getUserId() == authHelper.getRequestingUserId());
    }

    public boolean canFindRequest(long requestId) {
        LOGGER.info("Verifying Request Accessibility");

        // Entity Existence
        Optional<Request> optionalRequest = rs.findRequest(requestId);
        if (!optionalRequest.isPresent())
            return true; // Not Found is handled by the controller

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return (optionalRequest.get().getProduct().getSeller().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId())
                    && (optionalRequest.get().getUser().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId());
        return (optionalRequest.get().getProduct().getSeller().getUserId() == authHelper.getRequestingUserId())
                || (optionalRequest.get().getUser().getUserId() == authHelper.getRequestingUserId());
    }

    public boolean canCreateRequest(String userURI, String productURI) {
        LOGGER.info("Verifying Create Request Accessibility");

        // Structure Validation
        if (!URIValidator.validateURI(userURI, Endpoint.USERS) || !URIValidator.validateURI(productURI, Endpoint.PRODUCTS))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<User> optionalUser = us.findUser(extractFirstId(userURI));
        if (!optionalUser.isPresent())
            return false;
        TwoId productTwoId = extractTwoId(productURI);
        Optional<Product> optionalProduct = prs.findProduct(productTwoId.getFirstId(), productTwoId.getSecondId());
        if (!optionalProduct.isPresent())
            return false;

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return (optionalProduct.get().getSeller().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId())
                    && (optionalUser.get().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId());
        return (optionalProduct.get().getSeller().getUserId() != authHelper.getRequestingUserId())
                && (optionalUser.get().getUserId() == authHelper.getRequestingUserId());

    }

    public boolean canUpdateRequest(String userURI, String productURI, String requestStatusURI, long requestId) {
        LOGGER.info("Verifying Update Request Accessibility");

        // Structure Validation
        if (!URIValidator.validateOptionalURI(userURI, Endpoint.USERS) || !URIValidator.validateOptionalURI(productURI, Endpoint.PRODUCTS) || !URIValidator.validateOptionalURI(requestStatusURI, Endpoint.REQUEST_STATUSES))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<Request> optionalRequest = rs.findRequest(requestId);
        if (!optionalRequest.isPresent())
            return true; // Not Found is handled by the controller
        Optional<User> optionalUser = Optional.empty();
        if (userURI != null) {
            optionalUser = us.findUser(extractFirstId(userURI));
            if (!optionalUser.isPresent())
                return false;
        }
        Optional<Product> optionalProduct = Optional.empty();
        if (productURI != null) {
            TwoId productTwoId = extractTwoId(productURI);
            optionalProduct = prs.findProduct(productTwoId.getFirstId(), productTwoId.getSecondId());
            if (!optionalProduct.isPresent())
                return false;
        }
        Optional<RequestStatus> optionalRequestStatus;
        if (requestStatusURI != null) {
            optionalRequestStatus = RequestStatus.fromId(extractFirstId(requestStatusURI));
            if (!optionalRequestStatus.isPresent())
                return false;
        }

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return (!optionalProduct.isPresent() || optionalProduct.get().getSeller().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId())
                    && (!optionalUser.isPresent() || optionalUser.get().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId());
        return ((!optionalProduct.isPresent() || optionalProduct.get().getSeller().getUserId() == authHelper.getRequestingUserId())
                || (!optionalUser.isPresent() || optionalUser.get().getUserId() == authHelper.getRequestingUserId()))
                && optionalRequest.get().getProduct().getSeller().getUserId() == authHelper.getRequestingUserId();
    }

    public boolean canDeleteRequest(long requestId) {
        LOGGER.info("Verifying List Transactions Accessibility");

        // Entity Existence
        Optional<Request> optionalRequest = rs.findRequest(requestId);
        if (!optionalRequest.isPresent())
            return true; // Not Found is handled by the controller

        // Reference Authorization
        if (authHelper.isSuperAdministrator())
            return true;
        if (authHelper.isAdministrator())
            return (optionalRequest.get().getProduct().getSeller().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId())
                    && (optionalRequest.get().getUser().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId());
        return optionalRequest.get().getUser().getUserId() == authHelper.getRequestingUserId();
    }

    // ---------------------------------------------- REQUESTS ---------------------------------------------------------

    public boolean canCreateOrUpdateResource(String imageURI) {
        LOGGER.info("Verifying Create Resource Accessibility");

        // Structure Validation
        if (!URIValidator.validateOptionalURI(imageURI, Endpoint.IMAGES))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<Image> optionalImage = Optional.empty();
        if (imageURI != null) {
            optionalImage = ims.findImage(extractFirstId(imageURI));
            return optionalImage.isPresent();
        }
        return true;
    }

    // ------------------------------------------------ TAGS -----------------------------------------------------------

    public boolean canListShifts(String amenityURI) {
        LOGGER.info("Verifying List Shifts Accessibility");

        // Structure Validation
        if (!URIValidator.validateOptionalURI(amenityURI, Endpoint.AMENITIES))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<Amenity> optionalAmenity = Optional.empty();
        if (amenityURI != null) {
            TwoId amenityTwoId = extractTwoId(amenityURI);
            optionalAmenity = as.findAmenity(amenityTwoId.getFirstId(), amenityTwoId.getSecondId());
            if (!optionalAmenity.isPresent())
                return false;
        }

        // Reference Authentication
        return !optionalAmenity.isPresent() || optionalAmenity.get().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId();
    }

    // ------------------------------------------------ TAGS -----------------------------------------------------------

    public boolean canListTags(String postURI) {
        LOGGER.info("Verifying Create Tags Accessibility");

        // Structure Validation
        if (!URIValidator.validateOptionalURI(postURI, Endpoint.POSTS))
            return true; // Constraints handle the error

        // Entity Existence
        Optional<Post> optionalPost = Optional.empty();
        if (postURI != null) {
            TwoId postTwoId = extractTwoId(postURI);
            optionalPost = ps.findPost(postTwoId.getFirstId(), postTwoId.getSecondId());
            if (!optionalPost.isPresent())
                return false;
        }

        // Reference Authentication
        return !optionalPost.isPresent() || optionalPost.get().getUser().getNeighborhood().getNeighborhoodId() == authHelper.getRequestingUserNeighborhoodId();
    }
}

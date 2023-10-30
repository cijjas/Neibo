package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.interfaces.exceptions.*;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.MainEntities.Amenity;
import ar.edu.itba.paw.models.MainEntities.Image;
import ar.edu.itba.paw.webapp.form.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.Date;
import java.util.List;
import java.util.Locale;

@Controller
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private final SessionUtils sessionUtils;
    private final PostService ps;
    private final UserService us;
    private final NeighborhoodService nhs;
    private final CommentService cs;
    private final TagService ts;
    private final ChannelService chs;
    private final SubscriptionService ss;
    private final CategorizationService cas;
    private final ImageService is;
    private final AmenityService as;
    private final EventService es;
    private final ResourceService res;
    private final ContactService cos;
    private final AttendanceService ats;
    private final LikeService ls;
    private final BookingService bs;
    private final ShiftService shs;
    private final NeighborhoodWorkerService nhws;
    private final ProfessionWorkerService pws;
    private final ReviewService rws;
    private final WorkerService ws;
    private final AvailabilityService avs;

    // ------------------------------------- FEED --------------------------------------

    @Autowired
    public MainController(SessionUtils sessionUtils,
                          final PostService ps,
                          final UserService us,
                          final NeighborhoodService nhs,
                          final CommentService cs,
                          final TagService ts,
                          final ChannelService chs,
                          final SubscriptionService ss,
                          final CategorizationService cas,
                          final ImageService is,
                          final AmenityService as,
                          final EventService es,
                          final ResourceService res,
                          final ContactService cos,
                          final AttendanceService ats,
                          final LikeService ls,
                          final NeighborhoodWorkerService nhws,
                          final ProfessionWorkerService pws,
                          final ReviewService rws,
                          final WorkerService ws,
                          final BookingService bs,
                          final ShiftService shs,
                          final AvailabilityService avs
    ) {
        this.sessionUtils = sessionUtils;
        this.is = is;
        this.ps = ps;
        this.us = us;
        this.nhs = nhs;
        this.cs = cs;
        this.ts = ts;
        this.chs = chs;
        this.ss = ss;
        this.cas = cas;
        this.as = as;
        this.es = es;
        this.res = res;
        this.cos = cos;
        this.ats = ats;
        this.ls = ls;
        this.nhws = nhws;
        this.pws = pws;
        this.rws = rws;
        this.ws = ws;
        this.bs = bs;
        this.shs = shs;
        this.avs = avs;
    }

    private ModelAndView handleChannelRequest(
            String channelName,
            int page,
            int size,
            List<String> tags,
            String postStatus
    ) {

        ModelAndView mav = new ModelAndView("views/index");
        mav.addObject("tagList", ts.getTags(sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId()));
        mav.addObject("appliedTags", tags);
        mav.addObject("postList", ps.getPostsByCriteria(channelName, page, size, tags, sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId(), PostStatus.valueOf(postStatus)));
        mav.addObject("page", page);
        mav.addObject("totalPages", ps.getTotalPages(channelName, size, tags, sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId(), PostStatus.valueOf(postStatus), 0));
        mav.addObject("channel", channelName);
        mav.addObject("contextPath", "/" + channelName.toLowerCase());

        return mav;
    }

    @RequestMapping(value={"/", "/feed"})
    public ModelAndView index(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "tag", required = false) List<String> tags,
            @RequestParam(value = "postStatus", required = false, defaultValue = "none") String postStatus
    ) {
        LOGGER.info("User arriving at '/feed'");
        return handleChannelRequest(BaseChannel.FEED.toString(), page, size, tags, postStatus);
    }


    // ------------------------------------- PROFILE --------------------------------------

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView profile(
            @ModelAttribute("profilePictureForm") final ProfilePictureForm profilePictureForm
    ) {
        LOGGER.info("User arriving at '/profile'");
        ModelAndView mav = new ModelAndView("views/userProfile");
        mav.addObject("neighbor", sessionUtils.getLoggedUser());
        return mav;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ModelAndView profile(
            @Valid @ModelAttribute("profilePictureForm") final ProfilePictureForm profilePictureForm,
            final BindingResult errors
    ) {
        sessionUtils.clearLoggedUser();
        ModelAndView mav = new ModelAndView("redirect:/profile");
        if (errors.hasErrors()) {
            LOGGER.error("Error while updating profile picture");
            return profile(profilePictureForm);
        }
        us.updateProfilePicture(sessionUtils.getLoggedUser().getUserId(), profilePictureForm.getImageFile());
        return mav;
    }

    @RequestMapping(value = "/toggle-dark-mode", method = RequestMethod.POST)
    public String updateDarkModePreference() {
        sessionUtils.clearLoggedUser();
        sessionUtils.getLoggedUser();
        System.out.println("Toggle Dark Mode");
        us.toggleDarkMode(sessionUtils.getLoggedUser().getUserId());
        return "redirect:/profile";
    }

    @RequestMapping(value = "/toggle-language", method = RequestMethod.POST)
    public String changeLanguage(
            @RequestParam(value = "lang", required = false) String language,
            HttpServletRequest request
    ) {
        sessionUtils.clearLoggedUser();
        sessionUtils.getLoggedUser();
        request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, new Locale(language));
        us.toggleLanguage(sessionUtils.getLoggedUser().getUserId());
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/feed");
    }

    @RequestMapping(value = "/apply-tags-as-filter", method = RequestMethod.POST)
    public ModelAndView applyTagsFilter(
            @RequestParam("tags") String tags,
            @RequestParam("currentUrl") String currentUrl
    ) {
        return new ModelAndView("redirect:" + ts.createURLForTagFilter(tags, currentUrl, sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId()));
    }

    // ------------------------------------- ANNOUNCEMENTS --------------------------------------

    @RequestMapping("/announcements")
    public ModelAndView announcements(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "tag", required = false) List<String> tags,
            @RequestParam(value = "postStatus", required = false, defaultValue = "none") String postStatus
    ) {
        LOGGER.info("User arriving at '/announcements'");
        return handleChannelRequest(BaseChannel.ANNOUNCEMENTS.toString(), page, size, tags, postStatus);
    }

    // ------------------------------------- FORUM --------------------------------------

    @RequestMapping("/complaints")
    public ModelAndView complaints(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "tag", required = false) List<String> tags,
            @RequestParam(value = "postStatus", required = false, defaultValue = "none") String postStatus
    ) {
        LOGGER.info("User arriving at '/complaints'");
        return handleChannelRequest(BaseChannel.COMPLAINTS.toString(), page, size, tags, postStatus);
    }

    @RequestMapping(value = "/unverified", method = RequestMethod.GET)
    public ModelAndView unverified() {
        LOGGER.info("User arriving at '/unverified'");
        return new ModelAndView("views/unverified");
    }

    @RequestMapping(value = "/rejected", method = RequestMethod.GET)
    public ModelAndView rejectedForm(
            @ModelAttribute("neighborhoodForm") final NeighborhoodForm neighborhoodForm
    ) {
        LOGGER.info("User arriving at '/rejected'");
        ModelAndView mav = new ModelAndView("views/rejected");
        mav.addObject("neighborhoodsList", nhs.getNeighborhoods());
        return mav;
    }

    @RequestMapping(value = "/rejected", method = RequestMethod.POST)
    public ModelAndView rejectedForm(
            @ModelAttribute("neighborhoodForm") final NeighborhoodForm neighborhoodForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            LOGGER.error("Error in Neighborhood Form'");
            return rejectedForm(neighborhoodForm);
        }
        us.unverifyNeighbor(sessionUtils.getLoggedUser().getUserId(), neighborhoodForm.getNeighborhoodId());
        return new ModelAndView("redirect:/logout");
    }

    // ------------------------------------- PUBLISH --------------------------------------

    @RequestMapping(value = "/publish", method = RequestMethod.GET)
    public ModelAndView publishForm(
            @ModelAttribute("publishForm") final PublishForm publishForm,
            @RequestParam(value = "onChannelId", required = false) Long onChannelId
    ) {
        LOGGER.info("User arriving at '/publish'");
        final ModelAndView mav = new ModelAndView("views/publish");
        mav.addObject("tagList", ts.getTags(sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId()));
        mav.addObject("channel", onChannelId);
        mav.addObject("channelList", chs.getNeighborChannels(sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId(), sessionUtils.getLoggedUser().getUserId()));
        return mav;
    }

    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public ModelAndView publish(
            @Valid @ModelAttribute("publishForm") final PublishForm publishForm,
            final BindingResult errors,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "onChannelId", required = false) Long onChannelId
    ) {
        if (errors.hasErrors()) {
            LOGGER.error("Error in Publish Form'");
            return publishForm(publishForm, onChannelId);
        }
        ps.createPost(publishForm.getSubject(), publishForm.getMessage(), sessionUtils.getLoggedUser().getUserId(), publishForm.getChannel(), publishForm.getTags(), imageFile);
        ModelAndView mav = new ModelAndView("views/publish");
        mav.addObject("channelId", publishForm.getChannel());
        mav.addObject("showSuccessMessage", true);
        return mav;
    }


    @RequestMapping(value = "/redirect-to-channel", method = RequestMethod.POST)
    public ModelAndView redirectToChannel(
            @RequestParam("channelId") Long channelId
    ) {
        String channelName = chs.findChannelById(channelId).orElseThrow(() -> new NotFoundException("Channel not Found")).getChannel().toLowerCase();
        if (channelName.equals(BaseChannel.WORKERS.toString().toLowerCase()))
            return new ModelAndView("redirect:/services");
        else
            return new ModelAndView("redirect:/" + channelName);

    }

    @RequestMapping(value = "/publish-to-channel", method = RequestMethod.POST)
    public ModelAndView publishToChannel(
            @RequestParam("channel") String channelString
    ) {
        long channelId = chs.findChannelByName(channelString).orElseThrow(() -> new NotFoundException("Channel not Found")).getChannelId();
        return new ModelAndView("redirect:/publish?onChannelId=" + channelId);
    }

    // ------------------------------------- POSTS --------------------------------------

    @RequestMapping(value = "/posts/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView viewPost(
            @PathVariable(value = "id") int postId,
            @ModelAttribute("commentForm") final CommentForm commentForm,
            @RequestParam(value = "success", required = false) boolean success,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        LOGGER.info("User arriving at '/posts/{}", postId);

        ModelAndView mav = new ModelAndView("views/post");

        mav.addObject("post", ps.findPostById(postId).orElseThrow(() -> new NotFoundException("Post Not Found")));

        mav.addObject("comments", cs.getCommentsByPostId(postId, page, size));
        mav.addObject("page", page);
        mav.addObject("totalPages", cs.getTotalCommentPages(postId, size));
        mav.addObject("tags", ts.findTagsByPostId(postId));
        mav.addObject("commentForm", commentForm);
        mav.addObject("showSuccessMessage", success);
        mav.addObject("contextPath", "/posts/" + postId);

        return mav;
    }

    @RequestMapping(value = "/posts/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView addCommentToPost(
            @PathVariable(value = "id") int postId,
            @Valid @ModelAttribute("commentForm") final CommentForm commentForm,
            final BindingResult errors,
            @RequestParam(value = "post_page", defaultValue = "1") int postPage,
            @RequestParam(value = "post_size", defaultValue = "10") int postSize
    ) {
        if (errors.hasErrors()) {
            LOGGER.error("Error in Comment Form");
            return viewPost(postId, commentForm, false, postPage, postSize);
        }
        cs.createComment(commentForm.getComment(), sessionUtils.getLoggedUser().getUserId(), postId);
        ModelAndView mav = new ModelAndView("redirect:/posts/" + postId);
        mav.addObject("commentForm", new CommentForm());
        return mav;
    }


    // ------------------------------------- RESOURCES --------------------------------------

    @RequestMapping(value = "/images/{imageId:\\d+}")
    @ResponseBody
    public byte[] imageRetriever(
            @PathVariable long imageId
    ) {
        LOGGER.info("Retrieving image");
        return is.getImage(imageId).map(Image::getImage).orElse(null);
    }

    // ---------------------------------- LOGIN SIGNUP AND SESSION  -----------------------------------

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView logIn(
            @ModelAttribute("signupForm") final SignupForm signupform,
            @ModelAttribute("workerSignupForm") final WorkerSignupForm workerSignupForm,
            @RequestParam(value = "error", required = false, defaultValue = "false") boolean error,
            @RequestParam(value = "email", required = false) String email
    ) {
        LOGGER.info("User arriving at '/login'");
        ModelAndView mav = new ModelAndView("views/landingPage");

        mav.addObject("email", email);
        mav.addObject("professionsPairs", Professions.PROF_PAIRS);
        System.out.println("FELIX: PROFESSION PAIRS :" + Professions.PROF_PAIRS);
        mav.addObject("error", error);
        mav.addObject("neighborhoodsList", nhs.getNeighborhoods());
        mav.addObject("openSignupDialog", false);
        return mav;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView logIn(
    ) {
        return new ModelAndView("views/index");
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ModelAndView signupForm(
            @ModelAttribute("signupForm") final SignupForm signupform,
            @ModelAttribute("workerSignupForm") final WorkerSignupForm workerSignupForm,
            @RequestParam(value = "successfullySignup", required = false) boolean successfullySignup
    ) {
        LOGGER.info("User arriving at '/signup'");
        ModelAndView mav = new ModelAndView("views/landingPage");
        mav.addObject("successfullySignup", successfullySignup);
        mav.addObject("neighborhoodsList", nhs.getNeighborhoods());
        return mav;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView signupForm(
            @Valid @ModelAttribute("signupForm") final SignupForm signupForm,
            final BindingResult errors,
            @ModelAttribute("workerSignupForm") final WorkerSignupForm workerSignupForm
    ) {
        if (errors.hasErrors()) {
            LOGGER.error("Error in Sign Up Form");
            ModelAndView mav = logIn(signupForm, new WorkerSignupForm(), true, "");
            mav.addObject("openSignupDialog", true);
            return mav;
        }
        us.createNeighbor(signupForm.getMail(), signupForm.getPassword(), signupForm.getName(), signupForm.getSurname(), signupForm.getNeighborhoodId(), Language.ENGLISH, signupForm.getIdentification());
        ModelAndView mav = new ModelAndView("redirect:/signup");
        mav.addObject("successfullySignup", true);
        return mav;
    }

    @RequestMapping(value = "/signup-worker", method = RequestMethod.GET)
    public ModelAndView workerSignupForm(
            @ModelAttribute("workerSignupForm") final WorkerSignupForm workerSignupForm,
            @ModelAttribute("signupForm") final SignupForm signupForm,
            @RequestParam(value = "successfullySignup", required = false) boolean successfullySignup
    ) {
        LOGGER.info("User arriving at '/signup-worker'");

        ModelAndView mav = new ModelAndView("views/landingPage");

        mav.addObject("professionsPairs", Professions.PROF_PAIRS);
        mav.addObject("successfullySignup", successfullySignup);
        mav.addObject("neighborhoodsList", nhs.getNeighborhoods());
        return mav;
    }

    @RequestMapping(value = "/signup-worker", method = RequestMethod.POST)
    public ModelAndView workerSignupForm(
            @Valid @ModelAttribute("workerSignupForm") final WorkerSignupForm workerSignupForm,
            final BindingResult errors,
            @ModelAttribute("signupForm") final SignupForm signupForm
    ) {
        if (errors.hasErrors()) {
            LOGGER.error("Error in Sign Up Form");
            ModelAndView mav = logIn(new SignupForm(), workerSignupForm, true, "");
            mav.addObject("openWorkerSignupDialog", true);
            return mav;
        }

        ws.createWorker(workerSignupForm.getW_mail(), workerSignupForm.getW_name(), workerSignupForm.getW_surname(), workerSignupForm.getW_password(), workerSignupForm.getW_identification(), workerSignupForm.getPhoneNumber(), workerSignupForm.getAddress(), Language.ENGLISH, workerSignupForm.getProfessionIds(), workerSignupForm.getBusinessName());
        ModelAndView mav = new ModelAndView("redirect:/signup-worker");
        mav.addObject("successfullySignup", true);
        return mav;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logOut() {
        LOGGER.info("User arriving at '/logout'");
        sessionUtils.clearLoggedUser();
    }


    //------------------------------------- USER AMENITIES & RESERVATIONS --------------------------------------

    @RequestMapping(value = "/reservation", method = RequestMethod.GET)
    public ModelAndView reservation(
            @ModelAttribute("reservationTimeForm") final ReservationTimeForm reservationTimeForm,
            @RequestParam(value = "amenityId", required = false) long amenityId,
            @RequestParam(value = "date", required = false) java.sql.Date date
    ) {
        LOGGER.info("User arriving at '/reservation'");
        ModelAndView mav = new ModelAndView("views/reservation");

        mav.addObject("amenityId", amenityId);
        mav.addObject("date", date);
        mav.addObject("amenityName", as.findAmenityById(amenityId).orElseThrow(() -> new NotFoundException("Amenity not Found")).getName());
        mav.addObject("bookings", shs.getShifts(amenityId, date));
        mav.addObject("reservationsList", bs.getUserBookings(sessionUtils.getLoggedUser().getUserId()));
        return mav;
    }


    @RequestMapping(value = "/reservation", method = RequestMethod.POST)
    public ModelAndView reservation(
            @RequestParam("amenityId") Long amenityId,
            @RequestParam("date") Date date,
            @RequestParam("selectedShifts") List<Long> selectedShifts
    ) {
        bs.createBooking(sessionUtils.getLoggedUser().getUserId(), amenityId, selectedShifts, date);
        return new ModelAndView("redirect:/amenities");
    }


    @RequestMapping(value = "/amenities", method = RequestMethod.GET)
    public ModelAndView amenities(
            @ModelAttribute("reservationForm") final ReservationForm reservationForm,
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size
    ) {
        LOGGER.info("User arriving at '/amenities'");

        ModelAndView mav = new ModelAndView("views/amenities");

        System.out.println(as.getAmenities(sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId(), page, size));

        mav.addObject("totalPages", as.getTotalAmenitiesPages(sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId(), size));
        mav.addObject("daysPairs", DayOfTheWeek.DAY_PAIRS);
        mav.addObject("timesPairs", StandardTime.TIME_PAIRS);
        mav.addObject("amenities", as.getAmenities(sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId(), page, size));
        mav.addObject("channel", BaseChannel.RESERVATIONS.toString());
        mav.addObject("reservationsList", bs.getUserBookings(sessionUtils.getLoggedUser().getUserId()));
        return mav;
    }

    @RequestMapping(value = "/amenities", method = RequestMethod.POST)
    public ModelAndView amenities(
            @Valid @ModelAttribute("reservationForm") final ReservationForm reservationForm,
            final BindingResult errors,
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size
    ) {
        if (errors.hasErrors()) {
            LOGGER.error("Error in Booking Form");
            return amenities(reservationForm, page, size);
        }
        ModelAndView mav = new ModelAndView("redirect:/reservation");
        mav.addObject("amenityId", reservationForm.getAmenityId());
        mav.addObject("date", reservationForm.getDate());

        return mav;
    }

    @RequestMapping(value = "/delete-reservation", method = RequestMethod.POST)
    public ModelAndView deleteReservation(
            @RequestParam("bookingIds") List<Long> bookingIds
    ) {
        bs.deleteBookings(bookingIds);
        return new ModelAndView("redirect:/amenities");
    }

    // ------------------------------------- CALENDAR --------------------------------------

    @RequestMapping("/calendar")
    public ModelAndView calendar(
            @RequestParam(required = false, defaultValue = "0") long timestamp
    ) {
        LOGGER.info("User arriving at '/calendar'");

        Date selectedDate = new Date(timestamp != 0 ? timestamp : System.currentTimeMillis());

        ModelAndView mav = new ModelAndView("views/calendar");
        mav.addObject("isAdmin", sessionUtils.getLoggedUser().getRole() == UserRole.ADMINISTRATOR);
        mav.addObject("selectedTimestamp", selectedDate.getTime()); // Pass the selected timestamp
        mav.addObject("selectedDay", selectedDate.getDate());
        mav.addObject("selectedMonth", es.getSelectedMonth(selectedDate.getMonth(), sessionUtils.getLoggedUser().getLanguage()));
        mav.addObject("selectedYear", es.getSelectedYear(selectedDate.getYear()));
        mav.addObject("selectedDate", selectedDate);
        mav.addObject("eventList", es.getEventsByDate(selectedDate, sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId()));
        return mav;
    }

    @RequestMapping(value = "/redirect-to-site", method = RequestMethod.POST)
    public ModelAndView redirectToSite(
            @RequestParam("site") String site
    ) {
        return new ModelAndView("redirect:/" + site);
    }

    // ------------------------------------- EVENTS --------------------------------------

    @RequestMapping(value = "/events/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView viewEvent(
            @PathVariable(value = "id") int eventId,
            @RequestParam(value = "success", required = false) boolean success
    ) {
        LOGGER.info("User arriving at '/events/{}'", eventId);

        ModelAndView mav = new ModelAndView("views/event");

        mav.addObject("event", es.findEventById(eventId).orElseThrow(() -> new NotFoundException("Event not found")));
        mav.addObject("attendees", us.getEventUsers(eventId));
        mav.addObject("willAttend", us.isAttending(eventId, sessionUtils.getLoggedUser().getUserId()));
        mav.addObject("showSuccessMessage", success);

        return mav;
    }

    @RequestMapping(value = "/attend/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView attendEvent(
            @PathVariable(value = "id") int eventId
    ) {
        ModelAndView mav = new ModelAndView("redirect:/events/" + eventId);
        ats.createAttendee(sessionUtils.getLoggedUser().getUserId(), eventId);
        return mav;
    }

    @RequestMapping(value = "/unattend/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView unattendEvent(
            @PathVariable(value = "id") int eventId
    ) {
        ModelAndView mav = new ModelAndView("redirect:/events/" + eventId);
        ats.deleteAttendee(sessionUtils.getLoggedUser().getUserId(), eventId);
        return mav;
    }

    // ------------------------------------- INFORMATION --------------------------------
    @RequestMapping(value = "/information", method = RequestMethod.GET)
    public ModelAndView information() {
        LOGGER.info("User arriving at '/information'");

        ModelAndView mav = new ModelAndView("views/information");
        mav.addObject("resourceList", res.getResources(sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId()));
        mav.addObject("phoneNumbersList", cos.getContacts(sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId()));
        mav.addObject("channel", BaseChannel.INFORMATION.toString());
        mav.addObject("resourceMap", res.getResources(sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId()));
        mav.addObject("phoneNumbersMap", cos.getContacts(sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId()));
        return mav;
    }

    // ------------------------------------- EXCEPTIONS --------------------------------------

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView notFound(RuntimeException ex) {
        LOGGER.info("Not Found Exception was Thrown", ex);
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "404");
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }

    @ExceptionHandler({DuplicateKeyException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView duplicated(RuntimeException ex) {
        LOGGER.info("Duplicate Key Exception was Thrown", ex);
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "409"); // 409 = Conflict
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(InsertionException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView insertion(RuntimeException ex) {
        LOGGER.info("Insertion Exception was Thrown", ex);
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "500");
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(MailingException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView mailing(RuntimeException ex) {
        LOGGER.info("Mailing Exception was Thrown", ex);
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "500");
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(UnexpectedException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView unexpected(RuntimeException ex) {
        LOGGER.info("Unexpected Exception was Thrown", ex);
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "500");
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public void test(){

    }
}

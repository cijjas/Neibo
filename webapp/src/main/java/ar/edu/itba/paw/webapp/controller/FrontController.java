package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.interfaces.exceptions.*;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.form.*;
import ar.edu.itba.paw.webapp.form.ReservationTimeForm;
import ar.edu.itba.paw.webapp.form.ReviewForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.sql.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Controller
public class FrontController {

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

    @Autowired
    public FrontController(SessionUtils sessionUtils,
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

    // ------------------------------------- FEED --------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontController.class);

    private ModelAndView handleChannelRequest(
            String channelName,
            int page,
            int size,
            List<String> tags,
            String postStatus
    ) {
        List<Post> postList = ps.getPostsByCriteria(channelName, page, size, tags, sessionUtils.getLoggedUser().getNeighborhoodId(), postStatus);
        int totalPages = ps.getTotalPages(channelName, size, tags, sessionUtils.getLoggedUser().getNeighborhoodId(), postStatus, 0);

        String contextPath;

        if (channelName.equals(BaseChannel.FEED.toString())) {
            contextPath = "";
        } else {
            contextPath = "/" + channelName.toLowerCase();
        }

        ModelAndView mav = new ModelAndView("views/index");
        mav.addObject("tagList", ts.getTags(sessionUtils.getLoggedUser().getNeighborhoodId()));
        mav.addObject("appliedTags", tags);
        mav.addObject("postList", postList);
        mav.addObject("page", page);
        mav.addObject("totalPages", totalPages);
        mav.addObject("channel", channelName);
        mav.addObject("contextPath", contextPath);

        return mav;
    }

    @RequestMapping("/")
    public ModelAndView index(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "tag", required = false) List<String> tags,
            @RequestParam(value = "postStatus", required = false, defaultValue = "none") String postStatus
    ) {
        LOGGER.info("Registered a new user under the id {}", sessionUtils.getLoggedUser().getUserId());

        return handleChannelRequest(BaseChannel.FEED.toString(), page, size, tags, postStatus);
    }


    // ------------------------------------- PROFILE --------------------------------------

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView profile(
            @ModelAttribute("profilePictureForm") final ProfilePictureForm profilePictureForm
    ) {
        ModelAndView mav = new ModelAndView("views/userProfile");
        mav.addObject("neighbor", sessionUtils.getLoggedUser());
        return mav;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ModelAndView profile(
            @Valid @ModelAttribute("profilePictureForm") final ProfilePictureForm profilePictureForm,
            final BindingResult errors
    ) {
        ModelAndView mav = new ModelAndView("redirect:/profile");
        if (errors.hasErrors()) {
            LOGGER.error("Error while updating profile picture");
            return profile(profilePictureForm);
        }
        us.updateProfilePicture(sessionUtils.getLoggedUser().getUserId(), profilePictureForm.getImageFile());
        return mav;
    }

    @RequestMapping (value = "/update-darkmode-preference", method = RequestMethod.POST)
    public String updateDarkModePreference() {
        sessionUtils.clearLoggedUser();
        us.toggleDarkMode(sessionUtils.getLoggedUser().getUserId());
        return "redirect:/profile";
    }

    @RequestMapping (value = "/change-language", method = RequestMethod.POST)
    public String changeLanguage(
            @RequestParam(value="lang", required = false) String language,
            HttpServletRequest request
    ) {
        User user = sessionUtils.getLoggedUser();
        Locale locale = new Locale(language);
        request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
        us.toggleLanguage(user.getUserId());
        // Redirect back to the previous page or a specific page
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @RequestMapping(value = "/apply-tags-as-filter", method = RequestMethod.POST)
    public ModelAndView applyTagsFilter(
            @RequestParam("tags") String tags,
            @RequestParam("currentUrl") String currentUrl
    ) {
        return new ModelAndView("redirect:" + ts.createURLForTagFilter(tags, currentUrl, sessionUtils.getLoggedUser().getNeighborhoodId()));
    }

    // ------------------------------------- ANNOUNCEMENTS --------------------------------------

    @RequestMapping("/announcements")
    public ModelAndView announcements(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "tag", required = false) List<String> tags,
            @RequestParam(value = "postStatus", required = false, defaultValue = "none") String postStatus
    ) {
        return handleChannelRequest(BaseChannel.ANNOUNCEMENTS.toString(), page, size, tags, postStatus);
    }

    // ------------------------------------- FORUM --------------------------------------

    @RequestMapping("/complaints")
    public ModelAndView complaints(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "tag", required = false) List<String> tags,
            @RequestParam(value = "postStatus", required = false, defaultValue = "none") String postStatus
    ){
        return handleChannelRequest(BaseChannel.COMPLAINTS.toString(), page, size, tags, postStatus);
    }

    @RequestMapping(value = "/unverified", method = RequestMethod.GET)
    public ModelAndView unverified() {
        return  new ModelAndView("views/unverified");
    }

    @RequestMapping(value = "/rejected", method = RequestMethod.GET)
    public ModelAndView rejectedForm(
            @ModelAttribute("neighborhoodForm") final NeighborhoodForm neighborhoodForm
    ) {
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
        final ModelAndView mav = new ModelAndView("views/publish");

        mav.addObject("channel", onChannelId);
        mav.addObject("channelList", chs.getNeighborChannels(sessionUtils.getLoggedUser().getNeighborhoodId(), sessionUtils.getLoggedUser().getUserId()));
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
            return publishForm(publishForm, onChannelId);
        }
        Integer channelId = publishForm.getChannel();

        ps.createPost(publishForm.getSubject(), publishForm.getMessage(), sessionUtils.getLoggedUser().getUserId(), channelId, publishForm.getTags(), imageFile);
        ModelAndView mav = new ModelAndView("views/publish");
        mav.addObject("channelId", channelId);
        mav.addObject("showSuccessMessage", true);
        return mav;
    }


    @RequestMapping(value = "/redirect-to-channel", method = RequestMethod.POST)
    public ModelAndView redirectToChannel(
            @RequestParam("channelId") int channelId
    ) {
        String channelName= chs.findChannelById(channelId).orElseThrow(()-> new NotFoundException("Channel not Found")).getChannel().toLowerCase();
        if(channelName.equals(BaseChannel.FEED.toString().toLowerCase())){
            return new ModelAndView("redirect:/");
        }
        else {
            return new ModelAndView("redirect:/" + channelName);
        }
    }

    @RequestMapping(value = "/publish-to-channel", method = RequestMethod.POST)
    public ModelAndView publishToChannel(
            @RequestParam("channel") String channelString
    ) {
        long channelId = chs.findChannelByName(channelString).orElseThrow(()-> new NotFoundException("Channel not Found")).getChannelId();
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
        ModelAndView mav = new ModelAndView("views/post");

        Optional<Post> optionalPost = ps.findPostById(postId);
        mav.addObject("post", optionalPost.orElseThrow(() -> new NotFoundException("Post Not Found")));

        String contextPath = "/posts/" + postId;

        List<Comment> commentList = cs.findCommentsByPostId(postId, page, size);
        int totalPages = cs.getTotalPostPages(postId, size);

        mav.addObject("comments", commentList);
        mav.addObject("page", page);
        mav.addObject("totalPages", totalPages);

        List<Tag> tags = ts.findTagsByPostId(postId);

        mav.addObject("tags", tags);
        mav.addObject("commentForm", commentForm);
        mav.addObject("showSuccessMessage", success);
        mav.addObject("contextPath", contextPath);

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
            return viewPost(postId, commentForm, false, postPage, postSize);
        }
        cs.createComment(commentForm.getComment(), sessionUtils.getLoggedUser().getUserId(), postId);
        ModelAndView mav = new ModelAndView("redirect:/posts/" + postId);
        mav.addObject("commentForm", new CommentForm());
        return mav;
    }


    // ------------------------------------- RESOURCES --------------------------------------

    @RequestMapping(value = "/images/{imageId}")
    @ResponseBody
    public byte[] imageRetriever(
            @PathVariable long imageId
    ) {
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
        ModelAndView mav = new ModelAndView("views/landingPage");
        List<Pair<Integer, String>> professionsPairs = new ArrayList<>();
        for (Professions profession : Professions.values()){
            professionsPairs.add(new Pair<>(profession.getId(), profession.toString()));
        }

        mav.addObject("email", email);
        mav.addObject("professionsPairs", professionsPairs);
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
            ModelAndView mav =  logIn(signupForm, new WorkerSignupForm(), true, "");
            mav.addObject("openSignupDialog", true);
            return mav;
        }
        int identification = 0;
        try {
            identification = Integer.parseInt(signupForm.getIdentification());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        us.createNeighbor(signupForm.getMail(), signupForm.getPassword(), signupForm.getName(), signupForm.getSurname(), signupForm.getNeighborhoodId(), Language.ENGLISH, identification);
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
        ModelAndView mav = new ModelAndView("views/landingPage");


        List<Pair<Integer, String>> professionsPairs = new ArrayList<>();
        for (Professions profession : Professions.values()){
            professionsPairs.add(new Pair<>(profession.getId(), profession.toString()));
        }

        mav.addObject("professionsPairs", professionsPairs);
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
            ModelAndView mav = logIn(new SignupForm(), workerSignupForm, true, "");
            mav.addObject("openWorkerSignupDialog", true);
            return mav;
        }
        int identification = 0;
        try {
            identification = Integer.parseInt(workerSignupForm.getW_identification());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        ws.createWorker(workerSignupForm.getW_mail(), workerSignupForm.getW_name(), workerSignupForm.getW_surname(), workerSignupForm.getW_password(), identification, workerSignupForm.getPhoneNumber(), workerSignupForm.getAddress(), Language.ENGLISH, workerSignupForm.getProfessionIds(), workerSignupForm.getBusinessName());
        ModelAndView mav = new ModelAndView("redirect:/signup-worker");
        mav.addObject("successfullySignup", true);
        return mav;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logOut() {
        sessionUtils.clearLoggedUser();
    }


    //------------------------------------- USER AMENITIES & RESERVATIONS --------------------------------------

    @RequestMapping(value = "/reservation", method = RequestMethod.GET)
    public ModelAndView reservation(
            @ModelAttribute("reservationTimeForm") final ReservationTimeForm reservationTimeForm,
            @RequestParam(value = "amenityId", required = false) long amenityId,
            @RequestParam(value = "date", required = false) java.sql.Date date
    ) {
        ModelAndView mav = new ModelAndView("views/reservation");

        mav.addObject("amenityId", amenityId);
        mav.addObject("date", date);
        mav.addObject("amenityName", as.findAmenityById(amenityId).orElseThrow(()-> new NotFoundException("Amenity not Found")).getName());
        mav.addObject("bookings", shs.getShifts(amenityId,date));
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
            @ModelAttribute("reservationForm") final ReservationForm reservationForm
    ) {

        ModelAndView mav = new ModelAndView("views/amenities");
        List<Amenity> amenities = as.getAmenities(sessionUtils.getLoggedUser().getNeighborhoodId());


        mav.addObject("daysPairs", DayOfTheWeek.DAY_PAIRS);
        mav.addObject("timesPairs", StandardTime.TIME_PAIRS);
        mav.addObject("amenities", amenities);
        mav.addObject("channel", BaseChannel.RESERVATIONS.toString());
        mav.addObject("reservationsList", bs.getUserBookings(sessionUtils.getLoggedUser().getUserId()));
        return mav;
    }

    @RequestMapping(value = "/amenities", method = RequestMethod.POST)
    public ModelAndView amenities(
            @Valid @ModelAttribute("reservationForm") final ReservationForm reservationForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return amenities(reservationForm);
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

        Date selectedDate = new Date(timestamp != 0 ? timestamp : System.currentTimeMillis());

        List<Event> eventList = es.getEventsByDate(selectedDate, sessionUtils.getLoggedUser().getNeighborhoodId());

        // Get the selected day, month (word), and year directly from selectedDate
        int selectedDay = selectedDate.getDate(); // getDate() returns the day of the month
        String selectedMonth = es.getSelectedMonth(selectedDate.getMonth(), sessionUtils.getLoggedUser().getLanguage());
        int selectedYear = es.getSelectedYear(selectedDate.getYear());

        ModelAndView mav = new ModelAndView("views/calendar");
        mav.addObject("isAdmin", sessionUtils.getLoggedUser().getRole() == UserRole.ADMINISTRATOR);
        mav.addObject("selectedTimestamp", selectedDate.getTime()); // Pass the selected timestamp
        mav.addObject("selectedDay", selectedDay);
        mav.addObject("selectedMonth", selectedMonth);
        mav.addObject("selectedYear", selectedYear);
        mav.addObject("selectedDate", selectedDate );
        mav.addObject("eventList", eventList);
        return mav;
    }

    @RequestMapping(value = "/redirect-to-site", method = RequestMethod.POST)
    public ModelAndView redirectToSite(
            @RequestParam("site") String site
    ) {
        return new ModelAndView("redirect:/" + site);
    }

    // ------------------------------------- POSTS --------------------------------------

    @RequestMapping(value = "/events/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView viewEvent(
            @PathVariable(value = "id") int eventId,
            @RequestParam(value = "success", required = false) boolean success
    ) {
        ModelAndView mav = new ModelAndView("views/event");

        Optional<Event> optionalEvent = es.findEventById(eventId);
        mav.addObject("event", optionalEvent.orElseThrow(() -> new NotFoundException("Event not found")));
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
        ModelAndView mav = new ModelAndView("views/information");
        mav.addObject("resourceList", res.getResources(sessionUtils.getLoggedUser().getNeighborhoodId()));
        mav.addObject("phoneNumbersList", cos.getContacts(sessionUtils.getLoggedUser().getNeighborhoodId()));
        mav.addObject("channel", BaseChannel.INFORMATION.toString());
        mav.addObject("resourceMap", res.getResources(sessionUtils.getLoggedUser().getNeighborhoodId()));
        mav.addObject("phoneNumbersMap", cos.getContacts(sessionUtils.getLoggedUser().getNeighborhoodId()));
        return mav;
    }


    // ------------------------------------- EXCEPTIONS --------------------------------------

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView notFound(RuntimeException ex) {
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "404");
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }

    @ExceptionHandler({DuplicateKeyException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView duplicated(RuntimeException ex) {
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "409"); // 409 = Conflict
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }

    @ExceptionHandler({InsertionException.class, MailingException.class})
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView insertion(RuntimeException ex) {
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "500");
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }


    // ------------------------------------- TEST --------------------------------------



    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ModelAndView test() {
//        Random random = new Random();
//        long[] jobNumbers = {1, 2, 3, 4}; // Define the possible job numbers
//        for (int i = 5; i < 35; i++) {
//            String email = "worker" + i + "@test.com";
//            String name = "WorkerName" + i;
//            String surname = "WorkerSurname" + i;
//            String password = "password";
//            int identificationNumber = 1000000 + i; // Starting from 1000000
//            String phoneNumber = "PhoneNumber" + i;
//            String address = "Address" + i;
//            Language language = Language.ENGLISH;
//
//            // Generate a random number of jobs for the worker (between 1 and 4)
//            int numJobs = random.nextInt(4) + 1;
//
//            // Create an array to store the job numbers for this worker
//            long[] workerJobNumbers = new long[numJobs];
//
//            // Generate random job numbers for this worker
//            for (int j = 0; j < numJobs; j++) {
//                int randomIndex = random.nextInt(jobNumbers.length);
//                workerJobNumbers[j] = jobNumbers[randomIndex];
//            }
//
//            // Create the worker with multiple jobs
//            Worker worker = ws.createWorker(email, name, surname, password, identificationNumber, phoneNumber, address, language, workerJobNumbers, "BusinessName");
//
//            // Add the worker to a neighborhood (assuming neighborhood ID is 1)
//            nhws.addWorkerToNeighborhood(worker.getUser().getUserId(), 1);
//        }
//
//
//        ps.createWorkerPost("This is a second test posttt", "Alrighty Aphrodite", 29, null);

        /*// System.out.println(bs.createBooking(););
        System.out.println("Shifts on Tueday 2023-10-10 for the Swimming Pool");
        System.out.println(shs.getShifts(1, DayOfTheWeek.Tuesday.getId(),Date.valueOf("2023-10-10")));

        System.out.println("Overall All the Shifts for the Swimming Pool particular day (Monday)");
        System.out.println(shs.getShifts(1, 1));

        System.out.println("Enum check");
        System.out.println(DayOfTheWeek.Thursday.getId()); // 4 hopefully
        System.out.println(StandardTime.TIME_06_30.getId()); // 14 hopefully
        // values of the array are obtained through the Shift object, when the user chooses a specific shift time, it has
        // a mapping to a shiftId, those are the values expected to be received in the arrayList
        System.out.println("User bookings 23");
        System.out.println(bs.getUserBookings(23));

        System.out.println("User bookings 25");
        System.out.println(bs.getUserBookings(25));

        System.out.println("Create Booking");
        bs.createBooking(23, 1, new ArrayList<>(Arrays.asList(28L, 29L, 30L)), Date.valueOf("2023-10-10"));
        */

        /*
        System.out.println(shs.getAmenityShifts(1));
        avs.updateAvailability(1, new ArrayList<>(Arrays.asList(1L, 2L, 3L)));
        System.out.println(shs.getAmenityShifts(1));
        avs.updateAvailability(1, new ArrayList<>(Arrays.asList(4L, 5L, 6L)));
        System.out.println(shs.getAmenityShifts(1));
         */

        return new ModelAndView("views/index");
    }

    @RequestMapping(value = "/admin/test", method = RequestMethod.GET)
    public ModelAndView adminTest() {

        return new ModelAndView("admin/views/adminRequestHandler");
    }

    @RequestMapping(value = "/testDuplicatedException", method = RequestMethod.GET)
    public ModelAndView testDuplicatedException() {
        throw new DuplicateKeyException("testing wubbawubba");
    }

    @RequestMapping(value = "/testNotFoundException", method = RequestMethod.GET)
    public ModelAndView testNotFoundException() {
        throw new NotFoundException("testing wubabidabi");
    }

    @RequestMapping(value = "/testException", method = RequestMethod.GET)
    public ModelAndView testException() {
        throw new InsertionException("An error occurred whilst creating the User");
    }

    /*------------------------------------------SERVICES --------------------------------------*/


    @RequestMapping(value = "/service/profile/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView serviceProfile(
            @ModelAttribute("reviewForm") final ReviewForm reviewForm,
            @PathVariable(value = "id") long workerId,
            @ModelAttribute("editWorkerProfileForm") final EditWorkerProfileForm editWorkerProfileForm
    ) {
        ModelAndView mav = new ModelAndView("serviceProvider/views/serviceProfile");
        Optional<Worker> optionalWorker = ws.findWorkerById(workerId);

        //List<Post> postList = ps.getWorkerPostsByCriteria(BaseChannel.WORKERS.toString(), 1, 10,null, 0, null,workerId);
        //int totalPages = ps.getTotalPages(BaseChannel.WORKERS.toString(), 10, null, 0, null, workerId);

        mav.addObject("worker", optionalWorker.orElseThrow(() -> new NotFoundException("Worker not found")));
        mav.addObject("professions", pws.getWorkerProfessions(workerId));
        mav.addObject("reviews", rws.getReviews(workerId));
        mav.addObject("reviewsCount", rws.getReviewsCount(workerId));
        mav.addObject("averageRating", rws.getAvgRating(workerId).orElseThrow(() -> new NotFoundException("Average Rating not found")));
        //mav.addObject("postList", postList);
        //mav.addObject("totalPages", totalPages);
        return mav;
    }
    @RequestMapping(value = "/service/profile/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView serviceProfile(
    ) {
        return new ModelAndView("serviceProvider/views/serviceProfile");
    }

    @RequestMapping(value = "/service/profile/{id:\\d+}/review", method = RequestMethod.GET)
    public ModelAndView createReview(
            @ModelAttribute("reviewForm") final ReviewForm reviewForm,
            @PathVariable(value = "id") int workerId,
            @ModelAttribute("editWorkerProfileForm") final EditWorkerProfileForm editWorkerProfileForm
    ) {
        ModelAndView mav = new ModelAndView("serviceProvider/views/serviceProfile");
        Optional<Worker> optionalWorker = ws.findWorkerById(workerId);

        mav.addObject("worker", optionalWorker.orElseThrow(() -> new NotFoundException("Worker not found")));
        mav.addObject("professions", pws.getWorkerProfessions(workerId));
        mav.addObject("reviews", rws.getReviews(workerId));
        mav.addObject("reviewsCount", rws.getReviewsCount(workerId));
        mav.addObject("averageRating", rws.getAvgRating(workerId).orElseThrow(() -> new NotFoundException("Average Rating not found")));
        return mav;

    }

    @RequestMapping(value = "/service/profile/{id:\\d+}/review", method = RequestMethod.POST)
    public ModelAndView createReview(
            @Valid @ModelAttribute("reviewForm") final ReviewForm reviewForm,
            final BindingResult errors,
            @PathVariable(value = "id") int workerId,
            @ModelAttribute("editWorkerProfileForm") final EditWorkerProfileForm editWorkerProfileForm
    ) {
        if(errors.hasErrors()) {
            ModelAndView mav = serviceProfile(reviewForm, workerId, new EditWorkerProfileForm());
            mav.addObject("openReviewDialog", true);
            return mav;
        }

        rws.createReview(workerId, sessionUtils.getLoggedUser().getUserId(), reviewForm.getRating(), reviewForm.getReview());

        return new ModelAndView("redirect:/service/profile/" + workerId);
    }

    @RequestMapping(value = "/service/profile/edit", method = RequestMethod.GET)
    public ModelAndView editProfile(
            @ModelAttribute("reviewForm") final ReviewForm reviewForm,
            @ModelAttribute("editWorkerProfileForm") final EditWorkerProfileForm editWorkerProfileForm
    ) {
        ModelAndView mav = new ModelAndView("serviceProvider/views/serviceProfile");
        long workerId = sessionUtils.getLoggedUser().getUserId();
        Optional<Worker> optionalWorker = ws.findWorkerById(workerId);

        mav.addObject("worker", optionalWorker.orElseThrow(() -> new NotFoundException("Worker not found")));
        mav.addObject("professions", pws.getWorkerProfessions(workerId));
        mav.addObject("reviews", rws.getReviews(workerId));
        mav.addObject("reviewsCount", rws.getReviewsCount(workerId));
        mav.addObject("averageRating", rws.getAvgRating(workerId).orElseThrow(() -> new NotFoundException("Average Rating not found")));
        return mav;

    }

    @RequestMapping(value = "/service/profile/edit", method = RequestMethod.POST)
    public ModelAndView editProfile(
//            @ModelAttribute("reviewForm") final ReviewForm reviewForm,
            @Valid @ModelAttribute("editWorkerProfileForm") final EditWorkerProfileForm editWorkerProfileForm,
            final BindingResult errors
    ) {
        long workerId = sessionUtils.getLoggedUser().getUserId();
        if(errors.hasErrors()) {
            ModelAndView mav = serviceProfile(new ReviewForm(), workerId, editWorkerProfileForm);
            mav.addObject("openEditProfileDialog", true);
        }

        ws.updateWorker(workerId, editWorkerProfileForm.getPhoneNumber(), editWorkerProfileForm.getAddress(),
                editWorkerProfileForm.getBusinessName(), editWorkerProfileForm.getImageFile(), editWorkerProfileForm.getBio());

        return new ModelAndView("redirect:/service/profile/" + workerId);
    }

    @RequestMapping(value = "/services", method = RequestMethod.GET)
    public ModelAndView services() {
        ModelAndView mav = new ModelAndView("serviceProvider/views/services");
        System.out.println(ws.getWorkersByCriteria(1,10, null, sessionUtils.getLoggedUser().getNeighborhoodId()));
        List<Worker> workerList = ws.getWorkersByCriteria(1,10, null, sessionUtils.getLoggedUser().getNeighborhoodId());
        mav.addObject("workersList", workerList);
        return mav;
    }

    @RequestMapping(value = "/services/neighborhoods", method = RequestMethod.GET)
    public ModelAndView workersNeighborhoods(
            @ModelAttribute("neighborhoodForm") final NeighborhoodForm neighborhoodForm
    ) {
        ModelAndView mav = new ModelAndView("serviceProvider/views/neighborhoods");
        long workerId = sessionUtils.getLoggedUser().getUserId();
        mav.addObject("associatedNeighborhoods", nhws.getNeighborhoods(workerId));
        mav.addObject("otherNeighborhoods", nhws.getOtherNeighborhoods(workerId));
        System.out.println("associated hoods: " + nhws.getNeighborhoods(workerId));
        System.out.println("other hoods: " + nhws.getOtherNeighborhoods(workerId));
        return mav;
    }

    @RequestMapping(value = "/services/neighborhoods", method = RequestMethod.POST)
    public ModelAndView addWorkerToNeighborhood(
            @Valid @ModelAttribute("neighborhoodForm") final NeighborhoodForm neighborhoodForm,
            final BindingResult errors
    ) {
        if(errors.hasErrors()) {
            return workersNeighborhoods(neighborhoodForm);
        }
        long workerId = sessionUtils.getLoggedUser().getUserId();
        nhws.addWorkerToNeighborhood(workerId, neighborhoodForm.getNeighborhoodId());
        return new ModelAndView("redirect:/services/neighborhoods");
    }

    @RequestMapping(value = "/services/neighborhoods/remove/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView removeWorkerFromNeighborhood(
            @PathVariable(value = "id") long neighborhoodId
    ) {
        long workerId = sessionUtils.getLoggedUser().getUserId();
        nhws.removeWorkerFromNeighborhood(workerId, neighborhoodId);
        return new ModelAndView("redirect:/services/neighborhoods");
    }
}

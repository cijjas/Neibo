package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exceptions.*;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.form.*;
import ar.edu.itba.paw.webapp.form.validation.ReservationTimeForm;
import ar.edu.itba.paw.webapp.form.validation.ReviewForm;
import enums.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.sql.Date;
import java.util.stream.Collectors;

import ar.edu.itba.paw.models.Time;
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
    private final ReservationService rs;
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
                           final ReservationService rs,
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
                           final ShiftService shs) {
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
        this.rs = rs;
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
    }

    // ------------------------------------- FEED --------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontController.class);

    private ModelAndView handleChannelRequest(
            String channelName,
            int page,
            int size,
            SortOrder date,
            List<String> tags
    ) {
        List<Post> postList = ps.getPostsByCriteria(channelName, page, size, date, tags, sessionUtils.getLoggedUser().getNeighborhoodId());
        int totalPages = ps.getTotalPages(channelName, size, tags, sessionUtils.getLoggedUser().getNeighborhoodId());

        ModelAndView mav = new ModelAndView("views/index");
        mav.addObject("tagList", ts.getTags(sessionUtils.getLoggedUser().getNeighborhoodId()));
        mav.addObject("appliedTags", tags);
        mav.addObject("postList", postList);
        mav.addObject("page", page);
        mav.addObject("totalPages", totalPages);
        mav.addObject("channel", channelName);

        return mav;
    }

    @RequestMapping("/")
    public ModelAndView index(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "date", defaultValue = "DESC", required = false) SortOrder date,
            @RequestParam(value = "tag", required = false) List<String> tags
    ) {
        LOGGER.info("Registered a new user under the id {}", sessionUtils.getLoggedUser().getUserId());

        return handleChannelRequest(BaseChannel.FEED.toString(), page, size, date, tags);
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
        User user = sessionUtils.getLoggedUser();
        us.toggleDarkMode(user.getUserId());
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
            @RequestParam(value = "date", defaultValue = "DESC", required = false) SortOrder date,
            @RequestParam(value = "tag", required = false) List<String> tags
    ) {
        return handleChannelRequest(BaseChannel.ANNOUNCEMENTS.toString(), page, size, date, tags);
    }

    // ------------------------------------- FORUM --------------------------------------

    @RequestMapping("/complaints")
    public ModelAndView complaints(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "date", defaultValue = "DESC", required = false) SortOrder date,
            @RequestParam(value = "tag", required = false) List<String> tags
    ){
        return handleChannelRequest(BaseChannel.COMPLAINTS.toString(), page, size, date, tags);
    }

    @RequestMapping(value = "/unverified", method = RequestMethod.GET)
    public ModelAndView unverified() {
        return  new ModelAndView("views/unverified");
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

        Post p = ps.createPost(publishForm.getSubject(), publishForm.getMessage(), sessionUtils.getLoggedUser().getUserId(), channelId, publishForm.getTags(), imageFile);
        ModelAndView mav = new ModelAndView("views/publish");
        mav.addObject("channelId", channelId);
        mav.addObject("showSuccessMessage", true);
        return mav;
    }


    @RequestMapping(value = "/redirect-to-channel", method = RequestMethod.POST)
    public ModelAndView redirectToChannel(
            @RequestParam("channelId") int channelId
    ) {
        String channelName= chs.findChannelById(channelId).get().getChannel().toLowerCase();
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
        long channelId = chs.findChannelByName(channelString).get().getChannelId();
        return new ModelAndView("redirect:/publish?onChannelId=" + channelId);
    }

    // ------------------------------------- POSTS --------------------------------------

    @RequestMapping(value = "/posts/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView viewPost(
            @PathVariable(value = "id") int postId,
            @ModelAttribute("commentForm") final CommentForm commentForm,
            @RequestParam(value = "success", required = false) boolean success
    ) {
        ModelAndView mav = new ModelAndView("views/post");

        Optional<Post> optionalPost = ps.findPostById(postId);
        mav.addObject("post", optionalPost.orElseThrow(() -> new NotFoundException("Post Not Found")));

        Optional<List<Comment>> optionalComments = cs.findCommentsByPostId(postId);
        mav.addObject("comments", optionalComments.orElse(Collections.emptyList()));

        Optional<List<Tag>> optionalTags = ts.findTagsByPostId(postId);
        List<Tag> tags = optionalTags.orElse(Collections.emptyList());

        mav.addObject("tags", tags);
        mav.addObject("commentForm", commentForm);
        mav.addObject("showSuccessMessage", success);

        return mav;
    }

    @RequestMapping(value = "/posts/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView viewPost(
            @PathVariable(value = "id") int postId,
            @Valid @ModelAttribute("commentForm") final CommentForm commentForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return viewPost(postId, commentForm, false);
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
            Model model,
            @ModelAttribute("signupForm") final SignupForm signupform,
            @RequestParam(value = "error", required = false, defaultValue = "false") boolean error
    ) {
        model.addAttribute("neighbor", new User.Builder());
        ModelAndView mav = new ModelAndView("views/landingPage");
        mav.addObject("error", error);
        mav.addObject("neighborhoodsList", nhs.getNeighborhoods());
        mav.addObject("openSignupDialog", false);
        return mav;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView logIn(
            @RequestParam("mail") String mail,
            @RequestParam("password") String password
    ) {
        return new ModelAndView("views/index");
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ModelAndView signupForm(
            @ModelAttribute("signupForm") final SignupForm signupform,
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
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            ModelAndView mav = signupForm(signupForm, false);
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

   

    //------------------------------------- USER AMENITIES & RESERVATIONS --------------------------------------

    @RequestMapping(value = "/reservation", method = RequestMethod.GET)
    public ModelAndView reservation(
            @ModelAttribute("reservationTimeForm") final ReservationTimeForm reservationTimeForm,
            @RequestParam(value = "amenityId", required = false) long amenityId,
            @RequestParam(value = "date", required = false) java.sql.Date date
    ) {
        ModelAndView mav = new ModelAndView("views/reservation");
        mav.addObject("amenityId", amenityId);
        mav.addObject("amenityName", as.findAmenityById(amenityId).orElse(null).getName());
        mav.addObject("date", date);
        mav.addObject("reservationsList", rs.getReservationsByDay(amenityId, date));
        mav.addObject("timeList", rs.getAvailableTimesByDate(amenityId, date));
        return mav;
    }

    @RequestMapping(value = "/reservation", method = RequestMethod.POST)
    public ModelAndView reservation(@Valid @ModelAttribute("reservationTimeForm") final ReservationTimeForm reservationTimeForm,
                                    final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return reservation(reservationTimeForm, reservationTimeForm.getAmenityId(), reservationTimeForm.getDate());
        }
        ModelAndView mav = new ModelAndView("redirect:/amenities");
        Reservation res = rs.createReservation(reservationTimeForm.getAmenityId(), sessionUtils.getLoggedUser().getUserId(), reservationTimeForm.getDate(), reservationTimeForm.getStartTime(), reservationTimeForm.getEndTime(), sessionUtils.getLoggedUser().getNeighborhoodId());
        mav.addObject(res == null ? "showErrorMessage" : "showSuccessMessage", true);

        return mav;
    }

    @RequestMapping(value = "/amenities", method = RequestMethod.GET)
    public ModelAndView amenities(
            @ModelAttribute("reservationForm") final ReservationForm reservationForm
    ) {
        ModelAndView mav = new ModelAndView("views/amenities");
        mav.addObject("channel", BaseChannel.RESERVATIONS.toString());

        List<Amenity> amenities = as.getAmenities(sessionUtils.getLoggedUser().getNeighborhoodId());

        List<AmenityHours> amenityHoursList = new ArrayList<>();
        for (Amenity amenity : amenities) {
            Map<String, DayTime> amenityTimes = as.getAmenityHoursByAmenityId(amenity.getAmenityId());
            AmenityHours amenityHours = new AmenityHours.Builder().amenity(amenity).amenityHours(amenityTimes).build();
            amenityHoursList.add(amenityHours);
        }
        mav.addObject("amenitiesHours", amenityHoursList);
        mav.addObject("daysOfWeek", rs.getDaysOfWeek());
        mav.addObject("reservationsList", rs.getReservationsByUserId(sessionUtils.getLoggedUser().getUserId()));
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

    @RequestMapping(value = "/delete-reservation/{id}", method = RequestMethod.GET)
    public ModelAndView deleteReservation( @PathVariable(value = "id") int reservationId) {
        rs.deleteReservation(reservationId);
        return new ModelAndView("redirect:/amenities");
    }

    // ------------------------------------- CALENDAR --------------------------------------

    @RequestMapping("/calendar")
    public ModelAndView calendar(
            @RequestParam(required = false, defaultValue = "0") long timestamp) {

        Date selectedDate = new Date(timestamp != 0 ? timestamp : System.currentTimeMillis());


        List<Event> eventList = es.getEventsByDate(selectedDate, sessionUtils.getLoggedUser().getNeighborhoodId());

        // Define arrays for month names in English and Spanish
        String[] monthsEnglish = {
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
        };

        String[] monthsSpanish = {
                "enero", "febrero", "marzo", "abril",
                "mayo", "junio", "julio", "agosto",
                "septiembre", "octubre", "noviembre", "diciembre"
        };

        // Get the selected day, month (word), and year directly from selectedDate
        int selectedDay = selectedDate.getDate(); // getDate() returns the day of the month
        int selectedMonthIndex = selectedDate.getMonth(); // getMonth() returns the month as 0-based index
        String selectedMonth = sessionUtils.getLoggedUser().getLanguage() == Language.ENGLISH
                ? monthsEnglish[selectedMonthIndex]
                : monthsSpanish[selectedMonthIndex];
        int selectedYear = selectedDate.getYear() + 1900; // getYear() returns years since 1900

        ModelAndView mav = new ModelAndView("views/calendar");
        mav.addObject("isAdmin", sessionUtils.getLoggedUser().getRole() == UserRole.ADMINISTRATOR);
        mav.addObject("selectedTimestamp", selectedDate.getTime()); // Pass the selected timestamp
        mav.addObject("selectedDay", selectedDay);
        mav.addObject("selectedMonth", selectedMonth);
        mav.addObject("selectedYear", selectedYear);
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
    public ModelAndView attendEvent(@PathVariable(value = "id") int eventId) {
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

    @ExceptionHandler({InsertionException.class})
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView insertion(RuntimeException ex) {
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "500");
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }


    // ------------------------------------- TEST --------------------------------------

    @RequestMapping(value = "/testAmenityCreation", method = RequestMethod.GET)
    public ModelAndView amenities1() {
        ModelAndView mav = new ModelAndView("views/amenities3");

        // Create lists of pairs for DaysOfTheWeek and StandardTime
        List<Pair<Integer, String>> daysPairs = new ArrayList<>();
        List<Pair<Integer, String>> timesPairs = new ArrayList<>();

        for (DayOfTheWeek day : DayOfTheWeek.values())
            daysPairs.add(new Pair<>(day.getId(), day.name()));


        for (StandardTime time : StandardTime.values())
            timesPairs.add(new Pair<>(time.getId(), time.toString()));


        mav.addObject("daysPairs", daysPairs);
        mav.addObject("timesPairs", timesPairs);

        return mav;
    }




    @RequestMapping(value = "/testAmenityCreation", method = RequestMethod.POST)
    public ModelAndView amenities2(
            @RequestParam("selectedShifts") List<String> selectedShifts
    ) {
        System.out.println(selectedShifts);
        as.createAmenity("New Amenity", "This is a great new amenity", 1, selectedShifts);
        return new ModelAndView("redirect:/");
    }


    @RequestMapping(value = "/testAmenityBooking", method = RequestMethod.GET)
    public ModelAndView booking() {
        ModelAndView mav = new ModelAndView("views/amenities2");
        int amenityId = 1;

        // supongo que ya sabemos la fecha y el amenity id
        mav.addObject("bookingDate", Date.valueOf("2023-10-10"));
        mav.addObject("amenityId", amenityId);
        mav.addObject("bookings", shs.getShifts(amenityId, DayOfTheWeek.Tuesday.getId(),Date.valueOf("2023-10-10")));
        mav.addObject(Date.valueOf("2023-10-10"));
        return mav;
    }

    @RequestMapping(value = "/testAmenityBooking", method = RequestMethod.POST)
    public ModelAndView booking2(
            @RequestParam("amenityId") Long amenityId,
            @RequestParam("date") Date date,
            @RequestParam("selectedShifts") List<Long> selectedShifts
    ) {
        System.out.println(selectedShifts);
        int userId = 23; // where tha fuck is getLoggedUser()?
        bs.createBooking(userId, amenityId, selectedShifts, date);
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ModelAndView test() {


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


        return new ModelAndView("views/testView");
    }

    @RequestMapping(value = "/admin/test", method = RequestMethod.GET)
    public ModelAndView adminTest() {
        ModelAndView mav = new ModelAndView("admin/views/requestManager");
        return mav;
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

    @RequestMapping(value = "/service/profile/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView serviceProfile(@ModelAttribute("reviewForm") final ReviewForm reviewForm,
            @PathVariable(value = "id") int workerId
    ) {
        ModelAndView mav = new ModelAndView("serviceProvider/views/serviceProfile");
        Optional<Worker> optionalWorker = ws.findWorkerById(workerId);

        mav.addObject("worker", optionalWorker.orElseThrow(() -> new NotFoundException("Worker not found")));
        mav.addObject("profession", pws.getWorkerProfession(workerId));
        mav.addObject("reviews", rws.getReviews(workerId));
        mav.addObject("reviewsCount", rws.getReviewsCount(workerId));
        mav.addObject("averageRating", rws.getAvgRating(workerId));
        return mav;
    }

    @RequestMapping(value = "/services", method = RequestMethod.GET)
    public ModelAndView services() {
        ModelAndView mav = new ModelAndView("serviceProvider/views/services");
        List<Worker> workerList = ws.getWorkersByCriteria(1,10, null, sessionUtils.getLoggedUser().getNeighborhoodId());
        mav.addObject("workersList", workerList);
        return mav;
    }

}
